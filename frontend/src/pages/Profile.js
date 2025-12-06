import React, { useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import './Profile.css';

const Profile = () => {
  const { user, updateProfile } = useContext(AuthContext);
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    name: user?.name || '',
    bio: user?.bio || '',
    skills: user?.skills?.join(', ') || '',
    department: user?.department || '',
    year: user?.year || ''
  });
  const [message, setMessage] = useState('');

  const onChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    const skillsArray = formData.skills.split(',').map(s => s.trim()).filter(s => s);
    const result = await updateProfile({
      ...formData,
      skills: skillsArray
    });
    
    if (result.success) {
      setMessage('Profile updated successfully!');
      setIsEditing(false);
      setTimeout(() => setMessage(''), 3000);
    } else {
      setMessage('Failed to update profile');
    }
  };

  return (
    <div className="page profile-page">
      <div className="profile-container">
        <div className="profile-header">
          <div className="profile-avatar-large">
            {user?.name?.charAt(0).toUpperCase()}
          </div>
          <div>
            <h1 className="profile-name">{user?.name}</h1>
            <p className="profile-email">{user?.email}</p>
            <p className="profile-college">{user?.college}</p>
          </div>
          <button onClick={() => setIsEditing(!isEditing)} className="btn btn-primary">
            {isEditing ? 'Cancel' : 'Edit Profile'}
          </button>
        </div>

        <div className="profile-stats">
          <div className="profile-stat-card">
            <span className="profile-stat-value">{user?.codingScore || 0}</span>
            <span className="profile-stat-label">Coding Score</span>
          </div>
          <div className="profile-stat-card">
            <span className="profile-stat-value">{user?.groups?.length || 0}</span>
            <span className="profile-stat-label">Groups</span>
          </div>
          <div className="profile-stat-card">
            <span className="profile-stat-value">{user?.year || '-'}</span>
            <span className="profile-stat-label">Year</span>
          </div>
        </div>

        {message && <div className={`alert ${message.includes('success') ? 'alert-success' : 'alert-danger'}`}>{message}</div>}

        {isEditing ? (
          <form onSubmit={onSubmit} className="profile-form">
            <div className="form-group">
              <label className="form-label">Name</label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={onChange}
                className="form-control"
              />
            </div>

            <div className="form-group">
              <label className="form-label">Bio</label>
              <textarea
                name="bio"
                value={formData.bio}
                onChange={onChange}
                className="form-control"
                rows="4"
                placeholder="Tell us about yourself..."
              />
            </div>

            <div className="form-group">
              <label className="form-label">Skills (comma-separated)</label>
              <input
                type="text"
                name="skills"
                value={formData.skills}
                onChange={onChange}
                className="form-control"
                placeholder="JavaScript, Python, Data Structures"
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Department</label>
                <input
                  type="text"
                  name="department"
                  value={formData.department}
                  onChange={onChange}
                  className="form-control"
                />
              </div>

              <div className="form-group">
                <label className="form-label">Year</label>
                <select name="year" value={formData.year} onChange={onChange} className="form-control">
                  <option value="">Select Year</option>
                  <option value="1">1st Year</option>
                  <option value="2">2nd Year</option>
                  <option value="3">3rd Year</option>
                  <option value="4">4th Year</option>
                  <option value="5">5th Year</option>
                </select>
              </div>
            </div>

            <button type="submit" className="btn btn-primary">Save Changes</button>
          </form>
        ) : (
          <div className="profile-info">
            {user?.bio && (
              <div className="info-section">
                <h3 className="info-title">Bio</h3>
                <p className="info-content">{user.bio}</p>
              </div>
            )}

            {user?.skills && user.skills.length > 0 && (
              <div className="info-section">
                <h3 className="info-title">Skills</h3>
                <div className="skills-list">
                  {user.skills.map((skill, index) => (
                    <span key={index} className="badge badge-primary">{skill}</span>
                  ))}
                </div>
              </div>
            )}

            <div className="info-section">
              <h3 className="info-title">Details</h3>
              <div className="details-grid">
                <div className="detail-item">
                  <span className="detail-label">Department:</span>
                  <span className="detail-value">{user?.department || 'Not specified'}</span>
                </div>
                <div className="detail-item">
                  <span className="detail-label">Year:</span>
                  <span className="detail-value">{user?.year ? `${user.year}${['st', 'nd', 'rd', 'th'][user.year > 3 ? 3 : user.year - 1]} Year` : 'Not specified'}</span>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Profile;
