import { useEffect, useState } from "react";
import { courseApi } from "../api/coursesApi";
import { mentorApi } from "../api/mentorsApi";
import type { Course, Mentor } from "../types/models";

export function CoursesPage() {
  const [courses, setCourses] = useState<Course[]>([]);
  const [mentors, setMentors] = useState<Mentor[]>([]);
  const [title, setTitle] = useState("");
  const [fee, setFee] = useState(1000);

  const load = async () => {
    setCourses(await courseApi.list());
    setMentors(await mentorApi.list());
  };
  useEffect(() => { void load(); }, []);

  return (
    <div className="grid two">
      <section className="card">
        <h2>Courses</h2>
        <div className="inline"><input value={title} onChange={(e) => setTitle(e.target.value)} placeholder="Course title" />
        <input type="number" value={fee} onChange={(e) => setFee(Number(e.target.value))} /></div>
        <button style={{ marginTop: 8 }} onClick={async () => { await courseApi.create({ title, fee, active: true }); setTitle(""); await load(); }}>
          Create Course
        </button>
      </section>
      <section className="card">
        <h2>ManyToMany: Course {"<->"} Mentor</h2>
        <div className="table-wrap">
          <table><thead><tr><th>Course</th><th>Mentors</th><th>Link</th></tr></thead><tbody>
            {courses.map((c) => (
              <tr key={c.id}>
                <td>{c.title}</td>
                <td>{c.mentors.map((m) => m.name).join(", ") || "None"}</td>
                <td>
                  <select onChange={async (e) => { if (!e.target.value) return; await courseApi.linkMentor(c.id, Number(e.target.value)); await load(); }}>
                    <option value="">Assign mentor</option>
                    {mentors.map((m) => <option key={m.id} value={m.id}>{m.name}</option>)}
                  </select>
                </td>
              </tr>
            ))}
          </tbody></table>
        </div>
      </section>
    </div>
  );
}
