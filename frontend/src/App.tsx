import { useState } from 'react';
import type { GameSessionDTO } from './types';
import { MainMenuView } from './components/MainMenuView';
import { BlindSelectView } from './components/BlindSelectView';
import { TableView } from './components/TableView';
import { ShopView } from './components/ShopView';

type ViewState = 'MENU' | 'BLIND_SELECT' | 'TABLE' | 'SHOP' | 'GAME_OVER';

// Views that use the dark table background and need full-screen treatment
const DARK_VIEWS: ViewState[] = ['MENU', 'BLIND_SELECT', 'TABLE', 'GAME_OVER'];

function App() {
  const [view, setView] = useState<ViewState>('MENU');
  const [session, setSession] = useState<GameSessionDTO | null>(null);

  const handleGameStart = (newSession: GameSessionDTO) => {
    setSession(newSession);
    setView('BLIND_SELECT');
  };

  const handleStateUpdate = (updatedSession: GameSessionDTO) => {
    setSession(updatedSession);
  };

  const handleRoundWin = () => {
    setView('SHOP');
  };

  const handleGameOver = () => {
    setView('GAME_OVER');
  };

  const handleNextRound = () => {
    setView('BLIND_SELECT');
  };

  const isDark = DARK_VIEWS.includes(view);

  return (
    <div className={isDark ? 'min-h-screen' : 'min-h-screen flex flex-col items-center py-12 px-4'}>
      {view === 'MENU' && (
        <MainMenuView onGameStart={handleGameStart} />
      )}

      {view === 'BLIND_SELECT' && session && (
        <BlindSelectView
          session={session}
          onEnterBlind={() => setView('TABLE')}
        />
      )}

      {view === 'TABLE' && session && (
        <TableView
          session={session}
          onStateUpdate={handleStateUpdate}
          onRoundWin={handleRoundWin}
          onGameOver={handleGameOver}
        />
      )}

      {view === 'SHOP' && session && (
        <ShopView
          session={session}
          onStateUpdate={handleStateUpdate}
          onNextRound={handleNextRound}
        />
      )}

      {view === 'GAME_OVER' && (
        <div
          className="flex flex-col items-center justify-center min-h-screen gap-12 select-none"
          style={{ background: 'radial-gradient(ellipse at 50% 30%, #3a0a0a 0%, #1a0404 100%)' }}
        >
          {/* Skull decoration */}
          <div className="text-9xl animate-bounce">💀</div>

          <div className="text-center">
            <h1
              className="text-9xl font-extrabold tracking-widest"
              style={{
                color: '#ef4444',
                textShadow: '0 0 60px rgba(239,68,68,0.6), 4px 4px 0 #450a0a',
              }}
            >
              GAME OVER
            </h1>
            <p className="text-2xl mt-6 font-bold tracking-wide" style={{ color: '#fca5a5' }}>
              Te quedaste sin manos. ¡Más suerte la próxima!
            </p>
          </div>

          <button
            onClick={() => {
              setSession(null);
              setView('MENU');
            }}
            className="px-14 py-5 text-2xl font-black uppercase tracking-widest rounded-full border-4 transition-all duration-150 active:translate-y-1"
            style={{
              background: 'linear-gradient(to bottom, #dc2626, #991b1b)',
              borderColor: '#450a0a',
              color: 'white',
              boxShadow: '0 6px 0 #450a0a, 0 0 32px rgba(220,38,38,0.4)',
            }}
          >
            ↩ Volver al Menú
          </button>
        </div>
      )}
    </div>
  );
}

export default App;
