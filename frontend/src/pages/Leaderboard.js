import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './Leaderboard.css';

const Leaderboard = () => {
  const [leaderboard, setLeaderboard] = useState([]);
  const [currentUser, setCurrentUser] = useState(null);
  const [activeTab, setActiveTab] = useState('college');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchLeaderboard();
  }, [activeTab]);

  const fetchLeaderboard = async () => {
    setLoading(true);
    try {
      const res = await axios.get(`/api/leaderboard/${activeTab}`);
      setLeaderboard(res.data.leaderboard);
      setCurrentUser(res.data.currentUser);
    } catch (err) {
      console.error('Error fetching leaderboard:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page leaderboard-page">
      <div className="page-header">
        <div>
          <h1 className="page-title">🏆 Leaderboard</h1>
          <p className="page-subtitle">Compete and track your ranking</p>
        </div>
      </div>

      {currentUser && (
        <div className="user-rank-card">
          <div className="rank-badge">#{currentUser.rank}</div>
          <div className="user-rank-info">
            <h3>{currentUser.name}</h3>
            <p>{currentUser.college}</p>
          </div>
          <div className="user-rank-score">
            <span className="score-large">{currentUser.codingScore}</span>
            <span className="score-label-small">points</span>
          </div>
        </div>
      )}

      <div className="tabs">
        <button 
          className={`tab ${activeTab === 'college' ? 'tab-active' : ''}`}
          onClick={() => setActiveTab('college')}
        >
          College Level
        </button>
        <button 
          className={`tab ${activeTab === 'global' ? 'tab-active' : ''}`}
          onClick={() => setActiveTab('global')}
        >
          Global
        </button>
        <button 
          className={`tab ${activeTab === 'groups' ? 'tab-active' : ''}`}
          onClick={() => setActiveTab('groups')}
        >
          Groups
        </button>
      </div>

      {loading ? (
        <div className="flex-center" style={{ minHeight: '40vh' }}>
          <div className="spinner"></div>
        </div>
      ) : (
        <div className="leaderboard-container">
          {activeTab === 'groups' ? (
            <div className="groups-leaderboard">
              {leaderboard.map((group, index) => (
                <div key={group._id} className="group-rank-card">
                  <div className="rank-number">#{index + 1}</div>
                  <div className="group-rank-info">
                    <h3>{group.name}</h3>
                    <p>{group.members.length} members</p>
                  </div>
                  <div className="group-score">{group.groupScore} pts</div>
                </div>
              ))}
            </div>
          ) : (
            <table className="leaderboard-table">
              <thead>
                <tr>
                  <th>Rank</th>
                  <th>Name</th>
                  <th>College</th>
                  <th>Department</th>
                  <th>Score</th>
                </tr>
              </thead>
              <tbody>
                {leaderboard.map((user, index) => (
                  <tr key={user._id} className={user._id === currentUser?._id ? 'current-user-row' : ''}>
                    <td>
                      <div className="rank-cell">
                        {index === 0 && '🥇'}
                        {index === 1 && '🥈'}
                        {index === 2 && '🥉'}
                        {index > 2 && `#${index + 1}`}
                      </div>
                    </td>
                    <td className="name-cell">{user.name}</td>
                    <td>{user.college}</td>
                    <td>{user.department || '-'}</td>
                    <td className="score-cell">{user.codingScore}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}
    </div>
  );
};

export default Leaderboard;
