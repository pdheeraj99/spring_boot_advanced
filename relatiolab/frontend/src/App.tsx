import { lazy, Suspense } from "react";
import { NavLink, Route, Routes } from "react-router-dom";

const StudentsPage = lazy(() => import("./pages/StudentsPage").then((m) => ({ default: m.StudentsPage })));
const CoursesPage = lazy(() => import("./pages/CoursesPage").then((m) => ({ default: m.CoursesPage })));
const MentorsPage = lazy(() => import("./pages/MentorsPage").then((m) => ({ default: m.MentorsPage })));
const NPlusOnePage = lazy(() => import("./pages/NPlusOnePage").then((m) => ({ default: m.NPlusOnePage })));
const SqlMonitorPage = lazy(() => import("./pages/SqlMonitorPage").then((m) => ({ default: m.SqlMonitorPage })));

const navItems = [
  ["/students", "Students"],
  ["/courses", "Courses"],
  ["/mentors", "Mentors"],
  ["/nplus1", "N+1 Lab"],
  ["/sql-monitor", "SQL Monitor"]
];

export default function App() {
  return (
    <div className="app-shell">
      <header className="hero">
        <h1>RelatioLab</h1>
        <p>JPA/Hibernate relationships and N+1 debugging playground</p>
      </header>
      <nav className="top-nav">
        {navItems.map(([to, label]) => (
          <NavLink key={to} to={to} className={({ isActive }) => (isActive ? "nav-link active" : "nav-link")}>
            {label}
          </NavLink>
        ))}
      </nav>
      <main>
        <Suspense fallback={<div className="card shimmer">Loading page...</div>}>
          <Routes>
            <Route path="/" element={<StudentsPage />} />
            <Route path="/students" element={<StudentsPage />} />
            <Route path="/courses" element={<CoursesPage />} />
            <Route path="/mentors" element={<MentorsPage />} />
            <Route path="/nplus1" element={<NPlusOnePage />} />
            <Route path="/sql-monitor" element={<SqlMonitorPage />} />
          </Routes>
        </Suspense>
      </main>
    </div>
  );
}
