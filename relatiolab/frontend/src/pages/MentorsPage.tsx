import { useEffect, useState } from "react";
import { mentorApi } from "../api/mentorsApi";
import type { Mentor, Skill } from "../types/models";

export function MentorsPage() {
  const [mentors, setMentors] = useState<Mentor[]>([]);
  const [skills, setSkills] = useState<Skill[]>([]);
  const [name, setName] = useState("");
  const [level, setLevel] = useState("SENIOR");
  const [skillCode, setSkillCode] = useState("JPA");
  const [skillName, setSkillName] = useState("Spring Data JPA");

  const load = async () => {
    setMentors(await mentorApi.list());
    setSkills(await mentorApi.listSkills());
  };

  useEffect(() => { void load(); }, []);

  return (
    <div className="grid two">
      <section className="card">
        <h2>Mentor + Skill</h2>
        <div className="inline"><input value={name} onChange={(e) => setName(e.target.value)} placeholder="Mentor" />
        <input value={level} onChange={(e) => setLevel(e.target.value)} placeholder="Level" /></div>
        <button style={{ marginTop: 8 }} onClick={async () => { await mentorApi.create({ name, expertiseLevel: level }); setName(""); await load(); }}>Create Mentor</button>
        <div className="inline" style={{ marginTop: 12 }}><input value={skillCode} onChange={(e) => setSkillCode(e.target.value)} placeholder="Skill code" />
        <input value={skillName} onChange={(e) => setSkillName(e.target.value)} placeholder="Skill name" /></div>
        <button style={{ marginTop: 8 }} className="secondary" onClick={async () => { await mentorApi.createSkill({ code: skillCode, displayName: skillName }); await load(); }}>Create Skill</button>
      </section>
      <section className="card">
        <h2>ManyToMany: Mentor {"<->"} Skill</h2>
        <div className="table-wrap">
          <table><thead><tr><th>Mentor</th><th>Skills</th><th>Link</th></tr></thead><tbody>
            {mentors.map((m) => (
              <tr key={m.id}>
                <td>{m.name}</td>
                <td>{m.skills.map((s) => s.code).join(", ") || "None"}</td>
                <td><select onChange={async (e) => { if (!e.target.value) return; await mentorApi.linkSkill(m.id, Number(e.target.value)); await load(); }}>
                  <option value="">Assign skill</option>
                  {skills.map((s) => <option value={s.id} key={s.id}>{s.code}</option>)}
                </select></td>
              </tr>
            ))}
          </tbody></table>
        </div>
      </section>
    </div>
  );
}
