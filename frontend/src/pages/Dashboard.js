import React, { useContext, useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import axios from 'axios';
import './Dashboard.css';

const Dashboard = () => {
  const { user } = useContext(AuthContext);
  const [stats, setStats] = useState({
    recentPosts: [],
    myGroups: [],
    upcomingContests: [],
    leaderboardPosition: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const [postsRes, groupsRes, contestsRes, leaderboardRes] = await Promise.all([
        axios.get('/api/posts?sortBy=recent'),
        axios.get('/api/groups'),
        axios.get('/api/contests'),
        axios.get('/api/leaderboard/college')
      ]);

      setStats({
        recentPosts: postsRes.data.slice(0, 5),
        myGroups: groupsRes.data.slice(0, 4),
        upcomingContests: contestsRes.data.filter(c => c.status === 'upcoming').slice(0, 3),
        leaderboardPosition: leaderboardRes.data.currentUser.rank
      });
    } catch (err) {
      console.error('Error fetching dashboard data:', err);
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

  return (
    <div className="page dashboard">
      <div className="dashboard-header">
        <div>
          <h1 className="page-title">Welcome back, {user?.name}!</h1>
          <p className="page-subtitle">{user?.college}</p>
        </div>
        <Link to="/create-post" className="btn btn-primary">
          Create New Post
        </Link>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: 'var(--accent)' }}>
            <span>⭐</span>
          </div>
          <div className="stat-content">
            <h3 className="stat-value">{user?.codingScore || 0}</h3>
            <p className="stat-label">Total Score</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: 'var(--info)' }}>
            <span>🏆</span>
          </div>
          <div className="stat-content">
            <h3 className="stat-value">#{stats.leaderboardPosition || 'N/A'}</h3>
            <p className="stat-label">College Rank</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: 'var(--success)' }}>
            <span>👥</span>
          </div>
          <div className="stat-content">
            <h3 className="stat-value">{stats.myGroups.length}</h3>
            <p className="stat-label">Groups Joined</p>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon" style={{ backgroundColor: 'var(--warning)' }}>
            <span>🎯</span>
          </div>
          <div className="stat-content">
            <h3 className="stat-value">{stats.upcomingContests.length}</h3>
            <p className="stat-label">Upcoming Contests</p>
          </div>
        </div>
      </div>

      <div className="dashboard-content">
        <div className="dashboard-section">
          <div className="section-header">
            <h2 className="section-title">Recent Posts</h2>
            <Link to="/posts" className="section-link">View All →</Link>
          </div>
          <div className="posts-list">
            {stats.recentPosts.length > 0 ? (
              stats.recentPosts.map(post => (
                <Link key={post._id} to={`/posts/${post._id}`} className="post-card-mini">
                  <div className="post-card-header">
                    <h3 className="post-card-title">{post.title}</h3>
                    <span className="badge badge-primary">{post.language}</span>
                  </div>
                  <p className="post-card-author">
                    by {post.author.name} • {new Date(post.createdAt).toLocaleDateString()}
                  </p>
                  <div className="post-card-stats">
                    <span>👁️ {post.views}</span>
                    <span>❤️ {post.likes.length}</span>
                  </div>
                </Link>
              ))
            ) : (
              <p className="empty-state">No posts yet. Be the first to create one!</p>
            )}
          </div>
        </div>

        <div className="dashboard-sidebar">
          <div className="dashboard-section">
            <div className="section-header">
              <h2 className="section-title">My Groups</h2>
              <Link to="/groups" className="section-link">View All →</Link>
            </div>
            <div className="groups-list">
              {stats.myGroups.length > 0 ? (
                stats.myGroups.map(group => (
                  <Link key={group._id} to={`/groups/${group._id}`} className="group-card-mini">
                    <div className="group-icon">👥</div>
                    <div>
                      <h4 className="group-name">{group.name}</h4>
                      <p className="group-members">{group.members.length} members</p>
                    </div>
                  </Link>
                ))
              ) : (
                <p className="empty-state-mini">No groups yet</p>
              )}
            </div>
            <Link to="/create-group" className="btn btn-secondary btn-block mt-2">
              Create Group
            </Link>
          </div>

          <div className="dashboard-section">
            <div className="section-header">
              <h2 className="section-title">Upcoming Contests</h2>
              <Link to="/contests" className="section-link">View All →</Link>
            </div>
            <div className="contests-list">
              {stats.upcomingContests.length > 0 ? (
                stats.upcomingContests.map(contest => (
                  <Link key={contest._id} to={`/contests/${contest._id}`} className="contest-card-mini">
                    <h4 className="contest-title">{contest.title}</h4>
                    <p className="contest-time">
                      {new Date(contest.startTime).toLocaleString()}
                    </p>
                    <span className="badge badge-info">{contest.duration} min</span>
                  </Link>
                ))
              ) : (
                <p className="empty-state-mini">No upcoming contests</p>
              )}
            </div>
            <Link to="/create-contest" className="btn btn-secondary btn-block mt-2">
              Create Contest
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
