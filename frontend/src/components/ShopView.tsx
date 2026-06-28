import React, { useState } from 'react';
import type { GameSessionDTO } from '../types';
import * as api from '../api';
import { PokerChipsDecoration } from './PokerChipsDecoration';

interface Props {
  session: GameSessionDTO;
  onStateUpdate: (session: GameSessionDTO) => void;
  onNextRound: () => void;
}

// ── Small reusable confirm modal ──────────────────────────────────────────────
const ConfirmModal: React.FC<{
  message: string;
  onConfirm: () => void;
  onCancel: () => void;
}> = ({ message, onConfirm, onCancel }) => (
  <div className="fixed inset-0 z-50 flex items-center justify-center" style={{ background: 'rgba(0,0,0,0.7)' }}>
    <div
      className="flex flex-col items-center gap-6 p-10 rounded-2xl border-4 max-w-sm text-center"
      style={{ background: '#1a1a2e', borderColor: '#ef4444', boxShadow: '0 0 40px rgba(239,68,68,0.3)' }}
    >
      <span className="text-4xl">🗑️</span>
      <p className="text-white font-bold text-lg leading-relaxed">{message}</p>
      <div className="flex gap-4">
        <button
          onClick={onConfirm}
          className="px-6 py-2 rounded-full font-black text-white border-2 transition-all active:translate-y-0.5"
          style={{ background: '#dc2626', borderColor: '#7f1d1d', boxShadow: '0 3px 0 #7f1d1d' }}
        >
          Sí, eliminar
        </button>
        <button
          onClick={onCancel}
          className="px-6 py-2 rounded-full font-black border-2 transition-all active:translate-y-0.5"
          style={{ background: '#374151', borderColor: '#1f2937', color: '#d1d5db', boxShadow: '0 3px 0 #111' }}
        >
          Cancelar
        </button>
      </div>
    </div>
  </div>
);

