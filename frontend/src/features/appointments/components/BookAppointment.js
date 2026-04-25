import React, { useState, useMemo } from 'react';
import './BookAppointment.css';

const MORNING_SLOTS = ['09:00 AM', '09:30 AM', '10:00 AM', '10:45 AM', '11:15 AM', '11:45 AM'];
const AFTERNOON_SLOTS = ['02:00 PM', '02:30 PM', '03:15 PM', '04:00 PM'];

function BookAppointment({ doctor, onBack, onConfirm }) {
    const [selectedDate, setSelectedDate] = useState(null);
    const [selectedSlot, setSelectedSlot] = useState(null);
    const [reason, setReason] = useState('');
    const [currentMonth, setCurrentMonth] = useState(new Date(2023, 9)); // October 2023

    const year = currentMonth.getFullYear();
    const month = currentMonth.getMonth();
    const monthName = currentMonth.toLocaleString('default', { month: 'long' });

    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const firstDayOfWeek = new Date(year, month, 1).getDay(); // 0=Sun

    const calendarCells = useMemo(() => {
        const cells = [];
        for (let i = 0; i < firstDayOfWeek; i++) cells.push(null);
        for (let d = 1; d <= daysInMonth; d++) cells.push(d);
        return cells;
    }, [firstDayOfWeek, daysInMonth]);

    const today = new Date();
    const isToday = (day) => day && year === today.getFullYear() && month === today.getMonth() && day === today.getDate();
    const isPast = (day) => {
        if (!day) return true;
        const cellDate = new Date(year, month, day);
        const todayClear = new Date(today.getFullYear(), today.getMonth(), today.getDate());
        return cellDate < todayClear;
    };

    const prevMonth = () => setCurrentMonth(new Date(year, month - 1));
    const nextMonth = () => setCurrentMonth(new Date(year, month + 1));

    const handleConfirm = () => {
        if (!selectedDate || !selectedSlot) {
            alert('Please select a date and time slot.');
            return;
        }
        const bookingDetails = {
            doctor,
            date: `${monthName} ${selectedDate}, ${year}`,
            time: selectedSlot,
            reason
        };
        if (onConfirm) onConfirm(bookingDetails);
    };

    // Breadcrumb step
    const step = !selectedDate ? 1 : !selectedSlot ? 2 : 3;

    return (
        <div className="booking-page">
            <header className="booking-topbar">
                <div className="booking-logo">
                    <span className="logo-dark">Appoint</span><span className="logo-blue">Med</span>
                </div>
                <div className="topbar-actions">
                    <button className="icon-btn"><span className="material-symbols-outlined">notifications</span></button>
                    <button className="icon-btn"><span className="material-symbols-outlined">calendar_month</span></button>
                </div>
            </header>

            <div className="booking-breadcrumb">
                <span className={step >= 1 ? 'bc-active' : ''}>Select Doctor</span>
                <span className="bc-sep">›</span>
                <span className={step >= 1 ? 'bc-bold' : ''}>Select Date &amp; Time</span>
                <span className="bc-sep">›</span>
                <span className={step >= 3 ? '' : 'bc-muted'}>Confirm Booking</span>
            </div>

            <div className="booking-layout">
                {/* Left Sidebar - Doctor Info */}
                <aside className="booking-sidebar">
                    <div className="selected-doctor-card">
                        <p className="label-upper">SELECTED PROFESSIONAL</p>
                        <div className="doc-info">
                            <div className="doc-avatar">
                                <span className="material-symbols-outlined">person</span>
                            </div>
                            <div>
                                <h3>{doctor?.name || 'Dr. Sarah Jenkins'}</h3>
                                <p className="doc-specialty">{doctor?.specialty ? `Senior ${doctor.specialty}` : 'Senior Cardiologist'}</p>
                                <p className="doc-exp">15 years experience</p>
                            </div>
                        </div>
                        <div className="doc-meta-list">
                            <p>★ {doctor?.rating || 4.9} ({doctor?.reviews || 120}+ reviews)</p>
                            <p><span className="material-symbols-outlined">location_on</span> Central Medical Plaza, NY</p>
                            <p><span className="material-symbols-outlined">payments</span> ₱150 - ₱200 per visit</p>
                        </div>
                        <button className="btn-change-doc" onClick={onBack}>Change Doctor</button>
                    </div>

                    <div className="booking-note">
                        <p className="note-title"><span className="material-symbols-outlined">info</span> Booking Note</p>
                        <p>Cancellations are accepted up to 24 hours before the appointment. Please arrive 10 minutes early for check-in.</p>
                    </div>
                </aside>

                {/* Right Content - Calendar + Time */}
                <div className="booking-content">
                    {/* Step 1: Calendar */}
                    <section className="booking-section">
                        <h2><span className="step-num">1.</span> Select Date</h2>
                        <div className="calendar-header">
                            <button onClick={prevMonth} className="cal-nav">‹</button>
                            <span className="cal-month">{monthName} {year}</span>
                            <button onClick={nextMonth} className="cal-nav">›</button>
                        </div>
                        <div className="calendar-grid">
                            {['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'].map(d => (
                                <div key={d} className="cal-day-header">{d}</div>
                            ))}
                            {calendarCells.map((day, i) => (
                                <button
                                    key={i}
                                    className={`cal-day ${!day ? 'empty' : ''} ${selectedDate === day ? 'selected' : ''} ${isToday(day) ? 'today' : ''} ${isPast(day) ? 'past' : ''}`}
                                    onClick={() => day && !isPast(day) && setSelectedDate(day)}
                                    disabled={!day || isPast(day)}
                                >
                                    {day}
                                </button>
                            ))}
                        </div>
                    </section>

                    {/* Step 2: Time Slots */}
                    <section className="booking-section">
                        <h2><span className="step-num">2.</span> Available Time Slots</h2>
                        <div className="time-group">
                            <p className="time-label"><span className="material-symbols-outlined">light_mode</span> MORNING</p>
                            <div className="time-slots">
                                {MORNING_SLOTS.map(slot => (
                                    <button
                                        key={slot}
                                        className={`time-slot ${selectedSlot === slot ? 'selected' : ''}`}
                                        onClick={() => setSelectedSlot(slot)}
                                    >{slot}</button>
                                ))}
                            </div>
                        </div>
                        <div className="time-group">
                            <p className="time-label"><span className="material-symbols-outlined">dark_mode</span> AFTERNOON</p>
                            <div className="time-slots">
                                {AFTERNOON_SLOTS.map(slot => (
                                    <button
                                        key={slot}
                                        className={`time-slot ${selectedSlot === slot ? 'selected' : ''}`}
                                        onClick={() => setSelectedSlot(slot)}
                                    >{slot}</button>
                                ))}
                            </div>
                        </div>
                    </section>

                    {/* Step 3: Reason */}
                    <section className="booking-section">
                        <h2><span className="step-num">3.</span> Reason for Visit</h2>
                        <textarea
                            className="reason-input"
                            placeholder="Please describe your symptoms or reason for the appointment (optional)..."
                            value={reason}
                            onChange={(e) => setReason(e.target.value)}
                            rows={4}
                        />
                    </section>

                    {/* Actions */}
                    <div className="booking-actions">
                        <button className="btn-back" onClick={onBack}>
                            <span className="material-symbols-outlined">arrow_back</span> Back
                        </button>
                        <button className="btn-confirm" onClick={handleConfirm}>
                            Confirm Booking <span className="material-symbols-outlined">check_circle</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default BookAppointment;
