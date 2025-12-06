import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import './Posts.css';

const Posts = () => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({
    language: '',
    difficulty: '',
    search: '',
    sortBy: 'recent'
  });

  useEffect(() => {
    fetchPosts();
  }, [filters]);

  const fetchPosts = async () => {
    try {
      const params = new URLSearchParams();
      if (filters.language) params.append('language', filters.language);
      if (filters.difficulty) params.append('difficulty', filters.difficulty);
      if (filters.search) params.append('search', filters.search);
      if (filters.sortBy) params.append('sortBy', filters.sortBy);

      const res = await axios.get(`/api/posts?${params.toString()}`);
      setPosts(res.data);
    } catch (err) {
      console.error('Error fetching posts:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (e) => {
    setFilters({ ...filters, [e.target.name]: e.target.value });
  };

  return (
    <div className="page posts-page">
      <div className="page-header">
        <div>
          <h1 className="page-title">Coding Solutions</h1>
          <p className="page-subtitle">Explore and learn from community solutions</p>
        </div>
        <Link to="/create-post" className="btn btn-primary">Create Post</Link>
      </div>

      <div className="filters-bar">
        <input
          type="text"
          name="search"
          placeholder="Search posts..."
          value={filters.search}
          onChange={handleFilterChange}
          className="form-control search-input"
        />
        <select name="language" value={filters.language} onChange={handleFilterChange} className="form-control">
          <option value="">All Languages</option>
          <option value="JavaScript">JavaScript</option>
          <option value="Python">Python</option>
          <option value="Java">Java</option>
          <option value="C++">C++</option>
          <option value="C">C</option>
          <option value="Go">Go</option>
          <option value="Rust">Rust</option>
          <option value="TypeScript">TypeScript</option>
        </select>
        <select name="difficulty" value={filters.difficulty} onChange={handleFilterChange} className="form-control">
          <option value="">All Levels</option>
          <option value="Easy">Easy</option>
          <option value="Medium">Medium</option>
          <option value="Hard">Hard</option>
        </select>
        <select name="sortBy" value={filters.sortBy} onChange={handleFilterChange} className="form-control">
          <option value="recent">Most Recent</option>
          <option value="popular">Most Popular</option>
        </select>
      </div>

      {loading ? (
        <div className="flex-center" style={{ minHeight: '40vh' }}>
          <div className="spinner"></div>
        </div>
      ) : (
        <div className="posts-grid">
          {posts.length > 0 ? (
            posts.map(post => (
              <Link key={post._id} to={`/posts/${post._id}`} className="post-card">
                <div className="post-card-header">
                  <h3 className="post-title">{post.title}</h3>
                  <span className={`badge badge-${post.difficulty === 'Easy' ? 'success' : post.difficulty === 'Medium' ? 'warning' : 'danger'}`}>
                    {post.difficulty}
                  </span>
                </div>
                <p className="post-problem">{post.problem.substring(0, 150)}...</p>
                <div className="post-meta">
                  <span className="badge badge-primary">{post.language}</span>
                  <div className="post-stats">
                    <span>👁️ {post.views}</span>
                    <span>❤️ {post.likes.length}</span>
                  </div>
                </div>
                <div className="post-author">
                  <div className="author-avatar">
                    {post.author.name.charAt(0).toUpperCase()}
                  </div>
                  <div>
                    <p className="author-name">{post.author.name}</p>
                    <p className="author-college">{post.author.college}</p>
                  </div>
                </div>
              </Link>
            ))
          ) : (
            <div className="empty-state">No posts found. Try adjusting your filters.</div>
          )}
        </div>
      )}
    </div>
  );
};

export default Posts;
