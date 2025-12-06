const express = require('express');
const router = express.Router();
const auth = require('../middleware/auth');
const Contest = require('../models/Contest');
const Group = require('../models/Group');
const User = require('../models/User');

// @route   GET /api/contests
// @desc    Get all contests
// @access  Private
router.get('/', auth, async (req, res) => {
  try {
    const contests = await Contest.find()
      .populate('creator', 'name email')
      .populate('participatingGroups.group', 'name')
      .sort({ startTime: -1 });
    
    res.json(contests);
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

// @route   GET /api/contests/:id
// @desc    Get contest by ID
// @access  Private
router.get('/:id', auth, async (req, res) => {
  try {
    const contest = await Contest.findById(req.params.id)
      .populate('creator', 'name email')
      .populate('participatingGroups.group', 'name members')
      .populate('submissions.user', 'name email')
      .populate('submissions.group', 'name');

    if (!contest) {
      return res.status(404).json({ message: 'Contest not found' });
    }

    res.json(contest);
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

// @route   POST /api/contests
// @desc    Create a contest
// @access  Private
router.post('/', auth, async (req, res) => {
  const { title, description, participatingGroups, problems, startTime, duration } = req.body;

  try {
    const endTime = new Date(new Date(startTime).getTime() + duration * 60000);

    const newContest = new Contest({
      title,
      description,
      creator: req.user.id,
      participatingGroups: participatingGroups.map(groupId => ({
        group: groupId,
        score: 0
      })),
      problems,
      startTime,
      endTime,
      duration,
      status: new Date() > new Date(startTime) ? 'ongoing' : 'upcoming'
    });

    const contest = await newContest.save();

    const populatedContest = await Contest.findById(contest.id)
      .populate('creator', 'name email')
      .populate('participatingGroups.group', 'name');

    res.json(populatedContest);
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

// @route   POST /api/contests/:id/submit
// @desc    Submit solution to contest
// @access  Private
router.post('/:id/submit', auth, async (req, res) => {
  const { problemIndex, code, language } = req.body;

  try {
    const contest = await Contest.findById(req.params.id);

    if (!contest) {
      return res.status(404).json({ message: 'Contest not found' });
    }

    // Check if contest is ongoing
    const now = new Date();
    if (now < contest.startTime || now > contest.endTime) {
      return res.status(400).json({ message: 'Contest is not currently active' });
    }

    // Find user's group
    const user = await User.findById(req.user.id);
    const userGroup = contest.participatingGroups.find(pg => 
      user.groups.some(g => g.toString() === pg.group.toString())
    );

    if (!userGroup) {
      return res.status(400).json({ message: 'You are not part of any participating group' });
    }

    // Calculate score (simplified scoring)
    const problem = contest.problems[problemIndex];
    const score = problem.points || 100;

    // Add submission
    contest.submissions.push({
      user: req.user.id,
      group: userGroup.group,
      problem: problemIndex,
      code,
      language,
      score,
      submittedAt: new Date()
    });

    // Update group score
    userGroup.score += score;

    // Update user coding score
    user.codingScore += score;
    await user.save();

    // Update group total score
    const group = await Group.findById(userGroup.group);
    group.groupScore += score;
    await group.save();

    await contest.save();

    res.json({ message: 'Submission successful', score });
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

// @route   PUT /api/contests/:id/status
// @desc    Update contest status
// @access  Private
router.put('/:id/status', auth, async (req, res) => {
  try {
    const contest = await Contest.findById(req.params.id);

    if (!contest) {
      return res.status(404).json({ message: 'Contest not found' });
    }

    const now = new Date();
    
    if (now < contest.startTime) {
      contest.status = 'upcoming';
    } else if (now >= contest.startTime && now <= contest.endTime) {
      contest.status = 'ongoing';
    } else {
      contest.status = 'completed';
    }

    await contest.save();
    res.json(contest);
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

// @route   DELETE /api/contests/:id
// @desc    Delete a contest
// @access  Private
router.delete('/:id', auth, async (req, res) => {
  try {
    const contest = await Contest.findById(req.params.id);

    if (!contest) {
      return res.status(404).json({ message: 'Contest not found' });
    }

    // Check if user is creator
    if (contest.creator.toString() !== req.user.id) {
      return res.status(401).json({ message: 'Not authorized' });
    }

    await contest.deleteOne();
    res.json({ message: 'Contest deleted' });
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

module.exports = router;
