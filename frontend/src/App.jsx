import CreatePoll from "./components/CreatePoll.jsx";
import Vote from "./components/Vote.jsx";

export default function App() {
  return (
    <div className="container">
      <header className="header">
        <h1 className="title">DAT250 – Polls</h1>
        <span className="subtitle">React + Spring</span>
      </header>

      <div className="grid">
        <section className="card">
          <div className="card-head">Create a new poll</div>
          <div className="card-body">
            <CreatePoll />
          </div>
        </section>

        <section className="card">
          <div className="card-head">Vote on a poll</div>
          <div className="card-body">
            <Vote />
          </div>
        </section>
      </div>
    </div>
  );
}
