const mongoose = require('mongoose');

const ContestSchema = new mongoose.Schema({
  title: {
    type: String,
    required: true,
    trim: true
  },
  description: {
    type: String,
    required: true
  },
  creator: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  participatingGroups: [{
    group: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Group'
    },
    score: {
      type: Number,
      default: 0
    }
  }],
  problems: [{
    title: String,
    description: String,
    difficulty: {
      type: String,
      enum: ['Easy', 'Medium', 'Hard']
    },
    points: Number,
    testCases: [{
      input: String,
      output: String
    }]
  }],
  startTime: {
    type: Date,
    required: true
  },
  endTime: {
    type: Date,
    required: true
  },
  duration: {
    type: Number, // in minutes
    required: true
  },
  status: {
    type: String,
    enum: ['upcoming', 'ongoing', 'completed'],
    default: 'upcoming'
  },
  submissions: [{
    user: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    },
    group: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Group'
    },
    problem: Number,
    code: String,
    language: String,
    score: Number,
    submittedAt: Date
  }],
  createdAt: {
    type: Date,
    default: Date.now
  }
});

module.exports = mongoose.model('Contest', ContestSchema);
