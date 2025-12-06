import React, { useState, useEffect, useContext } from 'react';
import { useParams } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import axios from 'axios';
import './GroupDetail.css';

const GroupDetail = () => {
  const { id } = useParams();
  const { user } = useContext(AuthContext);
  const [group, setGroup] = useState(null);
  const [inviteCode, setInviteCode] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchGroup();
  }, [id]);

  const fetchGroup = async () => {
    try {
      const res = await axios.get(`/api/groups/${id}`);
      setGroup(res.data);
    } catch (err) {
      console.error('Error fetching group:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleJoin = async () => {
    try {
      const res = await axios.post(`/api/groups/${id}/join`, { inviteCode });
      setGroup(res.data);
      alert('Successfully joined the group!');
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to join group');
    }
  };

  const handleLeave = async () => {
    if (window.confirm('Are you sure you want to leave this group?')) {
      try {
        await axios.delete(`/api/groups/${id}/leave`);
        fetchGroup();
      } catch (err) {
        alert(err.response?.data?.message || 'Failed to leave group');
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

  if (!group) {
    return <div className="page"><p>Group not found</p></div>;
  }

  const isMember = group.members.some(m => m.user._id === user?.id);
  const isCreator = group.creator._id === user?.id;

  return (
    <div className="page group-detail-page">
      <div className="group-detail">
        <div className="group-header">
          <div className="group-icon-xl">👥</div>
          <div className="flex-1">
            <h1 className="group-title">{group.name}</h1>
            <p className="group-info">
              Created by {group.creator.name} • {group.members.length} members • Score: {group.groupScore}
            </p>
          </div>
          {isMember ? (
            !isCreator && (
              <button onClick={handleLeave} className="btn btn-danger">Leave Group</button>
            )
          ) : (
            <div className="join-section">
              {group.isPrivate && (
                <input
                  type="text"
                  value={inviteCode}
                  onChange={(e) => setInviteCode(e.target.value)}
                  className="form-control"
                  placeholder="Enter invite code"
                />
              )}
              <button onClick={handleJoin} className="btn btn-primary">Join Group</button>
            </div>
          )}
        </div>

        <div className="group-description-section">
          <h2 className="section-title">About</h2>
          <p className="group-description-full">{group.description}</p>
        </div>

        {isMember && group.isPrivate && (
          <div className="invite-section">
            <h3>Invite Code:</h3>
            <code className="invite-code">{group.inviteCode}</code>
          </div>
        )}

        <div className="members-section">
          <h2 className="section-title">Members ({group.members.length})</h2>
          <div className="members-grid">
            {group.members.map(member => (
              <div key={member.user._id} className="member-card">
                <div className="member-avatar">
                  {member.user.name.charAt(0).toUpperCase()}
                </div>
                <div className="member-info">
                  <p className="member-name">{member.user.name}</p>
                  <p className="member-email">{member.user.email}</p>
                  <p className="member-college">{member.user.college}</p>
                  <div className="member-meta">
                    <span className="badge badge-primary">Score: {member.user.codingScore}</span>
                    {member.role === 'admin' && (
                      <span className="badge badge-warning">Admin</span>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default GroupDetail;
