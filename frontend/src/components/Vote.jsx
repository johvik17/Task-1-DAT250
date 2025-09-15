import { useEffect, useMemo, useState } from "react";
import { getPolls, voteOnPoll } from "../api";

export default function Vote() {
  const [polls, setPolls] = useState([]);
  const [pollId, setPollId] = useState("");     // keep as string in UI
  const [optionId, setOptionId] = useState(""); // keep as string in UI
  const [busy, setBusy] = useState(false);
  const [msg, setMsg] = useState(null);

  async function load() {
    try {
      const data = await getPolls();
      setPolls(data);
      if (!pollId && data.length) setPollId(String(data[0].id)); // ensure string
      setMsg(null);
    } catch (e) {
      setMsg({ ok: false, text: "Failed to load polls: " + e.message });
    }
  }
  useEffect(() => { load(); /* eslint-disable-next-line */ }, []);

  const current = polls.find(p => String(p.id) === String(pollId)); // compare as strings
  const totalVotes = useMemo(
    () => (current ? current.options.reduce((sum, o) => sum + (o.votes ?? 0), 0) : 0),
    [current]
  );

  async function submit(e) {
    e.preventDefault();
    if (!pollId || !optionId) return setMsg({ ok: false, text: "Choose a poll and an option." });
    setBusy(true);
    try {
      await voteOnPoll(Number(pollId), Number(optionId)); // send numbers to API
      await load();
      setMsg({ ok: true, text: "Vote recorded!" });
      setOptionId("");
    } catch (e) {
      setMsg({ ok: false, text: "Voting failed: " + e.message });
    } finally {
      setBusy(false);
    }
  }

  return (
    <div style={{ display: "grid", gap: 12 }}>
      <div className="row">
        <label style={{ flex: 1 }}>
          <span className="lbl">Poll</span>
          <select
            value={String(pollId)}
            onChange={(e) => { setPollId(e.target.value); setOptionId(""); }}
          >
            <option value="">— select —</option>
            {polls.map(p => (
              <option key={p.id} value={String(p.id)}>
                {p.question}
              </option>
            ))}
          </select>
        </label>
        <button type="button" className="btn secondary" onClick={load}>↻ Refresh</button>
      </div>

      {current && (
        <>
          <form onSubmit={submit} style={{ display: "grid", gap: 10 }}>
            <div className="kicker">{current.question}</div>
            {current.options.map(o => (
              <label key={o.id} className="row" style={{ justifyContent: "space-between" }}>
                <span className="row" style={{ gap: 10 }}>
                  <input
                    type="radio"
                    name="option"
                    value={String(o.id)}
                    checked={optionId === String(o.id)}
                    onChange={() => setOptionId(String(o.id))}
                  />
                  <span>{o.text}</span>
                </span>
                {typeof o.votes === "number" && (
                  <span className="kicker">votes: {o.votes}</span>
                )}
              </label>
            ))}
            <div className="row">
              <button className="btn" disabled={busy}>{busy ? "Submitting…" : "Submit vote"}</button>
              {msg && <div className={`notice ${msg.ok ? "ok" : "err"}`}>{msg.text}</div>}
            </div>
          </form>

          <div className="results">
            {current.options.map(o => {
              const v = o.votes ?? 0;
              const pct = totalVotes ? Math.round((v / totalVotes) * 100) : 0;
              return (
                <div key={o.id}>
                  <div className="bar-label">
                    <span>{o.text}</span>
                    <span>{v} • {pct}%</span>
                  </div>
                  <div className="bar" aria-valuenow={pct} aria-valuemin="0" aria-valuemax="100">
                    <div className="bar-fill" style={{ width: `${pct}%` }} />
                  </div>
                </div>
              );
            })}
            <div className="kicker">Total votes: {totalVotes}</div>
          </div>
        </>
      )}
    </div>
  );
}
