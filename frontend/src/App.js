import React, { useState, useEffect } from 'react';
import { LoginPage, RegisterPage } from './features/auth';
import { DoctorDashboard, PatientDashboard } from './features/dashboard';
import { ProfilePage } from './features/profile';
import { AdminDashboard } from './features/admin';
import { SelectSpecialist, BookAppointment, BookingConfirmed } from './features/appointments';

function App() {
  const [user, setUser] = useState(null);
  const [showRegister, setShowRegister] = useState(false);
  const [currentView, setCurrentView] = useState('DASHBOARD');
  const [selectedDoctor, setSelectedDoctor] = useState(null);
  const [bookingDetails, setBookingDetails] = useState(null);

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
    setCurrentView('DASHBOARD');
  };

  const handleLogout = () => {
    localStorage.removeItem('user');
    setUser(null);
    setShowRegister(false);
    setCurrentView('DASHBOARD');
    setSelectedDoctor(null);
    setBookingDetails(null);
  };

  const navigateTo = (view) => {
    setCurrentView(view);
  };

  // Not logged in: show login/register
  if (!user) {
    return showRegister ? (
      <RegisterPage onSwitch={() => setShowRegister(false)} />
    ) : (
      <LoginPage onLogin={handleLogin} onSwitch={() => setShowRegister(true)} />
    );
  }

  // Logged in: route based on currentView
  switch (currentView) {
    case 'PROFILE':
      return (
        <ProfilePage
          user={user}
          onBack={() => setCurrentView('DASHBOARD')}
          onLogout={handleLogout}
          onNavigate={navigateTo}
        />
      );

    case 'SPECIALISTS':
      return (
        <SelectSpecialist
          onNavigate={navigateTo}
          onSelectDoctor={(doc) => {
            setSelectedDoctor(doc);
            setCurrentView('BOOK_APPOINTMENT');
          }}
        />
      );

    case 'BOOK_APPOINTMENT':
      return (
        <BookAppointment
          doctor={selectedDoctor}
          onBack={() => setCurrentView('SPECIALISTS')}
          onConfirm={(details) => {
            setBookingDetails(details);
            setCurrentView('BOOKING_CONFIRMED');
          }}
        />
      );

    case 'BOOKING_CONFIRMED':
      return (
        <BookingConfirmed
          booking={bookingDetails}
          onGoToDashboard={() => {
            setBookingDetails(null);
            setSelectedDoctor(null);
            setCurrentView('DASHBOARD');
          }}
        />
      );

    case 'DASHBOARD':
    default:
      // Route based on role
      if (user.role === 'ADMIN') {
        return (
          <AdminDashboard
            onLogout={handleLogout}
            onNavigate={navigateTo}
          />
        );
      }
      if (user.role === 'DOCTOR') {
        return (
          <DoctorDashboard
            onLogout={handleLogout}
            onNavigate={navigateTo}
          />
        );
      }
      // Default: Patient
      return (
        <PatientDashboard
          onLogout={handleLogout}
          onNavigateProfile={() => setCurrentView('PROFILE')}
          onNavigate={navigateTo}
        />
      );
  }
}

export default App;
