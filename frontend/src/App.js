import React, { useState, useEffect } from 'react';
import LoginPage from './components/LoginPage';
import RegisterPage from './components/RegisterPage';
import DoctorDashboard from './components/DoctorDashboard';
import PatientDashboard from './components/PatientDashboard';

function App() {
  const [user, setUser] = useState(null);
  const [showRegister, setShowRegister] = useState(false);

  useEffect(() => {
    const stored = localStorage.getItem('user');
    if (stored) {
      try {
        setUser(JSON.parse(stored));
      } catch { }
    }
  }, []);

  const handleLogin = (u) => {
    localStorage.setItem('user', JSON.stringify(u));
    setUser(u);
  };

  const handleLogout = () => {
    localStorage.removeItem('user');
    setUser(null);
    setShowRegister(false);
  };

  if (user) {
    return user.role === 'DOCTOR' ? (
      <DoctorDashboard onLogout={handleLogout} />
    ) : (
      <PatientDashboard onLogout={handleLogout} />
    );
  }

  return showRegister ? (
    <RegisterPage onSwitch={() => setShowRegister(false)} />
  ) : (
    <LoginPage onLogin={handleLogin} onSwitch={() => setShowRegister(true)} />
  );
}

export default App;
