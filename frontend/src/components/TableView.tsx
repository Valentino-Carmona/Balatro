import React, { useState, useEffect, useRef } from 'react';
import type { GameSessionDTO, CardDTO, ScoreResponseDTO } from '../types';
import * as api from '../api';
import { PokerChipsDecoration } from './PokerChipsDecoration';

interface Props {
  session: GameSessionDTO;
  onStateUpdate: (session: GameSessionDTO) => void;
  onRoundWin: () => void;
  onGameOver: () => void;
}

// ─── Score Panel (Left Sidebar) ───────────────────────────────────────────────
interface ScorePanelProps {
  session: GameSessionDTO;
  liveScore: ScoreResponseDTO | null;
  lastScore: ScoreResponseDTO | null;
  activeEventName: string | null;
  activeEventType: string | null; // 'card' | 'joker' | 'base'
  isAnimating: boolean;
  chipKey: number;
  multKey: number;
}

const ScorePanel: React.FC<ScorePanelProps> = ({
  session, liveScore, lastScore, activeEventName, activeEventType, isAnimating, chipKey, multKey
}) => {
  // What to show: during animation use lastScore (updated step by step), then preview from liveScore
  const display = lastScore ?? liveScore;
  const isPreview = !lastScore && !!liveScore;

  // Derive blind name from currentBlind index (0=Small, 1=Big, 2=Boss)
  const blindNames = ['Ciega Pequeña', 'Ciega Grande', 'Ciega Jefe'];
  const blindName = blindNames[session.currentBlind] ?? `Ciega ${session.currentBlind + 1}`;

  const progress = session.round ? Math.min((session.round.playerScore / session.round.targetScore) * 100, 100) : 0;

  return (
    <aside className="flex flex-col gap-3 min-w-[200px] max-w-[220px] select-none">

      {/* Blind Badge */}
      <div className="bg-ink text-paper rounded-xl px-3 py-2 text-center border-4 border-paper/30 shadow-vintage">
        <div className="text-xs uppercase tracking-widest opacity-70">{blindName}</div>
        <div className="text-vintageRed font-bold text-lg leading-tight">Ante {session.currentAnte}</div>
      </div>

      {/* Score Progress */}
      <div className="bg-ink/90 rounded-xl px-3 py-2 border-4 border-paper/20 shadow-vintage">
        <div className="text-paper text-xs uppercase tracking-wider mb-1 opacity-70">Puntuación</div>
        <div className="flex justify-between items-end mb-1">
          <span className="text-white font-bold text-xl">{session.round?.playerScore ?? 0}</span>
          <span className="text-paper/50 text-xs">/ {session.round?.targetScore ?? '?'}</span>
        </div>
        <div className="h-3 bg-paper/20 rounded-full overflow-hidden">
          <div
            className="h-full bg-gradient-to-r from-yellow-400 to-green-400 rounded-full transition-all duration-700"
            style={{ width: `${progress}%` }}
          />
        </div>
      </div>

      {/* ── THE MAIN SCORE CALCULATOR ─────────── */}
      <div className={`bg-ink rounded-xl border-4 ${isPreview ? 'border-gray-500' : 'border-paper/30'} shadow-vintage px-3 py-3 flex flex-col items-center gap-1 transition-all duration-200`}>

        {/* Hand name */}
        <div className="h-6 flex items-center justify-center">
          {display?.handName ? (
            <span key={display.handName} className="text-yellow-300 font-bold text-sm uppercase tracking-wide animate-pop-in">
              {display.handName}
            </span>
          ) : (
            <span className="text-paper/30 text-xs uppercase tracking-wide">Seleccioná cartas</span>
          )}
        </div>

        {/* Chips × Mult — the BIG numbers */}
        <div className="flex items-center gap-2 my-1">
          {/* Blue Chip counter */}
          <div className="flex flex-col items-center">
            <span className="text-blue-300 text-[10px] font-bold uppercase tracking-wider">Fichas</span>
            <span
              key={chipKey}
              className={`text-5xl font-bold text-blue-400 leading-none drop-shadow-lg ${isAnimating && activeEventType !== 'joker' ? 'animate-glow-chip' : ''}`}
              style={{ fontVariantNumeric: 'tabular-nums', minWidth: '2ch', textAlign: 'center', textShadow: '0 0 10px rgba(96,165,250,0.4)' }}
            >
              {display?.points ?? 0}
            </span>
          </div>

          {/* × separator */}
          <div className="flex flex-col items-center pb-1">
            <span className="text-paper/40 text-2xl font-bold">×</span>
          </div>

          {/* Red Multiplier counter */}
          <div className="flex flex-col items-center">
            <span className="text-red-300 text-[10px] font-bold uppercase tracking-wider">Multi</span>
            <span
              key={multKey}
              className={`text-5xl font-bold text-red-400 leading-none drop-shadow-lg ${isAnimating && activeEventType === 'joker' ? 'animate-mult-hit' : ''}`}
              style={{ fontVariantNumeric: 'tabular-nums', minWidth: '2ch', textAlign: 'center', textShadow: '0 0 10px rgba(248,113,113,0.4)' }}
            >
              {display?.multiplier ?? 1}
            </span>
          </div>
        </div>

        {/* Separator */}
        <div className="w-full h-px bg-paper/20 my-1" />

        {/* Active event indicator */}
        <div className="h-5 flex items-center justify-center">
          {activeEventName ? (
            <span className="text-yellow-300 text-xs font-bold animate-pulse truncate max-w-full px-1">
              ✦ {activeEventName} ✦
            </span>
          ) : display ? (
            <span className="text-green-300 text-xs font-bold">
              = {display.totalScore.toLocaleString()}
            </span>
          ) : (
            <span className="text-paper/20 text-xs">—</span>
          )}
        </div>

        {isPreview && (
          <div className="text-paper/40 text-[10px] italic">vista previa</div>
        )}
      </div>

      {/* Hands & Discards */}
      <div className="grid grid-cols-2 gap-2">
        <div className="bg-blue-900/80 border-4 border-blue-400/50 rounded-xl p-2 text-center shadow-vintage">
          <div className="text-blue-300 text-[10px] uppercase tracking-wider font-bold">Manos</div>
          <div className="text-blue-100 text-3xl font-bold leading-none">{session.round?.handsLeft ?? 0}</div>
        </div>
        <div className="bg-red-900/80 border-4 border-red-400/50 rounded-xl p-2 text-center shadow-vintage">
          <div className="text-red-300 text-[10px] uppercase tracking-wider font-bold">Descartes</div>
          <div className="text-red-100 text-3xl font-bold leading-none">{session.round?.discardsLeft ?? 0}</div>
        </div>
      </div>

      {/* Money */}
      <div className="bg-yellow-900/80 border-4 border-yellow-500/50 rounded-xl px-3 py-2 text-center shadow-vintage">
        <div className="text-yellow-300 text-[10px] uppercase tracking-wider font-bold">Dinero</div>
        <div className="text-yellow-100 text-2xl font-bold">${session.player.money}</div>
      </div>

    </aside>
  );
};

