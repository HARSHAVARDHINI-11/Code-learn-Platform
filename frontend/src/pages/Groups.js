import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import './Groups.css';

const Groups = () => {
  const [myGroups, setMyGroups] = useState([]);
  const [allGroups, setAllGroups] = useState([]);
  const [activeTab, setActiveTab] = useState('my');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchGroups();
  }, []);

  const fetchGroups = async () => {
    try {
      const [myRes, allRes] = await Promise.all([
        axios.get('/api/groups'),
        axios.get('/api/groups/all')
      ]);
      setMyGroups(myRes.data);
      setAllGroups(allRes.data);
    } catch (err) {
      console.error('Error fetching groups:', err);
    } finally {
      setLoading(false);
    }
  };

  const groups = activeTab === 'my' ? myGroups : allGroups;

  return (
    <div className="page groups-page">
      <div className="page-header">
        <div>
          <h1 className="page-title">Study Groups</h1>
          <p className="page-subtitle">Collaborate and compete with your peers</p>
        </div>
        <Link to="/create-group" className="btn btn-primary">Create Group</Link>
      </div>

      <div className="tabs">
        <button 
          className={`tab ${activeTab === 'my' ? 'tab-active' : ''}`}
          onClick={() => setActiveTab('my')}
        >
          My Groups ({myGroups.length})
        </button>
        <button 
          className={`tab ${activeTab === 'all' ? 'tab-active' : ''}`}
          onClick={() => setActiveTab('all')}
        >
          Discover ({allGroups.length})
        </button>
      </div>

      {loading ? (
        <div className="flex-center" style={{ minHeight: '40vh' }}>
          <div className="spinner"></div>
        </div>
      ) : (
        <div className="groups-grid">
          {groups.length > 0 ? (
            groups.map(group => (
              <Link key={group._id} to={`/groups/${group._id}`} className="group-card">
                <div className="group-card-header">
                  <div className="group-icon-large">👥</div>
                  <div className="flex-1">
                    <h3 className="group-name-large">{group.name}</h3>
                    <p className="group-creator">Created by {group.creator.name}</p>
                  </div>
                </div>
                <p className="group-description">{group.description}</p>
                <div className="group-stats">
                  <div className="group-stat">
                    <span className="stat-number">{group.members.length}</span>
                    <span className="stat-text">Members</span>
                  </div>
                  <div className="group-stat">
                    <span className="stat-number">{group.groupScore}</span>
                    <span className="stat-text">Score</span>
                  </div>
                  <div className="group-badge">
                    {group.isPrivate ? (
                      <span className="badge badge-warning">🔒 Private</span>
                    ) : (
                      <span className="badge badge-success">🌐 Public</span>
                    )}
                  </div>
                </div>
              </Link>
            ))
          ) : (
            <div className="empty-state">
              {activeTab === 'my' ? 'You haven\'t joined any groups yet.' : 'No public groups available.'}
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default Groups;
