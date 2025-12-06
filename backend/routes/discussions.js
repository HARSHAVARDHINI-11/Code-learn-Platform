const express = require('express');
const router = express.Router();
const auth = require('../middleware/auth');
const Discussion = require('../models/Discussion');
const Post = require('../models/Post');
const User = require('../models/User');

// @route   GET /api/discussions/:postId
// @desc    Get discussion for a post
// @access  Public
router.get('/:postId', async (req, res) => {
  try {
    let discussion = await Discussion.findOne({ post: req.params.postId })
      .populate('comments.user', 'name email codingScore college')
      .populate('comments.replies.user', 'name email');

    if (!discussion) {
      discussion = await Discussion.create({ post: req.params.postId, comments: [] });
    }

    res.json(discussion);
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

// @route   POST /api/discussions/:postId/comment
// @desc    Add a comment to discussion
// @access  Private
router.post('/:postId/comment', auth, async (req, res) => {
  const { content, code, language } = req.body;

  try {
    let discussion = await Discussion.findOne({ post: req.params.postId });

    if (!discussion) {
      discussion = new Discussion({ post: req.params.postId, comments: [] });
    }

    discussion.comments.push({
      user: req.user.id,
      content,
      code: code || '',
      language: language || ''
    });

    await discussion.save();

    // Award points for participation
    const user = await User.findById(req.user.id);
    user.codingScore += 5;
    await user.save();

    discussion = await Discussion.findById(discussion.id)
      .populate('comments.user', 'name email codingScore college')
      .populate('comments.replies.user', 'name email');

    res.json(discussion);
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

// @route   POST /api/discussions/:postId/comment/:commentId/reply
// @desc    Reply to a comment
// @access  Private
router.post('/:postId/comment/:commentId/reply', auth, async (req, res) => {
  const { content } = req.body;

  try {
    const discussion = await Discussion.findOne({ post: req.params.postId });

    if (!discussion) {
      return res.status(404).json({ message: 'Discussion not found' });
    }

    const comment = discussion.comments.id(req.params.commentId);

    if (!comment) {
      return res.status(404).json({ message: 'Comment not found' });
    }

    comment.replies.push({
      user: req.user.id,
      content
    });

    await discussion.save();

    // Award points for participation
    const user = await User.findById(req.user.id);
    user.codingScore += 3;
    await user.save();

    const updatedDiscussion = await Discussion.findById(discussion.id)
      .populate('comments.user', 'name email codingScore college')
      .populate('comments.replies.user', 'name email');

    res.json(updatedDiscussion);
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

// @route   PUT /api/discussions/:postId/comment/:commentId/like
// @desc    Like/Unlike a comment
// @access  Private
router.put('/:postId/comment/:commentId/like', auth, async (req, res) => {
  try {
    const discussion = await Discussion.findOne({ post: req.params.postId });

    if (!discussion) {
      return res.status(404).json({ message: 'Discussion not found' });
    }

    const comment = discussion.comments.id(req.params.commentId);

    if (!comment) {
      return res.status(404).json({ message: 'Comment not found' });
    }

    const likeIndex = comment.likes.findIndex(like => like.toString() === req.user.id);

    if (likeIndex > -1) {
      comment.likes.splice(likeIndex, 1);
    } else {
      comment.likes.push(req.user.id);
      
      // Award points to comment author
      const author = await User.findById(comment.user);
      author.codingScore += 1;
      await author.save();
    }

    await discussion.save();

    const updatedDiscussion = await Discussion.findById(discussion.id)
      .populate('comments.user', 'name email codingScore college')
      .populate('comments.replies.user', 'name email');

    res.json(updatedDiscussion);
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

// @route   DELETE /api/discussions/:postId/comment/:commentId
// @desc    Delete a comment
// @access  Private
router.delete('/:postId/comment/:commentId', auth, async (req, res) => {
  try {
    const discussion = await Discussion.findOne({ post: req.params.postId });

    if (!discussion) {
      return res.status(404).json({ message: 'Discussion not found' });
    }

    const comment = discussion.comments.id(req.params.commentId);

    if (!comment) {
      return res.status(404).json({ message: 'Comment not found' });
    }

    // Check if user owns the comment
    if (comment.user.toString() !== req.user.id) {
      return res.status(401).json({ message: 'Not authorized' });
    }

    comment.deleteOne();
    await discussion.save();

    res.json({ message: 'Comment deleted' });
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

module.exports = router;