// ─── Main Table View ───────────────────────────────────────────────────────────
export const TableView: React.FC<Props> = ({ session, onStateUpdate, onRoundWin, onGameOver }) => {
  const [selectedCards, setSelectedCards] = useState<CardDTO[]>([]);
  const [lastScore, setLastScore] = useState<ScoreResponseDTO | null>(null);
  const [liveScore, setLiveScore] = useState<ScoreResponseDTO | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isAnimating, setIsAnimating] = useState(false);
  const [activeEventName, setActiveEventName] = useState<string | null>(null);
  const [activeEventType, setActiveEventType] = useState<string | null>(null);
  // Keys to trigger CSS re-animations when numbers change
  const [chipKey, setChipKey] = useState(0);
  const [multKey, setMultKey] = useState(0);

  // Track previous chip/mult values to know which counter changed
  const prevChipsRef = useRef(0);
  const prevMultRef = useRef(1);

  // Live preview: call evaluate-hand whenever selection changes
  useEffect(() => {
    if (selectedCards.length > 0 && !isAnimating) {
      api.evaluateHand(session.sessionId, selectedCards)
        .then(res => setLiveScore(res))
        .catch(console.error);
    } else if (selectedCards.length === 0) {
      setLiveScore(null);
    }
  }, [selectedCards, session.sessionId, isAnimating]);

  // Reorder is only available in the Shop (ShopView), not during a round.

  const handleUseTarot = async (tarotName: string) => {
    setIsLoading(true);
    try {
      const targetCardNames = selectedCards.map(c => `${c.rank} de ${c.suit}`);
      const res = await api.useTarot(session.sessionId, tarotName, targetCardNames);
      onStateUpdate(res);
      setSelectedCards([]);
    } catch (e) {
      alert("Error al usar el tarot (¿Seleccionaste 1 carta válida?)");
    } finally {
      setIsLoading(false);
    }
  };

  const toggleCard = (card: CardDTO) => {
    if (isAnimating) return;
    setLastScore(null);
    if (selectedCards.find((c) => c.suit === card.suit && c.rank === card.rank)) {
      setSelectedCards(selectedCards.filter((c) => !(c.suit === card.suit && c.rank === card.rank)));
    } else {
      if (selectedCards.length < 5) {
        setSelectedCards([...selectedCards, card]);
      }
    }
  };

  const handlePlay = async () => {
    if (selectedCards.length === 0 || isAnimating) return;
    setIsLoading(true);
    setIsAnimating(true);
    setLiveScore(null);
    try {
      const res = await api.playHand(session.sessionId, selectedCards);
      setSelectedCards([]);

      const events = res.handScore.events;
      if (events && events.length > 0) {
        // Set base state first (from the 'base' event)
        const baseEvent = events.find(e => e.type === 'base');
        if (baseEvent) {
          setLastScore({
            points: baseEvent.currentTotalPoints,
            multiplier: baseEvent.currentTotalMult,
            totalScore: baseEvent.currentTotalPoints * baseEvent.currentTotalMult,
            handName: res.handScore.handName
          });
          prevChipsRef.current = baseEvent.currentTotalPoints;
          prevMultRef.current = baseEvent.currentTotalMult;
        }
        await new Promise(r => setTimeout(r, 500));

        // Animate each subsequent event
        for (const ev of events) {
          if (ev.type === 'base') continue; // already shown

          setActiveEventName(ev.name);
          setActiveEventType(ev.type);

          const chipsChanged = ev.currentTotalPoints !== prevChipsRef.current;
          const multChanged = ev.currentTotalMult !== prevMultRef.current;

          if (chipsChanged) setChipKey(k => k + 1);
          if (multChanged) setMultKey(k => k + 1);

          setLastScore({
            points: ev.currentTotalPoints,
            multiplier: ev.currentTotalMult,
            totalScore: ev.currentTotalPoints * ev.currentTotalMult,
            handName: res.handScore.handName
          });

          prevChipsRef.current = ev.currentTotalPoints;
          prevMultRef.current = ev.currentTotalMult;

          await new Promise(r => setTimeout(r, 450));
        }
      } else {
        // No events fallback
        setLastScore(res.handScore);
      }

      // Final state
      setActiveEventName(null);
      setActiveEventType(null);
      setLastScore(res.handScore);
      onStateUpdate(res.gameState);

      if (res.roundWon) {
        setTimeout(onRoundWin, 2500);
      } else if (res.gameState.gameOver) {
        setTimeout(onGameOver, 2500);
      }
    } catch (e) {
      alert("Error al jugar mano");
    } finally {
      setIsLoading(false);
      setIsAnimating(false);
    }
  };

  const handleDiscard = async () => {
    if (selectedCards.length === 0 || isAnimating) return;
    setIsLoading(true);
    try {
      const res = await api.discardCards(session.sessionId, selectedCards);
      setSelectedCards([]);
      onStateUpdate(res);
      setLastScore(null);
    } catch (e) {
      alert("Error al descartar");
    } finally {
      setIsLoading(false);
    }
  };

  const mapSuit = (suit: string) => {
    switch (suit) {
      case 'Corazones': return '♥';
      case 'Diamantes': return '♦';
      case 'Picas': return '♠';
      case 'Trebol': return '♣';
      default: return '?';
    }
  };

  const mapRank = (rank: string) => {
    switch (rank) {
      case 'Rey': return 'K';
      case 'Reina': return 'Q';
      case 'Jota': return 'J';
      case 'As': return 'A';
      default: return rank;
    }
  };

  const isRedSuit = (suit: string) => suit === 'Corazones' || suit === 'Diamantes';

  return (
    // ── Root: horizontal layout (sidebar + main table) ────────────────────────
    <div className="flex gap-5 w-full items-start min-h-screen p-4 table-bg-animated overflow-hidden relative">
      <PokerChipsDecoration />

      {/* ── LEFT SIDEBAR: Score Panel ────────────────────── */}
      <ScorePanel
        session={session}
        liveScore={liveScore}
        lastScore={lastScore}
        activeEventName={activeEventName}
        activeEventType={activeEventType}
        isAnimating={isAnimating}
        chipKey={chipKey}
        multKey={multKey}
      />

      {/* ── RIGHT: Main Play Area ────────────────────────── */}
      <div className="flex-1 flex flex-col gap-4 min-w-0">

        {/* Jokers & Tarots row */}
        <div className="flex gap-3">

          {/* Jokers zone */}
          <div className="flex-1 bg-black/40 border-2 border-white/10 rounded-xl p-3 min-h-[130px] backdrop-blur-sm">
            <div className="text-white/50 text-xs font-bold uppercase tracking-widest mb-2">
              Comodines ({(session.player.jokers || []).length}/5)
            </div>
            <div className="flex gap-3 flex-wrap animate-pop-in">
              {(session.player.jokers || []).map((joker, index) => (
                <div
                  key={index}
                  className={`w-20 h-28 bg-gradient-to-br from-blue-900 to-blue-700 border-2 rounded-xl shadow-vintage flex flex-col items-center justify-center p-1 text-center transition-transform hover:-translate-y-1 cursor-default ${activeEventName === joker.name ? 'border-yellow-400 shadow-yellow-400/50 scale-110 animate-wiggle' : 'border-blue-400/50'}`}
                  title={joker.description}
                >
                  <span className="text-2xl mb-1">🃏</span>
                  <span className="text-white font-bold text-[10px] leading-tight">{joker.name}</span>
                  <span className="text-blue-200 text-[8px] mt-0.5 leading-tight opacity-80">{joker.description}</span>
                </div>
              ))}
              {(session.player.jokers || []).length === 0 && (
                <div className="text-white/20 text-sm italic m-auto">Sin comodines</div>
              )}
            </div>
          </div>

          {/* Tarots zone */}
          <div className="w-52 bg-black/40 border-2 border-purple-500/20 rounded-xl p-3 min-h-[130px] backdrop-blur-sm">
            <div className="text-purple-300/70 text-xs font-bold uppercase tracking-widest mb-2">
              Tarots ({(session.player.tarots || []).length}/2)
            </div>
            <div className="flex flex-col gap-2 animate-pop-in">
              {(session.player.tarots || []).map((tarot, index) => (
                <div key={index} className="bg-purple-900/60 border border-purple-500/40 rounded-lg p-2 flex flex-col gap-1">
                  <span className="text-purple-100 font-bold text-xs">{tarot.name}</span>
                  <span className="text-purple-300 text-[10px] leading-tight opacity-80">{tarot.description}</span>
                  <button
                    onClick={() => handleUseTarot(tarot.name)}
                    disabled={isLoading || isAnimating}
                    className="btn-interactive bg-purple-600 text-white text-xs font-bold py-0.5 px-2 rounded mt-0.5"
                  >
                    Usar
                  </button>
                </div>
              ))}
              {(session.player.tarots || []).length === 0 && (
                <div className="text-purple-300/20 text-xs italic">Sin tarots</div>
              )}
            </div>
          </div>
        </div>

        {/* Cards in hand area */}
        <div className="bg-black/30 border-2 border-white/10 rounded-2xl p-4 relative backdrop-blur-sm">
          <div className="text-white/40 text-xs font-bold uppercase tracking-widest mb-3">
            Cartas en mano ({session.player.handCards.length})
          </div>

          <div className="flex justify-center flex-wrap gap-3 min-h-[170px] items-end pb-3 pt-6 px-4">
            {session.player.handCards.map((card, i) => {
              const isSelected = selectedCards.some(c => c.suit === card.suit && c.rank === card.rank);
              const red = isRedSuit(card.suit);
              // Dynamic arcing fan effect
              const totalCards = session.player.handCards.length;
              const mid = (totalCards - 1) / 2;
              const angle = (i - mid) * 3; // 3 degrees separation
              const translateY = Math.abs(i - mid) * 2; // arch shape offset
              
              return (
                <div
                  key={`${card.suit}-${card.rank}-${i}`}
                  style={{
                    transform: isSelected ? 'none' : `rotate(${angle}deg) translateY(${translateY}px)`,
                    transformOrigin: 'bottom center',
                  }}
                  className="transition-transform duration-300 ease-out"
                >
                  <button
                    onClick={() => toggleCard(card)}
                    disabled={isAnimating}
                    className={`
                      relative w-[76px] h-[106px] rounded-xl border-3 font-bold select-none card-deal card-hover-vibe
                      ${isSelected
                        ? 'border-yellow-400 -translate-y-8 shadow-[0_8px_24px_rgba(250,204,21,0.5)] bg-yellow-50 scale-105'
                        : 'border-gray-300 bg-white hover:-translate-y-3 hover:shadow-lg hover:scale-105'
                      }
                      ${isAnimating ? 'cursor-default' : 'cursor-pointer'}
                    `}
                    style={{ boxShadow: isSelected ? '0 0 0 3px #facc15, 4px 4px 0 #1A1A1A' : '3px 3px 0 #1A1A1A' }}
                  >
                    {/* Top-left rank & suit */}
                    <div className={`absolute top-1.5 left-2 text-left leading-none ${red ? 'text-red-600' : 'text-gray-900'}`}>
                      <div className="text-base font-black">{mapRank(card.rank)}</div>
                      <div className="text-sm leading-none">{mapSuit(card.suit)}</div>
                    </div>
                    {/* Center suit */}
                    <div className={`text-3xl ${red ? 'text-red-500' : 'text-gray-800'}`}>
                      {mapSuit(card.suit)}
                    </div>
                    {/* Bottom-right rank & suit (rotated) */}
                    <div className={`absolute bottom-1.5 right-2 text-right leading-none rotate-180 ${red ? 'text-red-600' : 'text-gray-900'}`}>
                      <div className="text-base font-black">{mapRank(card.rank)}</div>
                      <div className="text-sm leading-none">{mapSuit(card.suit)}</div>
                    </div>
                  </button>
                </div>
              );
            })}
          </div>
        </div>

        {/* Action Buttons */}
        <div className="flex gap-4 justify-center">
          <button
            onClick={handlePlay}
            disabled={selectedCards.length === 0 || isLoading || isAnimating || (session.round?.handsLeft ?? 0) <= 0}
            className={`
              btn-interactive
              bg-gradient-to-b from-red-500 to-red-700 text-white border-4 border-red-900
              px-8 py-3 rounded-full font-black text-lg uppercase tracking-widest
              shadow-[4px_4px_0_#7f1d1d]
              ${selectedCards.length > 0 && !isAnimating ? 'animate-pulse-subtle shadow-[0_0_20px_rgba(220,38,38,0.5)]' : ''}
            `}
          >
            {isAnimating ? '✦ Calculando...' : '▶ Jugar Mano'}
          </button>

          <button
            onClick={handleDiscard}
            disabled={selectedCards.length === 0 || isLoading || isAnimating || (session.round?.discardsLeft ?? 0) <= 0}
            className="
              btn-interactive
              bg-gradient-to-b from-gray-500 to-gray-700 text-white border-4 border-gray-900
              px-8 py-3 rounded-full font-black text-lg uppercase tracking-widest
              shadow-[4px_4px_0_#111]
            "
          >
            ✕ Descartar
          </button>
        </div>

      </div>
    </div>
  );
};
