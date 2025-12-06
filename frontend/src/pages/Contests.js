import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import './Contests.css';

const Contests = () => {
  const [contests, setContests] = useState([]);
  const [filter, setFilter] = useState('all');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchContests();
  }, []);

  const fetchContests = async () => {
    try {
      const res = await axios.get('/api/contests');
      setContests(res.data);
    } catch (err) {
      console.error('Error fetching contests:', err);
    } finally {
      setLoading(false);
    }
  };

  const filteredContests = contests.filter(contest => {
    if (filter === 'all') return true;
    return contest.status === filter;
  });

  const getStatusBadge = (status) => {
    const badges = {
      upcoming: { class: 'badge-info', text: '⏰ Upcoming' },
      ongoing: { class: 'badge-success', text: '▶️ Ongoing' },
      completed: { class: 'badge-secondary', text: '✓ Completed' }
    };
    return badges[status] || badges.upcoming;
  };

  return (
    <div className="page contests-page">
      <div className="page-header">
        <div>
          <h1 className="page-title">Coding Contests</h1>
          <p className="page-subtitle">Compete with other groups and improve your skills</p>
        </div>
        <Link to="/create-contest" className="btn btn-primary">Create Contest</Link>
      </div>

      <div className="filter-tabs">
        <button 
          className={`filter-btn ${filter === 'all' ? 'active' : ''}`}
          onClick={() => setFilter('all')}
        >
          All
        </button>
        <button 
          className={`filter-btn ${filter === 'upcoming' ? 'active' : ''}`}
          onClick={() => setFilter('upcoming')}
        >
          Upcoming
        </button>
        <button 
          className={`filter-btn ${filter === 'ongoing' ? 'active' : ''}`}
          onClick={() => setFilter('ongoing')}
        >
          Ongoing
        </button>
        <button 
          className={`filter-btn ${filter === 'completed' ? 'active' : ''}`}
          onClick={() => setFilter('completed')}
        >
          Completed
        </button>
      </div>

      {loading ? (
        <div className="flex-center" style={{ minHeight: '40vh' }}>
          <div className="spinner"></div>
        </div>
      ) : (
        <div className="contests-list">
          {filteredContests.length > 0 ? (
            filteredContests.map(contest => {
              const statusBadge = getStatusBadge(contest.status);
              return (
                <Link key={contest._id} to={`/contests/${contest._id}`} className="contest-card-large">
                  <div className="contest-header">
                    <h3 className="contest-title-large">{contest.title}</h3>
                    <span className={`badge ${statusBadge.class}`}>{statusBadge.text}</span>
                  </div>
                  <p className="contest-description">{contest.description}</p>
                  <div className="contest-info-grid">
                    <div className="contest-info-item">
                      <span className="info-label">Start Time</span>
                      <span className="info-value">
                        {new Date(contest.startTime).toLocaleString()}
                      </span>
                    </div>
                    <div className="contest-info-item">
                      <span className="info-label">Duration</span>
                      <span className="info-value">{contest.duration} minutes</span>
                    </div>
                    <div className="contest-info-item">
                      <span className="info-label">Problems</span>
                      <span className="info-value">{contest.problems.length}</span>
                    </div>
                    <div className="contest-info-item">
                      <span className="info-label">Groups</span>
                      <span className="info-value">{contest.participatingGroups.length}</span>
                    </div>
                  </div>
                </Link>
              );
            })
          ) : (
            <div className="empty-state">No contests found</div>
          )}
        </div>
      )}
    </div>
  );
};

export default Contests;
