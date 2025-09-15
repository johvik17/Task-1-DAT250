const BASE = import.meta.env.VITE_API_BASE_URL || "/api/";


const jh = { "Content-Type": "application/json" };

export async function getPolls() {
  const r = await fetch(`${BASE}polls`, { headers: jh });
  if (!r.ok) throw new Error(`${r.status} ${r.statusText}`);
  return r.json();
}

export async function createPoll(question, options) {
  const body = { question, options }; // ev. options.map(text => ({ text }))
  const r = await fetch(`${BASE}polls`, {
    method: "POST",
    headers: jh,
    body: JSON.stringify(body),
  });
  if (!r.ok) throw new Error(await r.text());
  return r.json();
}

export async function voteOnPoll(pollId, optionId) {
  const r = await fetch(`${BASE}polls/${pollId}/vote`, {
    method: "POST", // bruk PUT hvis ditt API krever det
    headers: jh,
    body: JSON.stringify({ optionId }),
  });
  if (!r.ok) throw new Error(await r.text());
  return r.json();
}
