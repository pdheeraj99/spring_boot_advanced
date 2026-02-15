import { NavLink, Route, Routes } from "react-router-dom";
import { StudentsPage } from "./pages/StudentsPage";
import { CoursesPage } from "./pages/CoursesPage";
import { MentorsPage } from "./pages/MentorsPage";
import { NPlusOnePage } from "./pages/NPlusOnePage";
import { SqlMonitorPage } from "./pages/SqlMonitorPage";

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
        <Routes>
          <Route path="/" element={<StudentsPage />} />
          <Route path="/students" element={<StudentsPage />} />
          <Route path="/courses" element={<CoursesPage />} />
          <Route path="/mentors" element={<MentorsPage />} />
          <Route path="/nplus1" element={<NPlusOnePage />} />
          <Route path="/sql-monitor" element={<SqlMonitorPage />} />
        </Routes>
      </main>
    </div>
  );
}