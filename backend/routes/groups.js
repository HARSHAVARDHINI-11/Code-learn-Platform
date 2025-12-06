const express = require('express');
const router = express.Router();
const auth = require('../middleware/auth');
const Group = require('../models/Group');
const User = require('../models/User');
const crypto = require('crypto');

// @route   GET /api/groups
// @desc    Get all groups (user's groups)
// @access  Private
router.get('/', auth, async (req, res) => {
  try {
    const groups = await Group.find({ 'members.user': req.user.id })
      .populate('creator', 'name email')
      .populate('members.user', 'name email codingScore')
      .sort({ createdAt: -1 });
    
    res.json(groups);
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

// @route   GET /api/groups/all
// @desc    Get all public groups
// @access  Private
router.get('/all', auth, async (req, res) => {
  try {
    const groups = await Group.find({ isPrivate: false })
      .populate('creator', 'name email')
      .populate('members.user', 'name email codingScore')
      .sort({ createdAt: -1 });
    
    res.json(groups);
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

// @route   GET /api/groups/:id
// @desc    Get group by ID
// @access  Private
router.get('/:id', auth, async (req, res) => {
  try {
    const group = await Group.findById(req.params.id)
      .populate('creator', 'name email college')
      .populate('members.user', 'name email codingScore college department');

    if (!group) {
      return res.status(404).json({ message: 'Group not found' });
    }

    res.json(group);
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

// @route   POST /api/groups
// @desc    Create a group
// @access  Private
router.post('/', auth, async (req, res) => {
  const { name, description, allowedEmails, isPrivate } = req.body;

  try {
    // Generate unique invite code
    const inviteCode = crypto.randomBytes(6).toString('hex');

    const newGroup = new Group({
      name,
      description,
      creator: req.user.id,
      inviteCode,
      allowedEmails: allowedEmails || [],
      isPrivate: isPrivate || false,
      members: [{
        user: req.user.id,
        role: 'admin'
      }]
    });

    const group = await newGroup.save();

    // Add group to user's groups
    await User.findByIdAndUpdate(req.user.id, {
      $push: { groups: group.id }
    });

    const populatedGroup = await Group.findById(group.id)
      .populate('creator', 'name email')
      .populate('members.user', 'name email codingScore');

    res.json(populatedGroup);
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

// @route   POST /api/groups/:id/join
// @desc    Join a group
// @access  Private
router.post('/:id/join', auth, async (req, res) => {
  const { inviteCode } = req.body;

  try {
    const group = await Group.findById(req.params.id);

    if (!group) {
      return res.status(404).json({ message: 'Group not found' });
    }

    // Check if already a member
    const isMember = group.members.some(member => member.user.toString() === req.user.id);
    if (isMember) {
      return res.status(400).json({ message: 'Already a member of this group' });
    }

    // Check invite code if private
    if (group.isPrivate && group.inviteCode !== inviteCode) {
      return res.status(400).json({ message: 'Invalid invite code' });
    }

    // Check allowed emails if specified
    const user = await User.findById(req.user.id);
    if (group.allowedEmails.length > 0 && !group.allowedEmails.includes(user.email)) {
      return res.status(400).json({ message: 'Your email is not allowed to join this group' });
    }

    // Add member
    group.members.push({
      user: req.user.id,
      role: 'member'
    });

    await group.save();

    // Add group to user's groups
    await User.findByIdAndUpdate(req.user.id, {
      $push: { groups: group.id }
    });

    const populatedGroup = await Group.findById(group.id)
      .populate('creator', 'name email')
      .populate('members.user', 'name email codingScore');

    res.json(populatedGroup);
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

// @route   DELETE /api/groups/:id/leave
// @desc    Leave a group
// @access  Private
router.delete('/:id/leave', auth, async (req, res) => {
  try {
    const group = await Group.findById(req.params.id);

    if (!group) {
      return res.status(404).json({ message: 'Group not found' });
    }

    // Check if user is creator
    if (group.creator.toString() === req.user.id) {
      return res.status(400).json({ message: 'Creator cannot leave the group. Delete it instead.' });
    }

    // Remove member
    group.members = group.members.filter(member => member.user.toString() !== req.user.id);
    await group.save();

    // Remove group from user's groups
    await User.findByIdAndUpdate(req.user.id, {
      $pull: { groups: group.id }
    });

    res.json({ message: 'Left the group successfully' });
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

// @route   DELETE /api/groups/:id
// @desc    Delete a group
// @access  Private
router.delete('/:id', auth, async (req, res) => {
  try {
    const group = await Group.findById(req.params.id);

    if (!group) {
      return res.status(404).json({ message: 'Group not found' });
    }

    // Check if user is creator
    if (group.creator.toString() !== req.user.id) {
      return res.status(401).json({ message: 'Not authorized' });
    }

    // Remove group from all members' groups array
    await User.updateMany(
      { groups: group.id },
      { $pull: { groups: group.id } }
    );

    await group.deleteOne();
    res.json({ message: 'Group deleted' });
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

module.exports = router;
