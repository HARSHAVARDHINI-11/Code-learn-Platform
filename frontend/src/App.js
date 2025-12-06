import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import PrivateRoute from './components/PrivateRoute';
import Navbar from './components/Navbar';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Posts from './pages/Posts';
import PostDetail from './pages/PostDetail';
import CreatePost from './pages/CreatePost';
import Groups from './pages/Groups';
import GroupDetail from './pages/GroupDetail';
import CreateGroup from './pages/CreateGroup';
import Contests from './pages/Contests';
import ContestDetail from './pages/ContestDetail';
import CreateContest from './pages/CreateContest';
import Leaderboard from './pages/Leaderboard';
import Profile from './pages/Profile';
import './App.css';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="app">
          <Navbar />
          <main className="main-content">
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
              <Route path="/posts" element={<PrivateRoute><Posts /></PrivateRoute>} />
              <Route path="/posts/:id" element={<PrivateRoute><PostDetail /></PrivateRoute>} />
              <Route path="/create-post" element={<PrivateRoute><CreatePost /></PrivateRoute>} />
              <Route path="/groups" element={<PrivateRoute><Groups /></PrivateRoute>} />
              <Route path="/groups/:id" element={<PrivateRoute><GroupDetail /></PrivateRoute>} />
              <Route path="/create-group" element={<PrivateRoute><CreateGroup /></PrivateRoute>} />
              <Route path="/contests" element={<PrivateRoute><Contests /></PrivateRoute>} />
              <Route path="/contests/:id" element={<PrivateRoute><ContestDetail /></PrivateRoute>} />
              <Route path="/create-contest" element={<PrivateRoute><CreateContest /></PrivateRoute>} />
              <Route path="/leaderboard" element={<PrivateRoute><Leaderboard /></PrivateRoute>} />
              <Route path="/profile" element={<PrivateRoute><Profile /></PrivateRoute>} />
              <Route path="*" element={<Navigate to="/" />} />
            </Routes>
          </main>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
