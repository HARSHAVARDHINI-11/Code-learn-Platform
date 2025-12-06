const express = require('express');
const router = express.Router();
const auth = require('../middleware/auth');
const Post = require('../models/Post');
const User = require('../models/User');

// @route   GET /api/posts
// @desc    Get all posts
// @access  Public
router.get('/', async (req, res) => {
  try {
    const { language, difficulty, search, sortBy } = req.query;
    let query = {};

    if (language) query.language = language;
    if (difficulty) query.difficulty = difficulty;
    if (search) {
      query.$or = [
        { title: { $regex: search, $options: 'i' } },
        { problem: { $regex: search, $options: 'i' } }
      ];
    }

    let posts = Post.find(query).populate('author', 'name email college codingScore');

    // Sorting
    if (sortBy === 'popular') {
      posts = posts.sort({ likes: -1, views: -1 });
    } else if (sortBy === 'recent') {
      posts = posts.sort({ createdAt: -1 });
    } else {
      posts = posts.sort({ createdAt: -1 });
    }

    posts = await posts;
    res.json(posts);
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

// @route   GET /api/posts/:id
// @desc    Get post by ID
// @access  Public
router.get('/:id', async (req, res) => {
  try {
    const post = await Post.findById(req.params.id).populate('author', 'name email college codingScore bio');
    
    if (!post) {
      return res.status(404).json({ message: 'Post not found' });
    }

    // Increment views
    post.views += 1;
    await post.save();

    res.json(post);
  } catch (err) {
    console.error(err.message);
    if (err.kind === 'ObjectId') {
      return res.status(404).json({ message: 'Post not found' });
    }
    res.status(500).json({ message: 'Server error' });
  }
});

// @route   POST /api/posts
// @desc    Create a post
// @access  Private
router.post('/', auth, async (req, res) => {
  const { title, problem, code, language, tags, difficulty } = req.body;

  try {
    const newPost = new Post({
      author: req.user.id,
      title,
      problem,
      code,
      language,
      tags: tags || [],
      difficulty: difficulty || 'Medium'
    });

    const post = await newPost.save();

    // Update user's coding score
    const user = await User.findById(req.user.id);
    user.codingScore += 10; // Award points for posting
    await user.save();

    const populatedPost = await Post.findById(post.id).populate('author', 'name email college codingScore');
    res.json(populatedPost);
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

// @route   PUT /api/posts/:id
// @desc    Update a post
// @access  Private
router.put('/:id', auth, async (req, res) => {
  const { title, problem, code, language, tags, difficulty } = req.body;

  try {
    let post = await Post.findById(req.params.id);

    if (!post) {
      return res.status(404).json({ message: 'Post not found' });
    }

    // Check if user owns the post
    if (post.author.toString() !== req.user.id) {
      return res.status(401).json({ message: 'User not authorized' });
    }

    post.title = title || post.title;
    post.problem = problem || post.problem;
    post.code = code || post.code;
    post.language = language || post.language;
    post.tags = tags || post.tags;
    post.difficulty = difficulty || post.difficulty;

    await post.save();

    const populatedPost = await Post.findById(post.id).populate('author', 'name email college codingScore');
    res.json(populatedPost);
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

// @route   DELETE /api/posts/:id
// @desc    Delete a post
// @access  Private
router.delete('/:id', auth, async (req, res) => {
  try {
    const post = await Post.findById(req.params.id);

    if (!post) {
      return res.status(404).json({ message: 'Post not found' });
    }

    // Check if user owns the post
    if (post.author.toString() !== req.user.id) {
      return res.status(401).json({ message: 'User not authorized' });
    }

    await post.deleteOne();
    res.json({ message: 'Post deleted' });
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

// @route   PUT /api/posts/:id/like
// @desc    Like/Unlike a post
// @access  Private
router.put('/:id/like', auth, async (req, res) => {
  try {
    const post = await Post.findById(req.params.id);

    if (!post) {
      return res.status(404).json({ message: 'Post not found' });
    }

    // Check if post is already liked
    const likeIndex = post.likes.findIndex(like => like.toString() === req.user.id);

    if (likeIndex > -1) {
      // Unlike
      post.likes.splice(likeIndex, 1);
    } else {
      // Like
      post.likes.push(req.user.id);
      
      // Award points to post author
      const author = await User.findById(post.author);
      author.codingScore += 2;
      await author.save();
    }

    await post.save();
    res.json(post);
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

// @route   GET /api/posts/user/:userId
// @desc    Get posts by user
// @access  Public
router.get('/user/:userId', async (req, res) => {
  try {
    const posts = await Post.find({ author: req.params.userId })
      .populate('author', 'name email college codingScore')
      .sort({ createdAt: -1 });
    
    res.json(posts);
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: 'Server error' });
  }
});

module.exports = router;
