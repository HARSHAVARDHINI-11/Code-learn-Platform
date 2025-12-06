import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './CreateGroup.css';

const CreateGroup = () => {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    isPrivate: false,
    allowedEmails: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const { name, description, isPrivate, allowedEmails } = formData;

  const onChange = (e) => {
    const value = e.target.type === 'checkbox' ? e.target.checked : e.target.value;
    setFormData({ ...formData, [e.target.name]: value });
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const emailsArray = allowedEmails
        .split(',')
        .map(email => email.trim())
        .filter(email => email);

      const res = await axios.post('/api/groups', {
        name,
        description,
        isPrivate,
        allowedEmails: emailsArray
      });
      navigate(`/groups/${res.data._id}`);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create group');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page create-group-page">
      <div className="page-header">
        <h1 className="page-title">Create Study Group</h1>
        <p className="page-subtitle">Form a group to collaborate and compete</p>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <form onSubmit={onSubmit} className="create-form">
        <div className="form-group">
          <label className="form-label">Group Name</label>
          <input
            type="text"
            name="name"
            value={name}
            onChange={onChange}
            className="form-control"
            placeholder="e.g., Algorithm Masters"
            required
          />
        </div>

        <div className="form-group">
          <label className="form-label">Description</label>
          <textarea
            name="description"
            value={description}
            onChange={onChange}
            className="form-control"
            placeholder="Describe the purpose and goals of your group..."
            required
            rows="4"
          />
        </div>

        <div className="form-group">
          <label className="checkbox-label">
            <input
              type="checkbox"
              name="isPrivate"
              checked={isPrivate}
              onChange={onChange}
              className="checkbox-input"
            />
            <span>Make this group private (requires invite code)</span>
          </label>
        </div>

        <div className="form-group">
          <label className="form-label">Allowed Emails (Optional)</label>
          <input
            type="text"
            name="allowedEmails"
            value={allowedEmails}
            onChange={onChange}
            className="form-control"
            placeholder="email1@example.com, email2@example.com"
          />
          <small className="form-help">Comma-separated list of emails allowed to join</small>
        </div>

        <div className="form-actions">
          <button type="button" onClick={() => navigate('/groups')} className="btn btn-secondary">
            Cancel
          </button>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Creating...' : 'Create Group'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default CreateGroup;
