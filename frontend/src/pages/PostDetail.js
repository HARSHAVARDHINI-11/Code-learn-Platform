import React, { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import axios from 'axios';
import './PostDetail.css';

const PostDetail = () => {
  const { id } = useParams();
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();
  const [post, setPost] = useState(null);
  const [discussion, setDiscussion] = useState(null);
  const [comment, setComment] = useState({ content: '', code: '', language: '' });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchPost();
    fetchDiscussion();
  }, [id]);

  const fetchPost = async () => {
    try {
      const res = await axios.get(`/api/posts/${id}`);
      setPost(res.data);
    } catch (err) {
      console.error('Error fetching post:', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchDiscussion = async () => {
    try {
      const res = await axios.get(`/api/discussions/${id}`);
      setDiscussion(res.data);
    } catch (err) {
      console.error('Error fetching discussion:', err);
    }
  };

  const handleLike = async () => {
    try {
      const res = await axios.put(`/api/posts/${id}/like`);
      setPost(res.data);
    } catch (err) {
      console.error('Error liking post:', err);
    }
  };

  const handleAddComment = async (e) => {
    e.preventDefault();
    try {
      const res = await axios.post(`/api/discussions/${id}/comment`, comment);
      setDiscussion(res.data);
      setComment({ content: '', code: '', language: '' });
    } catch (err) {
      console.error('Error adding comment:', err);
    }
  };

  const handleDelete = async () => {
    if (window.confirm('Are you sure you want to delete this post?')) {
      try {
        await axios.delete(`/api/posts/${id}`);
        navigate('/posts');
      } catch (err) {
        console.error('Error deleting post:', err);
      }
    }
  };

  if (loading) {
    return (
      <div className="page">
        <div className="flex-center" style={{ minHeight: '60vh' }}>
          <div className="spinner"></div>
        </div>
      </div>
    );
  }

  if (!post) {
    return <div className="page"><p>Post not found</p></div>;
  }

  const isLiked = post.likes.includes(user?.id);

  return (
    <div className="page post-detail-page">
      <div className="post-detail">
        <div className="post-header">
          <div>
            <h1 className="post-title-large">{post.title}</h1>
            <div className="post-meta-large">
              <span className={`badge badge-${post.difficulty === 'Easy' ? 'success' : post.difficulty === 'Medium' ? 'warning' : 'danger'}`}>
                {post.difficulty}
              </span>
              <span className="badge badge-primary">{post.language}</span>
              {post.tags.map((tag, index) => (
                <span key={index} className="badge badge-info">{tag}</span>
              ))}
            </div>
          </div>
          {user?.id === post.author._id && (
            <button onClick={handleDelete} className="btn btn-danger">Delete Post</button>
          )}
        </div>

        <div className="post-author-section">
          <div className="author-avatar-large">
            {post.author.name.charAt(0).toUpperCase()}
          </div>
          <div>
            <p className="author-name-large">{post.author.name}</p>
            <p className="author-info">{post.author.college} • Score: {post.author.codingScore}</p>
            <p className="post-date">{new Date(post.createdAt).toLocaleDateString()}</p>
          </div>
          <div className="post-actions">
            <button onClick={handleLike} className={`btn ${isLiked ? 'btn-primary' : 'btn-outline'}`}>
              {isLiked ? '❤️' : '🤍'} {post.likes.length}
            </button>
            <span className="post-views">👁️ {post.views} views</span>
          </div>
        </div>

        <div className="post-content-section">
          <h2 className="section-heading">Problem</h2>
          <div className="problem-description">{post.problem}</div>

          <h2 className="section-heading">Solution</h2>
          <pre className="code-block"><code>{post.code}</code></pre>
        </div>

        <div className="discussion-section">
          <h2 className="section-heading">Discussion ({discussion?.comments.length || 0})</h2>
          
          <form onSubmit={handleAddComment} className="comment-form">
            <textarea
              value={comment.content}
              onChange={(e) => setComment({ ...comment, content: e.target.value })}
              className="form-control"
              placeholder="Share your thoughts..."
              rows="3"
              required
            />
            <div className="comment-options">
              <input
                type="text"
                value={comment.language}
                onChange={(e) => setComment({ ...comment, language: e.target.value })}
                className="form-control"
                placeholder="Language (optional)"
              />
              <button type="submit" className="btn btn-primary">Post Comment</button>
            </div>
          </form>

          <div className="comments-list">
            {discussion?.comments.map(c => (
              <div key={c._id} className="comment-card">
                <div className="comment-header">
                  <div className="comment-author-avatar">
                    {c.user.name.charAt(0).toUpperCase()}
                  </div>
                  <div>
                    <p className="comment-author-name">{c.user.name}</p>
                    <p className="comment-date">{new Date(c.createdAt).toLocaleDateString()}</p>
                  </div>
                </div>
                <p className="comment-content">{c.content}</p>
                {c.code && (
                  <pre className="comment-code"><code>{c.code}</code></pre>
                )}
                <div className="comment-actions">
                  <span>❤️ {c.likes.length}</span>
                  <span>💬 {c.replies.length} replies</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default PostDetail;
