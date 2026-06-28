import type { GameSessionDTO, PlayResponseDTO, CardDTO, ScoreResponseDTO } from './types';

const API_BASE = import.meta.env.VITE_API_URL;

export const startGame = async (playerName: string = 'Player'): Promise<GameSessionDTO> => {
  const res = await fetch(`${API_BASE}/start?name=${encodeURIComponent(playerName)}`, { method: 'POST' });
  if (!res.ok) throw new Error('Error starting game');
  return res.json();
};

export const getState = async (sessionId: string): Promise<GameSessionDTO> => {
  const res = await fetch(`${API_BASE}/state`, {
    headers: { 'X-Session-ID': sessionId }
  });
  if (!res.ok) throw new Error('Error fetching state');
  return res.json();
};

export const playHand = async (sessionId: string, cards: CardDTO[]): Promise<PlayResponseDTO> => {
  const res = await fetch(`${API_BASE}/play`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-Session-ID': sessionId
    },
    body: JSON.stringify({ cards })
  });
  if (!res.ok) throw new Error('Error playing hand');
  return res.json();
};

export const evaluateHand = async (sessionId: string, cards: CardDTO[]): Promise<ScoreResponseDTO> => {
  const res = await fetch(`${API_BASE}/evaluate-hand`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-Session-ID': sessionId
    },
    body: JSON.stringify({ cards })
  });
  if (!res.ok) throw new Error('Error evaluating hand');
  return res.json();
};

export const discardCards = async (sessionId: string, cards: CardDTO[]): Promise<GameSessionDTO> => {
  const res = await fetch(`${API_BASE}/discard`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-Session-ID': sessionId
    },
    body: JSON.stringify({ cards })
  });
  if (!res.ok) throw new Error('Error discarding');
  return res.json();
};

export const nextRound = async (sessionId: string): Promise<GameSessionDTO> => {
  const res = await fetch(`${API_BASE}/next-round`, {
    method: 'POST',
    headers: { 'X-Session-ID': sessionId }
  });
  if (!res.ok) throw new Error('Error going to next round');
  return res.json();
};

export const buyItem = async (sessionId: string, type: 'joker' | 'tarot' | 'card', name: string): Promise<GameSessionDTO> => {
  const res = await fetch(`${API_BASE}/buy`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-Session-ID': sessionId
    },
    body: JSON.stringify({ type, name })
  });
  if (!res.ok) throw new Error('Error buying item');
  return res.json();
};

export const reorderJokers = async (sessionId: string, jokerName: string, direction: number): Promise<GameSessionDTO> => {
  const res = await fetch(`${API_BASE}/reorder-jokers`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-Session-ID': sessionId
    },
    body: JSON.stringify({ jokerName, direction })
  });
  if (!res.ok) throw new Error('Error reordering jokers');
  return res.json();
};

export const useTarot = async (sessionId: string, tarotName: string, targetCardNames: string[] = []): Promise<GameSessionDTO> => {
  const res = await fetch(`${API_BASE}/use-tarot`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-Session-ID': sessionId
    },
    body: JSON.stringify({ tarotName, targetCardNames })
  });
  if (!res.ok) throw new Error('Error using tarot');
  return res.json();
};

export const removeJoker = async (sessionId: string, jokerName: string): Promise<GameSessionDTO> => {
  const res = await fetch(`${API_BASE}/remove-joker`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-Session-ID': sessionId
    },
    body: JSON.stringify({ name: jokerName })
  });
  if (!res.ok) throw new Error('Error removing joker');
  return res.json();
};

export const removeTarot = async (sessionId: string, tarotName: string): Promise<GameSessionDTO> => {
  const res = await fetch(`${API_BASE}/remove-tarot`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-Session-ID': sessionId
    },
    body: JSON.stringify({ name: tarotName })
  });
  if (!res.ok) throw new Error('Error removing tarot');
  return res.json();
};

