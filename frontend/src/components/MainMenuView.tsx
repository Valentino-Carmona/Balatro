import React, { useState } from 'react';
import * as api from '../api';
import type { GameSessionDTO } from '../types';
import { PokerChipsDecoration } from './PokerChipsDecoration';

interface Props {
  onGameStart: (session: GameSessionDTO) => void;
}

export const MainMenuView: React.FC<Props> = ({ onGameStart }) => {
  const [isLoading, setIsLoading] = useState(false);

  const handleStart = async () => {
    setIsLoading(true);
    try {
      const session = await api.startGame('Jugador 1');
      onGameStart(session);
    } catch (e) {
      alert("Error al contactar con el servidor backend.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen w-full gap-14 select-none relative table-bg-animated overflow-hidden">
      <PokerChipsDecoration />
      {/* Floating card suits decoration */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none opacity-10">
        {['♠', '♥', '♦', '♣'].map((s, i) => (
          <div
            key={i}
            className={`absolute text-white font-bold ${i % 2 === 0 ? 'animate-drift-slow' : 'animate-drift-slower'}`}
            style={{
              fontSize: `${8 + (i % 3) * 6}rem`,
              top: `${[10, 55, 20, 65][i]}%`,
              left: `${[5, 15, 80, 72][i]}%`,
              transform: `rotate(${[-15, 10, 20, -8][i]}deg)`,
              filter: 'blur(1px)',
            }}
          >
            {s}
          </div>
        ))}
      </div>

      {/* Logo */}
      <header className="text-center relative z-10">
        <div className="mb-4 flex justify-center gap-4 text-4xl">
          <span className="text-red-400 drop-shadow-lg">♥</span>
          <span className="text-white/60 drop-shadow-lg">♠</span>
          <span className="text-red-400 drop-shadow-lg">♦</span>
          <span className="text-white/60 drop-shadow-lg">♣</span>
        </div>
        <h1
          className="text-9xl font-extrabold tracking-widest drop-shadow-[0_4px_32px_rgba(0,0,0,0.8)] animate-text-glow-amber"
          style={{
            color: '#f5e6c8',
            textShadow: '0 0 40px rgba(245,230,200,0.3), 4px 4px 0 #0d1f0d',
            fontFamily: "'Rye', cursive",
          }}
        >
          BALATRO
        </h1>
        <p
          className="text-2xl mt-4 font-bold tracking-widest uppercase"
          style={{ color: '#c0392b', textShadow: '0 0 12px rgba(192,57,43,0.5)' }}
        >
          The Roguelike Edition
        </p>
      </header>

      {/* Start button */}
      <button
        onClick={handleStart}
        disabled={isLoading}
        className="relative z-10 btn-interactive px-20 py-6 text-3xl font-black uppercase tracking-widest rounded-full border-4"
        style={{
          background: isLoading
            ? '#1a5c1a'
            : 'linear-gradient(to bottom, #27ae60, #1e8449)',
          borderColor: '#0d4a1a',
          color: '#f0fff0',
          boxShadow: isLoading ? 'none' : '0 6px 0 #0d4a1a, 0 0 24px rgba(39,174,96,0.4)',
        }}
      >
        {isLoading ? '⟳ Barajando...' : '▶ Nueva Partida'}
      </button>

      {/* Bottom decorative text */}
      <p className="relative z-10 text-white/20 text-xs tracking-widest uppercase">
        MVP 2 - Valentino Carmona
      </p>
    </div>
  );
};
