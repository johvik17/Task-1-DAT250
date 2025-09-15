import { useState } from "react";
import { createPoll } from "../api";

export default function CreatePoll() {
  const [question, setQuestion] = useState("");
  const [options, setOptions] = useState(["Yes", "No"]);
  const [busy, setBusy] = useState(false);
  const [msg, setMsg] = useState(null);

  const addOption = () => setOptions(xs => [...xs, ""]);
  const changeOption = (i, v) => setOptions(xs => xs.map((x, idx) => (i === idx ? v : x)));
  const removeOption = (i) => setOptions(xs => xs.filter((_, idx) => i !== idx));

  async function submit(e) {
    e.preventDefault();
    setMsg(null);
    setBusy(true);
    try {
      const opts = options.map(s => s.trim()).filter(Boolean);
      if (!question.trim() || opts.length < 2) throw new Error("Question + 2 options required");
      await createPoll(question.trim(), opts);
      setMsg({ ok: true, text: "Poll created!" });
      setQuestion("");
      setOptions(["Yes", "No"]);
    } catch (err) {
      setMsg({ ok: false, text: err.message });
    } finally {
      setBusy(false);
    }
  }

  return (
    <form onSubmit={submit} style={{ display: "grid", gap: 12 }}>
      <label>
        <span className="lbl">Question</span>
        <textarea
          value={question}
          onChange={(e) => setQuestion(e.target.value)}
          placeholder="What should our team mascot be?"
          required
        />
      </label>

      <div>
        <div className="row" style={{ justifyContent: "space-between", marginBottom: 6 }}>
          <span className="kicker">Options</span>
          <button type="button" className="btn secondary" onClick={addOption}>+ Add option</button>
        </div>

        {options.map((opt, i) => (
          <div className="option-row" key={i}>
            <input
              type="text"
              value={opt}
              placeholder={`Option #${i + 1}`}
              onChange={(e) => changeOption(i, e.target.value)}
              required
            />
            <button className="icon-btn" type="button" onClick={() => removeOption(i)} title="Remove">
              🗑
            </button>
          </div>
        ))}
      </div>

      <div className="row">
        <button className="btn" disabled={busy}>{busy ? "Creating…" : "Create Poll"}</button>
        {msg && <div className={`notice ${msg.ok ? "ok" : "err"}`}>{msg.text}</div>}
      </div>
    </form>
  );
}
