import { useEffect, useState } from "react";
import { studentApi } from "../api/studentsApi";
import type { Student } from "../types/models";

export function StudentsPage() {
  const [students, setStudents] = useState<Student[]>([]);
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const [message, setMessage] = useState("");

  const load = async () => setStudents(await studentApi.list());
  useEffect(() => { void load(); }, []);

  const createStudent = async () => {
    await studentApi.create({ name, email });
    setName("");
    setEmail("");
    setMessage("Student created");
    await load();
  };

  const addProfile = async (id: number) => {
    await studentApi.upsertProfile(id, { phone, address: "Hyderabad", linkedinUrl: "https://linkedin.com/in/demo" });
    setPhone("");
    setMessage("Profile upserted");
    await load();
  };

  const deleteProfile = async (id: number) => {
    await studentApi.deleteProfile(id);
    setMessage("Profile deleted with orphan removal");
    await load();
  };

  return (
    <div className="grid two">
      <section className="card">
        <h2>Students</h2>
        <div className="inline"><input value={name} onChange={(e) => setName(e.target.value)} placeholder="Name" />
        <input value={email} onChange={(e) => setEmail(e.target.value)} placeholder="Email" /></div>
        <div className="inline" style={{ marginTop: 8 }}>
          <input value={phone} onChange={(e) => setPhone(e.target.value)} placeholder="Profile phone" />
          <button onClick={createStudent}>Create</button>
        </div>
        {message && <p className="success">{message}</p>}
      </section>

      <section className="card">
        <h2>OneToOne + OneToMany View</h2>
        <table>
          <thead><tr><th>Name</th><th>Profile</th><th>Enrollments</th><th>Action</th></tr></thead>
          <tbody>
            {students.map((s) => (
              <tr key={s.id}>
                <td>{s.name}<div className="badge">{s.email}</div></td>
                <td>{s.profile ? s.profile.phone : "No profile"}</td>
                <td>{s.enrollments.length}</td>
                <td>
                  <div className="inline">
                    <button className="secondary" onClick={() => addProfile(s.id)}>Upsert Profile</button>
                    <button className="danger" onClick={() => deleteProfile(s.id)}>Delete Profile</button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
    </div>
  );
}