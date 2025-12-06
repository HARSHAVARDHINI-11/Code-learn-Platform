import React, { useState, useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import './Auth.css';

const Register = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    college: '',
    department: '',
    year: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { register } = useContext(AuthContext);
  const navigate = useNavigate();

  const { name, email, password, confirmPassword, college, department, year } = formData;

  const onChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (password !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    setLoading(true);

    const result = await register({
      name,
      email,
      password,
      college,
      department,
      year: year ? parseInt(year) : undefined
    });
    
    if (result.success) {
      navigate('/');
    } else {
      setError(result.message);
    }
    setLoading(false);
  };

  return (
    <div className="auth-page">
      <div className="auth-container">
        <div className="auth-header">
          <h1 className="auth-title">Create Account</h1>
          <p className="auth-subtitle">Join the coding community</p>
        </div>

        {error && (
          <div className="alert alert-danger">{error}</div>
        )}

        <form onSubmit={onSubmit} className="auth-form">
          <div className="form-group">
            <label className="form-label">Full Name</label>
            <input
              type="text"
              name="name"
              value={name}
              onChange={onChange}
              className="form-control"
              placeholder="Enter your name"
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Email Address</label>
            <input
              type="email"
              name="email"
              value={email}
              onChange={onChange}
              className="form-control"
              placeholder="Enter your email"
              required
            />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Password</label>
              <input
                type="password"
                name="password"
                value={password}
                onChange={onChange}
                className="form-control"
                placeholder="Enter password"
                required
                minLength="6"
              />
            </div>

            <div className="form-group">
              <label className="form-label">Confirm Password</label>
              <input
                type="password"
                name="confirmPassword"
                value={confirmPassword}
                onChange={onChange}
                className="form-control"
                placeholder="Confirm password"
                required
                minLength="6"
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">College/University</label>
            <input
              type="text"
              name="college"
              value={college}
              onChange={onChange}
              className="form-control"
              placeholder="Enter your college"
              required
            />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Department (Optional)</label>
              <input
                type="text"
                name="department"
                value={department}
                onChange={onChange}
                className="form-control"
                placeholder="e.g., Computer Science"
              />
            </div>

            <div className="form-group">
              <label className="form-label">Year (Optional)</label>
              <select
                name="year"
                value={year}
                onChange={onChange}
                className="form-control"
              >
                <option value="">Select Year</option>
                <option value="1">1st Year</option>
                <option value="2">2nd Year</option>
                <option value="3">3rd Year</option>
                <option value="4">4th Year</option>
                <option value="5">5th Year</option>
              </select>
            </div>
          </div>

          <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
            {loading ? 'Creating Account...' : 'Create Account'}
          </button>
        </form>

        <div className="auth-footer">
          <p>Already have an account? <Link to="/login" className="auth-link">Login here</Link></p>
        </div>
      </div>
    </div>
  );
};

export default Register;
