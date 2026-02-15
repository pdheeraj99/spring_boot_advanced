import { useEffect, useMemo, useState } from "react";
import { debugApi } from "../api/debugApi";
import type { SqlTrace } from "../types/models";
import { Prism as SyntaxHighlighter } from "react-syntax-highlighter";
import { oneLight } from "react-syntax-highlighter/dist/esm/styles/prism";

export function SqlMonitorPage() {
  const [traces, setTraces] = useState<SqlTrace[]>([]);
  const [poll, setPoll] = useState(true);
  const [operation, setOperation] = useState("ALL");
  const [requestIdFilter, setRequestIdFilter] = useState("");
  const [selectedSql, setSelectedSql] = useState("");

  const load = async () => {
    const data = await debugApi.recentSql(100);
    setTraces(data.reverse());
  };

  useEffect(() => { void load(); }, []);
  useEffect(() => {
    if (!poll) return;
    const id = setInterval(() => { void load(); }, 2000);
    return () => clearInterval(id);
  }, [poll]);

  const filtered = useMemo(() => traces.filter((t) =>
    (operation === "ALL" || t.operation === operation) &&
    (!requestIdFilter || (t.requestId ?? "").includes(requestIdFilter))
  ), [traces, operation, requestIdFilter]);

  return (
    <div className="grid two">
      <section className="card">
        <h2>Live SQL Panel</h2>
        <div className="inline">
          <button onClick={() => setPoll((v) => !v)} className="secondary">{poll ? "Pause" : "Resume"} Poll</button>
          <button onClick={() => load()} className="secondary">Refresh</button>
          <button onClick={async () => { await debugApi.clearSql(); await load(); }} className="danger">Clear</button>
        </div>
        <div className="inline" style={{ marginTop: 8 }}>
          <select value={operation} onChange={(e) => setOperation(e.target.value)}>
            <option value="ALL">ALL</option>
            <option value="SELECT">SELECT</option>
            <option value="INSERT">INSERT</option>
            <option value="UPDATE">UPDATE</option>
            <option value="DELETE">DELETE</option>
          </select>
          <input value={requestIdFilter} onChange={(e) => setRequestIdFilter(e.target.value)} placeholder="Filter request id" />
        </div>
        <div className="sql-box" style={{ marginTop: 8 }}>
          <table>
            <thead><tr><th>Time</th><th>Op</th><th>Path</th><th>Request</th></tr></thead>
            <tbody>
              {filtered.map((t, i) => (
                <tr key={`${t.timestamp}-${i}`} onClick={() => setSelectedSql(t.sql)} style={{ cursor: "pointer" }}>
                  <td>{new Date(t.timestamp).toLocaleTimeString()}</td><td>{t.operation}</td><td>{t.path}</td><td>{t.requestId?.slice(0, 8)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
      <section className="card">
        <h2>SQL Detail</h2>
        <SyntaxHighlighter language="sql" style={oneLight}>{selectedSql || "Click any row to inspect SQL"}</SyntaxHighlighter>
      </section>
    </div>
  );
}