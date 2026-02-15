import { useState } from "react";
import { debugApi } from "../api/debugApi";
import type { FetchComparison } from "../types/models";

export function NPlusOnePage() {
  const [mode, setMode] = useState("bad");
  const [scenario, setScenario] = useState("students");
  const [result, setResult] = useState<FetchComparison | null>(null);

  const run = async () => {
    const data = scenario === "students"
      ? await debugApi.nplus1Students(mode)
      : scenario === "courses"
        ? await debugApi.nplus1Courses(mode)
        : await debugApi.enrollmentReport(mode);
    setResult(data);
  };

  return (
    <div className="grid two">
      <section className="card">
        <h2>N+1 Scenario Runner</h2>
        <label>Scenario</label>
        <select value={scenario} onChange={(e) => setScenario(e.target.value)}>
          <option value="students">Students + Enrollments</option>
          <option value="courses">Courses + Mentors</option>
          <option value="enrollments">Enrollment Report</option>
        </select>
        <label style={{ marginTop: 8 }}>Mode</label>
        <select value={mode} onChange={(e) => setMode(e.target.value)}>
          <option value="bad">BAD (N+1)</option>
          <option value="join-fetch">JOIN FETCH</option>
          <option value="entity-graph">@EntityGraph</option>
          <option value="batch">Batch Fetching</option>
        </select>
        <button style={{ marginTop: 8 }} onClick={run}>Run Comparison</button>
      </section>
      <section className="card">
        <h2>Comparison Output</h2>
        {result ? (
          <>
            <p><span className="badge">Scenario: {result.scenario}</span> <span className="badge">Mode: {result.mode}</span></p>
            <p>Query Count: <strong>{result.queryCount}</strong></p>
            <p>Select Count: <strong>{result.selectCount}</strong></p>
            <p>Duration: <strong>{result.durationMs} ms</strong></p>
            <textarea readOnly rows={10} value={JSON.stringify(result.data, null, 2)} />
          </>
        ) : <p>Run one scenario to see metrics.</p>}
      </section>
    </div>
  );
}