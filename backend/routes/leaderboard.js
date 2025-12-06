const express = require('express');
const router = express.Router();
const auth = require('../middleware/auth');
const User = require('../models/User');
const Group = require('../models/Group');

// @route   GET /api/leaderboard/college
// @desc    Get college-level leaderboard
// @access  Private
router.get('/college', auth, async (req, res) => {
  try {
    const user = await User.findById(req.user.id);
    
    // Get top users from same college
    const leaderboard = await User.find({ college: user.college })
      .select('name email college department year codingScore')
      .sort({ codingScore: -1 })
      .limit(100);

    // Find current user's rank
    const userRank = leaderboard.findIndex(u => u.id === req.user.id) + 1;

    res.json({
      leaderboard,
      currentUser: {
        ...user.toObject(),
        rank: userRank || 'Not Ranked'
      }
    });
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

// @route   GET /api/leaderboard/global
// @desc    Get global leaderboard
// @access  Private
router.get('/global', auth, async (req, res) => {
  try {
    const leaderboard = await User.find()
      .select('name email college department year codingScore')
      .sort({ codingScore: -1 })
      .limit(100);

    const user = await User.findById(req.user.id);
    const userRank = leaderboard.findIndex(u => u.id === req.user.id) + 1;

    res.json({
      leaderboard,
      currentUser: {
        ...user.toObject(),
        rank: userRank || 'Not Ranked'
      }
    });
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

// @route   GET /api/leaderboard/groups
// @desc    Get group leaderboard
// @access  Private
router.get('/groups', auth, async (req, res) => {
  try {
    const groups = await Group.find()
      .select('name groupScore members')
      .populate('members.user', 'name codingScore')
      .sort({ groupScore: -1 })
      .limit(50);

    res.json(groups);
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

// @route   GET /api/leaderboard/department/:department
// @desc    Get department-level leaderboard
// @access  Private
router.get('/department/:department', auth, async (req, res) => {
  try {
    const user = await User.findById(req.user.id);
    
    const leaderboard = await User.find({ 
      college: user.college,
      department: req.params.department
    })
      .select('name email college department year codingScore')
      .sort({ codingScore: -1 })
      .limit(100);

    const userRank = leaderboard.findIndex(u => u.id === req.user.id) + 1;

    res.json({
      leaderboard,
      currentUser: {
        ...user.toObject(),
        rank: userRank || 'Not Ranked'
      }
    });
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

module.exports = router;
