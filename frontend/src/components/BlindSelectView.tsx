import React from 'react';
import type { GameSessionDTO } from '../types';
import { PokerChipsDecoration } from './PokerChipsDecoration';

interface Props {
  session: GameSessionDTO;
  onEnterBlind: () => void;
}

export const BlindSelectView: React.FC<Props> = ({ session, onEnterBlind }) => {
  const blindNames = ['Ciega Pequeña', 'Ciega Grande', 'Ciega Jefe'];
  const blindColors = [
    { border: '#3b82f6', glow: 'rgba(59,130,246,0.4)', text: '#93c5fd', icon: '🔵' },
    { border: '#f59e0b', glow: 'rgba(245,158,11,0.4)', text: '#fcd34d', icon: '🟡' },
    { border: '#ef4444', glow: 'rgba(239,68,68,0.5)', text: '#fca5a5', icon: '💀' },
  ];
  const blindIdx = Math.max(0, Math.min(2, session.currentBlind - 1));
  const blindName = blindNames[blindIdx] ?? `Ciega ${session.currentBlind}`;
  const blindColor = blindColors[blindIdx];
  const isBoss = session.currentBlind === 3;

  return (
    <div className="flex flex-col items-center justify-center min-h-screen w-full gap-10 select-none relative table-bg-animated overflow-hidden">
      <PokerChipsDecoration />
      {/* Ante badge */}
      <div
        className="px-6 py-2 rounded-full border-2 text-sm font-bold uppercase tracking-widest"
        style={{ borderColor: blindColor.border, color: blindColor.text, background: 'rgba(0,0,0,0.4)' }}
      >
        Ante {session.currentAnte}
      </div>

      {/* Blind name + icon */}
      <div className="text-center">
        <div className="text-7xl mb-4">{blindColor.icon}</div>
        <h1
          className={`text-7xl font-extrabold uppercase tracking-widest ${isBoss ? 'animate-text-glow-red' : 'animate-text-glow-amber'}`}
          style={{
            color: blindColor.text,
            textShadow: `0 0 32px ${blindColor.glow}, 4px 4px 0 #0d1f0d`,
          }}
        >
          {blindName}
        </h1>
        {isBoss && (
          <p className="mt-4 text-red-400 font-bold text-lg tracking-wide animate-pulse">
            ⚠ El Jefe no tiene piedad. ⚠
          </p>
        )}
      </div>

      {/* Target score panel */}
      <div
        className="flex flex-col items-center gap-2 px-16 py-8 rounded-2xl border-4 backdrop-blur-sm"
        style={{
          borderColor: blindColor.border,
          background: 'rgba(0,0,0,0.5)',
          boxShadow: `0 0 40px ${blindColor.glow}`,
        }}
      >
        <p className="text-white/50 text-sm uppercase tracking-widest font-bold">Puntuación Objetivo</p>
        <p
          className="text-7xl font-black tabular-nums"
          style={{ color: blindColor.text, textShadow: `0 0 20px ${blindColor.glow}` }}
        >
          {(session.round?.targetScore ?? 0).toLocaleString()}
        </p>
      </div>

      {/* Player jokers preview (if any) */}
      {(session.player.jokers || []).length > 0 && (
        <div className="flex flex-col items-center gap-3">
          <p className="text-white/30 text-xs uppercase tracking-widest">Tus comodines</p>
          <div className="flex gap-3">
            {(session.player.jokers || []).map((j, i) => (
              <div
                key={i}
                className="flex flex-col items-center px-3 py-2 rounded-xl border text-center"
                style={{ borderColor: 'rgba(96,165,250,0.3)', background: 'rgba(30,58,138,0.4)' }}
                title={j.description}
              >
                <span className="text-xl">🃏</span>
                <span className="text-blue-200 text-[10px] font-bold mt-1 max-w-[64px] leading-tight">{j.name}</span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Enter button */}
      <button
        onClick={onEnterBlind}
        className="btn-interactive px-16 py-5 text-2xl font-black uppercase tracking-widest rounded-full border-4"
        style={{
          background: `linear-gradient(to bottom, ${blindColor.border}, ${blindColor.glow.replace('0.4)', '1)')})`,
          borderColor: isBoss ? '#7f1d1d' : (blindIdx === 1 ? '#92400e' : '#1e3a8a'),
          color: isBoss ? '#fff' : '#0d1f0d',
          boxShadow: `0 6px 0 ${blindIdx === 2 ? '#7f1d1d' : '#0d1f0d'}, 0 0 24px ${blindColor.glow}`,
        }}
      >
        ¡Jugar {blindName}!
      </button>
    </div>
  );
};
