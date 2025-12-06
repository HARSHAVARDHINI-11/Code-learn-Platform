import React, { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import './Navbar.css';

const Navbar = () => {
  const { user, logout, isAuthenticated } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <Link to="/" className="navbar-brand">
          <span className="brand-icon">{'</>'}</span>
          <span className="brand-text">CodeLearn</span>
        </Link>

        {isAuthenticated && (
          <>
            <div className="navbar-links">
              <Link to="/" className="nav-link">Dashboard</Link>
              <Link to="/posts" className="nav-link">Posts</Link>
              <Link to="/groups" className="nav-link">Groups</Link>
              <Link to="/contests" className="nav-link">Contests</Link>
              <Link to="/leaderboard" className="nav-link">Leaderboard</Link>
            </div>

            <div className="navbar-actions">
              <div className="user-score">
                <span className="score-label">Score:</span>
                <span className="score-value">{user?.codingScore || 0}</span>
              </div>
              <Link to="/profile" className="user-profile">
                <div className="user-avatar">
                  {user?.name?.charAt(0).toUpperCase()}
                </div>
                <span className="user-name">{user?.name}</span>
              </Link>
              <button onClick={handleLogout} className="btn btn-outline btn-sm">
                Logout
              </button>
            </div>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
