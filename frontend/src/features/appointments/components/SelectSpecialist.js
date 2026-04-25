import React, { useState, useMemo } from 'react';
import './SelectSpecialist.css';

const DOCTORS = [
    { id: 1, name: 'Dr. Sarah Jenkins', specialty: 'Cardiology', rating: 4.9, reviews: 124, available: true },
    { id: 2, name: 'Dr. Michael Chen', specialty: 'Pediatrics', rating: 4.8, reviews: 89, available: true },
    { id: 3, name: 'Dr. Elena Rodriguez', specialty: 'Dermatology', rating: 5.0, reviews: 210, available: true },
    { id: 4, name: 'Dr. James Wilson', specialty: 'General Medicine', rating: 4.7, reviews: 156, available: true },
    { id: 5, name: 'Dr. Aisha Khan', specialty: 'Neurology', rating: 4.9, reviews: 42, available: true },
    { id: 6, name: 'Dr. Thomas Berg', specialty: 'Oncology', rating: 4.6, reviews: 73, available: true },
    { id: 7, name: 'Dr. Maria Garcia', specialty: 'Psychiatry', rating: 4.8, reviews: 192, available: true },
    { id: 8, name: 'Dr. Robert Taylor', specialty: 'Orthopedics', rating: 4.7, reviews: 55, available: true },
    { id: 9, name: 'Dr. Lisa Park', specialty: 'Cardiology', rating: 4.6, reviews: 67, available: false },
    { id: 10, name: 'Dr. Ahmed Hassan', specialty: 'Pediatrics', rating: 4.5, reviews: 34, available: true },
    { id: 11, name: 'Dr. Clara Reyes', specialty: 'Dermatology', rating: 4.9, reviews: 101, available: true },
    { id: 12, name: 'Dr. Kevin Nguyen', specialty: 'General Medicine', rating: 4.4, reviews: 88, available: true },
];

const SPECIALTIES = ['All Specialists', 'Cardiology', 'Pediatrics', 'General Medicine', 'Dermatology', 'Neurology', 'Oncology', 'Psychiatry', 'Orthopedics'];

function SelectSpecialist({ onNavigate, onSelectDoctor }) {
    const savedUser = JSON.parse(localStorage.getItem('user'));
    const [searchTerm, setSearchTerm] = useState('');
    const [activeFilter, setActiveFilter] = useState('All Specialists');
    const [visibleCount, setVisibleCount] = useState(8);

    const filteredDoctors = useMemo(() => {
        return DOCTORS.filter(doc => {
            const matchSearch = doc.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                doc.specialty.toLowerCase().includes(searchTerm.toLowerCase());
            const matchFilter = activeFilter === 'All Specialists' || doc.specialty === activeFilter;
            return matchSearch && matchFilter;
        });
    }, [searchTerm, activeFilter]);

    const totalFound = filteredDoctors.length;
    const visibleDoctors = filteredDoctors.slice(0, visibleCount);

    const handleSelect = (doctor) => {
        if (onSelectDoctor) onSelectDoctor(doctor);
    };

    return (
        <div className="specialist-page">
            <aside className="specialist-sidebar">
                <div className="sidebar-logo">
                    <span className="logo-dark">Appoint</span><span className="logo-blue">Med</span>
                </div>
                <nav className="sidebar-nav">
                    <button className="nav-item" onClick={() => onNavigate('DASHBOARD')}>
                        <span className="material-symbols-outlined">dashboard</span> Dashboard
                    </button>
                    <button className="nav-item" onClick={() => onNavigate('APPOINTMENTS')}>
                        <span className="material-symbols-outlined">calendar_today</span> Appointments
                    </button>
                    <button className="nav-item active">
                        <span className="material-symbols-outlined">stethoscope</span> Specialists
                    </button>
                    <button className="nav-item">
                        <span className="material-symbols-outlined">chat_bubble</span> Messages
                    </button>
                    <button className="nav-item" onClick={() => onNavigate('PROFILE')}>
                        <span className="material-symbols-outlined">settings</span> Settings
                    </button>
                </nav>
                <div className="sidebar-user">
                    <span className="material-symbols-outlined">account_circle</span>
                    <div>
                        <p className="user-name">{savedUser?.name || 'Alex Johnson'}</p>
                        <p className="user-role">Patient Account</p>
                    </div>
                </div>
            </aside>

            <main className="specialist-main">
                <div className="specialist-header">
                    <h1>Select Specialist</h1>
                    <p>Find and book the right healthcare professional for your needs. Browse by specialty or search for a specific practitioner.</p>
                </div>

                <div className="search-container">
                    <span className="material-symbols-outlined search-icon">search</span>
                    <input
                        type="text"
                        placeholder="Search doctors, specialties, or clinics"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </div>

                <div className="filter-chips">
                    {SPECIALTIES.map(spec => (
                        <button
                            key={spec}
                            className={`chip ${activeFilter === spec ? 'active' : ''}`}
                            onClick={() => { setActiveFilter(spec); setVisibleCount(8); }}
                        >
                            {spec}
                        </button>
                    ))}
                </div>

                <div className="results-header">
                    <h2>Available Specialists</h2>
                    <span className="results-count">{totalFound} practitioners found</span>
                </div>

                <div className="doctors-grid">
                    {visibleDoctors.map(doc => (
                        <div className="doctor-card" key={doc.id}>
                            <div className="card-avatar-wrapper">
                                <div className="card-avatar">
                                    <span className="material-symbols-outlined">person</span>
                                </div>
                                {doc.available && <span className="online-dot"></span>}
                            </div>
                            <h4>{doc.name}</h4>
                            <p className="card-specialty">{doc.specialty}</p>
                            <div className="card-rating">
                                <span className="star">★</span> {doc.rating} <span className="review-count">({doc.reviews} reviews)</span>
                            </div>
                            <button className="btn-select" onClick={() => handleSelect(doc)}>Select</button>
                        </div>
                    ))}
                </div>

                {visibleCount < totalFound && (
                    <div className="load-more-container">
                        <button className="btn-load-more" onClick={() => setVisibleCount(prev => prev + 8)}>
                            Load More Specialists
                        </button>
                    </div>
                )}
            </main>
        </div>
    );
}

export default SelectSpecialist;
