import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';

const ContestDetail = () => {
  const { id } = useParams();
  const [contest, setContest] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchContest();
  }, [id]);

  const fetchContest = async () => {
    try {
      const res = await axios.get(`/api/contests/${id}`);
      setContest(res.data);
    } catch (err) {
      console.error('Error fetching contest:', err);
    } finally {
      setLoading(false);
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

  if (!contest) {
    return <div className="page"><p>Contest not found</p></div>;
  }

  return (
    <div className="page post-detail-page">
      <div className="post-detail">
        <div className="post-header">
          <div>
            <h1 className="post-title-large">{contest.title}</h1>
            <div className="post-meta-large">
              <span className={`badge badge-${contest.status === 'upcoming' ? 'info' : contest.status === 'ongoing' ? 'success' : 'secondary'}`}>
                {contest.status.toUpperCase()}
              </span>
              <span className="badge badge-primary">{contest.duration} minutes</span>
            </div>
          </div>
        </div>

        <div className="post-content-section">
          <h2 className="section-heading">Description</h2>
          <p className="problem-description">{contest.description}</p>

          <h2 className="section-heading">Contest Information</h2>
          <div className="contest-info-grid" style={{ marginBottom: 'var(--spacing-2xl)' }}>
            <div className="contest-info-item">
              <span className="info-label">Start Time</span>
              <span className="info-value">{new Date(contest.startTime).toLocaleString()}</span>
            </div>
            <div className="contest-info-item">
              <span className="info-label">End Time</span>
              <span className="info-value">{new Date(contest.endTime).toLocaleString()}</span>
            </div>
            <div className="contest-info-item">
              <span className="info-label">Duration</span>
              <span className="info-value">{contest.duration} minutes</span>
            </div>
            <div className="contest-info-item">
              <span className="info-label">Problems</span>
              <span className="info-value">{contest.problems.length}</span>
            </div>
          </div>

          <h2 className="section-heading">Problems</h2>
          {contest.problems.map((problem, index) => (
            <div key={index} className="problem-section" style={{ marginBottom: 'var(--spacing-lg)' }}>
              <div className="flex-between mb-2">
                <h3>Problem {index + 1}: {problem.title}</h3>
                <div>
                  <span className={`badge badge-${problem.difficulty === 'Easy' ? 'success' : problem.difficulty === 'Medium' ? 'warning' : 'danger'}`}>
                    {problem.difficulty}
                  </span>
                  <span className="badge badge-primary ml-1">{problem.points} pts</span>
                </div>
              </div>
              <p className="problem-description">{problem.description}</p>
            </div>
          ))}

          <h2 className="section-heading">Leaderboard</h2>
          <div className="table-container">
            <table className="leaderboard-table">
              <thead>
                <tr>
                  <th>Rank</th>
                  <th>Group</th>
                  <th>Score</th>
                </tr>
              </thead>
              <tbody>
                {contest.participatingGroups.sort((a, b) => b.score - a.score).map((pg, index) => (
                  <tr key={pg.group._id}>
                    <td>{index + 1}</td>
                    <td>{pg.group.name}</td>
                    <td>{pg.score}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ContestDetail;
