import React, { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import axios from 'axios';
import './CreatePost.css';

const CreatePost = () => {
  const [formData, setFormData] = useState({
    title: '',
    problem: '',
    code: '',
    language: 'JavaScript',
    difficulty: 'Medium',
    tags: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();

  const { title, problem, code, language, difficulty, tags } = formData;

  const onChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const tagsArray = tags.split(',').map(tag => tag.trim()).filter(tag => tag);
      const res = await axios.post('/api/posts', {
        title,
        problem,
        code,
        language,
        difficulty,
        tags: tagsArray
      });
      navigate(`/posts/${res.data._id}`);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create post');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page create-post-page">
      <div className="page-header">
        <h1 className="page-title">Share Your Solution</h1>
        <p className="page-subtitle">Help others learn from your coding solution</p>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <form onSubmit={onSubmit} className="create-post-form">
        <div className="form-group">
          <label className="form-label">Title</label>
          <input
            type="text"
            name="title"
            value={title}
            onChange={onChange}
            className="form-control"
            placeholder="e.g., Two Sum Problem Solution"
            required
          />
        </div>

        <div className="form-group">
          <label className="form-label">Problem Description</label>
          <textarea
            name="problem"
            value={problem}
            onChange={onChange}
            className="form-control"
            placeholder="Describe the problem you're solving..."
            required
            rows="5"
          />
        </div>

        <div className="form-group">
          <label className="form-label">Your Code Solution</label>
          <textarea
            name="code"
            value={code}
            onChange={onChange}
            className="form-control code-textarea"
            placeholder="Paste your code here..."
            required
            rows="15"
          />
        </div>

        <div className="form-row">
          <div className="form-group">
            <label className="form-label">Programming Language</label>
            <select name="language" value={language} onChange={onChange} className="form-control" required>
              <option value="JavaScript">JavaScript</option>
              <option value="Python">Python</option>
              <option value="Java">Java</option>
              <option value="C++">C++</option>
              <option value="C">C</option>
              <option value="Go">Go</option>
              <option value="Rust">Rust</option>
              <option value="TypeScript">TypeScript</option>
              <option value="Ruby">Ruby</option>
              <option value="PHP">PHP</option>
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Difficulty Level</label>
            <select name="difficulty" value={difficulty} onChange={onChange} className="form-control" required>
              <option value="Easy">Easy</option>
              <option value="Medium">Medium</option>
              <option value="Hard">Hard</option>
            </select>
          </div>
        </div>

        <div className="form-group">
          <label className="form-label">Tags (comma-separated)</label>
          <input
            type="text"
            name="tags"
            value={tags}
            onChange={onChange}
            className="form-control"
            placeholder="e.g., arrays, dynamic-programming, sorting"
          />
        </div>

        <div className="form-actions">
          <button type="button" onClick={() => navigate('/posts')} className="btn btn-secondary">
            Cancel
          </button>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Publishing...' : 'Publish Post'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default CreatePost;