export const ShopView: React.FC<Props> = ({ session, onStateUpdate, onNextRound }) => {
  const [isLoading, setIsLoading] = useState(false);
  const [confirm, setConfirm] = useState<{ type: 'joker' | 'tarot'; name: string } | null>(null);

  const withLoading = async (fn: () => Promise<GameSessionDTO>) => {
    setIsLoading(true);
    try {
      const res = await fn();
      onStateUpdate(res);
    } catch (e) {
      alert('Error al realizar la acción');
    } finally {
      setIsLoading(false);
    }
  };

  const handleNextRound = async () => {
    setIsLoading(true);
    try {
      const res = await api.nextRound(session.sessionId);
      onStateUpdate(res);
      onNextRound();
    } catch (e) {
      alert('Error al avanzar de ronda');
    } finally {
      setIsLoading(false);
    }
  };

  const handleBuy = (type: 'joker' | 'tarot' | 'card', name: string) =>
    withLoading(() => api.buyItem(session.sessionId, type, name));

  const handleReorder = (jokerName: string, direction: number) =>
    withLoading(() => api.reorderJokers(session.sessionId, jokerName, direction));

  const handleRemoveConfirmed = async () => {
    if (!confirm) return;
    const fn = confirm.type === 'joker'
      ? () => api.removeJoker(session.sessionId, confirm.name)
      : () => api.removeTarot(session.sessionId, confirm.name);
    setConfirm(null);
    await withLoading(fn);
  };

  const hasStoreItems =
    (session.store?.jokers?.length || 0) > 0 ||
    (session.store?.tarots?.length || 0) > 0 ||
    (session.store?.cards?.length || 0) > 0;

  const playerJokers = session.player.jokers || [];
  const playerTarots = session.player.tarots || [];

  const mapSuit = (suit: string) => {
    switch (suit) {
      case 'Corazones': return '♥';
      case 'Diamantes': return '♦';
      case 'Picas': return '♠';
      case 'Trebol': return '♣';
      default: return '?';
    }
  };
  const isRedSuit = (suit: string) => suit === 'Corazones' || suit === 'Diamantes';

  return (
    <div className="min-h-screen flex flex-col items-center gap-8 py-10 px-6 select-none shop-bg-animated overflow-hidden w-full relative">
      <PokerChipsDecoration />

      {/* Confirm modal */}
      {confirm && (
        <ConfirmModal
          message={`¿Eliminar "${confirm.name}" de tu inventario? Esta acción no se puede deshacer.`}
          onConfirm={handleRemoveConfirmed}
          onCancel={() => setConfirm(null)}
        />
      )}

      {/* Header */}
      <header className="text-center relative z-10">
        <h2
          className="text-6xl font-extrabold tracking-widest uppercase animate-text-glow-amber"
          style={{ color: '#fbbf24', textShadow: '0 0 32px rgba(251,191,36,0.4), 4px 4px 0 #0d1520' }}
        >
          🛒 La Tienda
        </h2>
        <p className="mt-3 text-lg font-bold" style={{ color: '#fcd34d' }}>
          Dinero disponible:{' '}
          <span className="text-yellow-300 font-black text-2xl">${session.player.money}</span>
        </p>
      </header>

      <div className="w-full max-w-7xl flex flex-col gap-8 relative z-10">

        {/* ── STORE ITEMS ──────────────────────────────────────────────────── */}
        <section>
          <h3 className="text-white/50 text-xs font-bold uppercase tracking-widest mb-4">Artículos disponibles</h3>
          <div className="flex flex-wrap gap-5 justify-center">

            {/* Jokers for sale */}
            {session.store?.jokers?.map((joker, i) => {
              const canBuy = session.player.money >= joker.cost && !isLoading && playerJokers.length < 5;
              return (
                <div
                  key={`store-joker-${i}`}
                  className="flex flex-col items-center justify-between gap-4 p-5 rounded-2xl border-2 w-52 transition-all duration-300 hover:-translate-y-2 hover:scale-105"
                  style={{ background: 'rgba(30,58,138,0.5)', borderColor: 'rgba(96,165,250,0.4)', boxShadow: '0 0 20px rgba(96,165,250,0.1)' }}
                >
                  <span className="text-5xl animate-bounce-slight">🃏</span>
                  <div className="text-center">
                    <p className="text-blue-200 font-black text-sm uppercase leading-tight">{joker.name}</p>
                    <p className="text-blue-300/70 text-xs mt-1 leading-snug">{joker.description}</p>
                  </div>
                  <button
                    onClick={() => handleBuy('joker', joker.name)}
                    disabled={!canBuy}
                    className="btn-interactive w-full py-2 rounded-full font-black text-sm border-2"
                    style={canBuy
                      ? { background: '#16a34a', borderColor: '#14532d', color: 'white', boxShadow: '0 3px 0 #14532d' }
                      : { background: '#374151', borderColor: '#1f2937', color: '#9ca3af' }}
                  >
                    {playerJokers.length >= 5 ? '🚫 Inventario lleno' : `Comprar $${joker.cost}`}
                  </button>
                </div>
              );
            })}

            {/* Tarots for sale */}
            {session.store?.tarots?.map((tarot, i) => {
              const canBuy = session.player.money >= tarot.cost && !isLoading && playerTarots.length < 2;
              return (
                <div
                  key={`store-tarot-${i}`}
                  className="flex flex-col items-center justify-between gap-4 p-5 rounded-2xl border-2 w-52 transition-all duration-300 hover:-translate-y-2 hover:scale-105"
                  style={{ background: 'rgba(76,29,149,0.5)', borderColor: 'rgba(167,139,250,0.4)', boxShadow: '0 0 20px rgba(167,139,250,0.1)' }}
                >
                  <span className="text-5xl animate-bounce-slight">🔮</span>
                  <div className="text-center">
                    <p className="text-purple-200 font-black text-sm uppercase leading-tight">{tarot.name}</p>
                    <p className="text-purple-300/70 text-xs mt-1 leading-snug">{tarot.description}</p>
                  </div>
                  <button
                    onClick={() => handleBuy('tarot', tarot.name)}
                    disabled={!canBuy}
                    className="btn-interactive w-full py-2 rounded-full font-black text-sm border-2"
                    style={canBuy
                      ? { background: '#7c3aed', borderColor: '#4c1d95', color: 'white', boxShadow: '0 3px 0 #4c1d95' }
                      : { background: '#374151', borderColor: '#1f2937', color: '#9ca3af' }}
                  >
                    {playerTarots.length >= 2 ? '🚫 Inventario lleno' : `Comprar $${tarot.cost}`}
                  </button>
                </div>
              );
            })}

            {/* Cards for sale */}
            {session.store?.cards?.map((card, i) => {
              const canBuy = session.player.money >= 4 && !isLoading;
              const red = isRedSuit(card.suit);
              return (
                <div
                  key={`store-card-${i}`}
                  className="flex flex-col items-center justify-between gap-4 p-5 rounded-2xl border-2 w-52 transition-all duration-300 hover:-translate-y-2 hover:scale-105"
                  style={{ background: 'rgba(120,53,15,0.4)', borderColor: 'rgba(251,191,36,0.4)', boxShadow: '0 0 20px rgba(251,191,36,0.1)' }}
                >
                  <div
                    className="w-16 h-22 rounded-xl border-2 bg-white flex flex-col items-center justify-center text-2xl font-black transition-transform hover:rotate-6 duration-200"
                    style={{ borderColor: '#1a1a1a', color: red ? '#dc2626' : '#1a1a1a' }}
                  >
                    <span className="text-xs font-black">{card.rank}</span>
                    <span>{mapSuit(card.suit)}</span>
                  </div>
                  <div className="text-center">
                    <p className="text-yellow-200 font-black text-sm uppercase">{card.rank} de {card.suit}</p>
                    <p className="text-yellow-300/70 text-xs mt-1">Añadir carta a tu mazo</p>
                  </div>
                  <button
                    onClick={() => handleBuy('card', card.suit)}
                    disabled={!canBuy}
                    className="btn-interactive w-full py-2 rounded-full font-black text-sm border-2"
                    style={canBuy
                      ? { background: '#d97706', borderColor: '#78350f', color: 'white', boxShadow: '0 3px 0 #78350f' }
                      : { background: '#374151', borderColor: '#1f2937', color: '#9ca3af' }}
                  >
                    Comprar $4
                  </button>
                </div>
              );
            })}

            {!hasStoreItems && (
              <div className="text-white/20 text-lg italic py-12">La tienda está vacía...</div>
            )}
          </div>
        </section>

        {/* ── PLAYER INVENTORY ─────────────────────────────────────────────── */}
        <section>
          <h3 className="text-white/50 text-xs font-bold uppercase tracking-widest mb-4">Tu inventario</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">

            {/* Jokers inventory */}
            <div
              className="rounded-2xl border-2 p-5"
              style={{ background: 'rgba(0,0,0,0.35)', borderColor: 'rgba(96,165,250,0.25)' }}
            >
              <div className="flex items-center justify-between mb-4">
                <span className="text-blue-300 text-xs font-bold uppercase tracking-widest">
                  🃏 Comodines ({playerJokers.length}/5)
                </span>
                <span className="text-blue-400/50 text-xs">← Orden de aplicación →</span>
              </div>

              {playerJokers.length === 0 ? (
                <div className="text-white/20 text-sm italic py-4 text-center">Sin comodines</div>
              ) : (
                <div className="flex flex-col gap-3">
                  {playerJokers.map((joker, index, arr) => (
                    <div
                      key={index}
                      className="flex items-center gap-3 rounded-xl border p-3 group transition-transform duration-200 hover:scale-[1.01]"
                      style={{ background: 'rgba(30,58,138,0.4)', borderColor: 'rgba(96,165,250,0.3)' }}
                    >
                      {/* Reorder buttons */}
                      <div className="flex flex-col gap-1">
                        <button
                          onClick={() => handleReorder(joker.name, -1)}
                          disabled={index === 0 || isLoading}
                          className="btn-interactive text-blue-300/60 hover:text-blue-200 disabled:opacity-20 text-xs font-bold leading-none px-1 py-1 rounded hover:bg-white/10"
                          title="Mover a la izquierda"
                        >
                          ▲
                        </button>
                        <button
                          onClick={() => handleReorder(joker.name, 1)}
                          disabled={index === arr.length - 1 || isLoading}
                          className="btn-interactive text-blue-300/60 hover:text-blue-200 disabled:opacity-20 text-xs font-bold leading-none px-1 py-1 rounded hover:bg-white/10"
                          title="Mover a la derecha"
                        >
                          ▼
                        </button>
                      </div>

                      {/* Position badge */}
                      <div
                        className="w-6 h-6 rounded-full flex items-center justify-center text-xs font-black shrink-0"
                        style={{ background: 'rgba(96,165,250,0.2)', color: '#93c5fd' }}
                      >
                        {index + 1}
                      </div>

                      {/* Icon */}
                      <span className="text-2xl shrink-0">🃏</span>

                      {/* Info */}
                      <div className="flex-1 min-w-0">
                        <p className="text-blue-100 font-black text-sm truncate">{joker.name}</p>
                        <p className="text-blue-300/60 text-xs leading-tight line-clamp-2">{joker.description}</p>
                      </div>

                      {/* Delete button */}
                      <button
                        onClick={() => setConfirm({ type: 'joker', name: joker.name })}
                        disabled={isLoading}
                        className="btn-interactive shrink-0 w-7 h-7 rounded-lg flex items-center justify-center text-red-400/40 hover:text-red-400 hover:bg-red-900/30"
                        title="Eliminar comodín"
                      >
                        🗑️
                      </button>
                    </div>
                  ))}
                </div>
              )}
            </div>

            {/* Tarots inventory */}
            <div
              className="rounded-2xl border-2 p-5"
              style={{ background: 'rgba(0,0,0,0.35)', borderColor: 'rgba(167,139,250,0.25)' }}
            >
              <div className="flex items-center justify-between mb-4">
                <span className="text-purple-300 text-xs font-bold uppercase tracking-widest">
                  🔮 Tarots ({playerTarots.length}/2)
                </span>
              </div>

              {playerTarots.length === 0 ? (
                <div className="text-white/20 text-sm italic py-4 text-center">Sin tarots</div>
              ) : (
                <div className="flex flex-col gap-3">
                  {playerTarots.map((tarot, index) => (
                    <div
                      key={index}
                      className="flex items-center gap-3 rounded-xl border p-3 transition-transform duration-200 hover:scale-[1.01]"
                      style={{ background: 'rgba(76,29,149,0.4)', borderColor: 'rgba(167,139,250,0.3)' }}
                    >
                      <span className="text-2xl shrink-0">🔮</span>
                      <div className="flex-1 min-w-0">
                        <p className="text-purple-100 font-black text-sm truncate">{tarot.name}</p>
                        <p className="text-purple-300/60 text-xs leading-tight line-clamp-2">{tarot.description}</p>
                      </div>
                      <button
                        onClick={() => setConfirm({ type: 'tarot', name: tarot.name })}
                        disabled={isLoading}
                        className="btn-interactive shrink-0 w-7 h-7 rounded-lg flex items-center justify-center text-red-400/40 hover:text-red-400 hover:bg-red-900/30"
                        title="Eliminar tarot"
                      >
                        🗑️
                      </button>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </section>
      </div>

      {/* Next round button */}
      <button
        onClick={handleNextRound}
        disabled={isLoading}
        className="btn-interactive px-16 py-5 text-2xl font-black uppercase tracking-widest rounded-full border-4 disabled:opacity-50 mt-4 relative z-10"
        style={{
          background: 'linear-gradient(to bottom, #3b82f6, #1d4ed8)',
          borderColor: '#1e3a8a',
          color: 'white',
          boxShadow: '0 6px 0 #1e3a8a, 0 0 24px rgba(59,130,246,0.3)',
        }}
      >
        Siguiente Ronda (Ante {session.currentAnte}) ➔
      </button>
    </div>
  );
};
