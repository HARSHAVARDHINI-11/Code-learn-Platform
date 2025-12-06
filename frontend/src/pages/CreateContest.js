import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const CreateContest = () => {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    startTime: '',
    duration: 60,
    problems: [{ title: '', description: '', difficulty: 'Medium', points: 100 }]
  });
  const [groups, setGroups] = useState([]);
  const [selectedGroups, setSelectedGroups] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  React.useEffect(() => {
    fetchGroups();
  }, []);

  const fetchGroups = async () => {
    try {
      const res = await axios.get('/api/groups');
      setGroups(res.data);
    } catch (err) {
      console.error('Error fetching groups:', err);
    }
  };

  const onChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleGroupToggle = (groupId) => {
    setSelectedGroups(prev =>
      prev.includes(groupId)
        ? prev.filter(id => id !== groupId)
        : [...prev, groupId]
    );
  };

  const handleProblemChange = (index, field, value) => {
    const newProblems = [...formData.problems];
    newProblems[index][field] = value;
    setFormData({ ...formData, problems: newProblems });
  };

  const addProblem = () => {
    setFormData({
      ...formData,
      problems: [...formData.problems, { title: '', description: '', difficulty: 'Medium', points: 100 }]
    });
  };

  const removeProblem = (index) => {
    setFormData({
      ...formData,
      problems: formData.problems.filter((_, i) => i !== index)
    });
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await axios.post('/api/contests', {
        ...formData,
        participatingGroups: selectedGroups
      });
      navigate('/contests');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create contest');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page create-contest-page">
      <div className="page-header">
        <h1 className="page-title">Create Contest</h1>
        <p className="page-subtitle">Set up a coding contest for groups</p>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <form onSubmit={onSubmit} className="create-form">
        <div className="form-group">
          <label className="form-label">Contest Title</label>
          <input
            type="text"
            name="title"
            value={formData.title}
            onChange={onChange}
            className="form-control"
            required
          />
        </div>

        <div className="form-group">
          <label className="form-label">Description</label>
          <textarea
            name="description"
            value={formData.description}
            onChange={onChange}
            className="form-control"
            rows="3"
            required
          />
        </div>

        <div className="form-row">
          <div className="form-group">
            <label className="form-label">Start Time</label>
            <input
              type="datetime-local"
              name="startTime"
              value={formData.startTime}
              onChange={onChange}
              className="form-control"
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Duration (minutes)</label>
            <input
              type="number"
              name="duration"
              value={formData.duration}
              onChange={onChange}
              className="form-control"
              min="15"
              required
            />
          </div>
        </div>

        <div className="form-group">
          <label className="form-label">Participating Groups</label>
          <div className="groups-selection">
            {groups.map(group => (
              <label key={group._id} className="checkbox-label">
                <input
                  type="checkbox"
                  checked={selectedGroups.includes(group._id)}
                  onChange={() => handleGroupToggle(group._id)}
                  className="checkbox-input"
                />
                <span>{group.name}</span>
              </label>
            ))}
          </div>
        </div>

        <div className="form-group">
          <div className="flex-between mb-2">
            <label className="form-label">Problems</label>
            <button type="button" onClick={addProblem} className="btn btn-secondary btn-sm">
              + Add Problem
            </button>
          </div>
          {formData.problems.map((problem, index) => (
            <div key={index} className="problem-section">
              <div className="flex-between mb-2">
                <h4>Problem {index + 1}</h4>
                {formData.problems.length > 1 && (
                  <button type="button" onClick={() => removeProblem(index)} className="btn btn-danger btn-sm">
                    Remove
                  </button>
                )}
              </div>
              <div className="form-group">
                <input
                  type="text"
                  value={problem.title}
                  onChange={(e) => handleProblemChange(index, 'title', e.target.value)}
                  className="form-control"
                  placeholder="Problem title"
                  required
                />
              </div>
              <div className="form-group">
                <textarea
                  value={problem.description}
                  onChange={(e) => handleProblemChange(index, 'description', e.target.value)}
                  className="form-control"
                  placeholder="Problem description"
                  rows="3"
                  required
                />
              </div>
              <div className="form-row">
                <div className="form-group">
                  <select
                    value={problem.difficulty}
                    onChange={(e) => handleProblemChange(index, 'difficulty', e.target.value)}
                    className="form-control"
                  >
                    <option value="Easy">Easy</option>
                    <option value="Medium">Medium</option>
                    <option value="Hard">Hard</option>
                  </select>
                </div>
                <div className="form-group">
                  <input
                    type="number"
                    value={problem.points}
                    onChange={(e) => handleProblemChange(index, 'points', parseInt(e.target.value))}
                    className="form-control"
                    placeholder="Points"
                    min="1"
                    required
                  />
                </div>
              </div>
            </div>
          ))}
        </div>

        <div className="form-actions">
          <button type="button" onClick={() => navigate('/contests')} className="btn btn-secondary">
            Cancel
          </button>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Creating...' : 'Create Contest'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default CreateContest;
