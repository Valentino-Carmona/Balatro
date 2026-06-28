import React from 'react';

export const PokerChipsDecoration: React.FC = () => {
  return (
    <div className="absolute inset-0 overflow-hidden pointer-events-none opacity-20 z-0">
      {/* Red Chip (Value 25) */}
      <div
        className="absolute w-24 h-24 rounded-full border-8 border-dashed border-red-400 bg-red-800 flex items-center justify-center animate-drift-slow"
        style={{ top: '15%', left: '8%', transform: 'rotate(12deg)' }}
      >
        <div className="w-14 h-14 rounded-full border-4 border-double border-white/60 bg-transparent flex items-center justify-center">
          <span className="text-white/80 text-xs font-black">25</span>
        </div>
      </div>

      {/* Blue Chip (Value 50) */}
      <div
        className="absolute w-20 h-20 rounded-full border-8 border-dashed border-blue-400 bg-blue-800 flex items-center justify-center animate-drift-slower"
        style={{ top: '75%', right: '8%', transform: 'rotate(-25deg)' }}
      >
        <div className="w-10 h-10 rounded-full border-4 border-double border-white/60 bg-transparent flex items-center justify-center">
          <span className="text-white/80 text-[10px] font-black">50</span>
        </div>
      </div>

      {/* Gold Chip (Value 100) */}
      <div
        className="absolute w-28 h-28 rounded-full border-8 border-dashed border-yellow-500 bg-yellow-800 flex items-center justify-center animate-drift-slow"
        style={{ top: '65%', left: '12%', transform: 'rotate(45deg)' }}
      >
        <div className="w-16 h-16 rounded-full border-4 border-double border-white/60 bg-transparent flex items-center justify-center">
          <span className="text-white/80 text-sm font-black">100</span>
        </div>
      </div>

      {/* Black Chip (Value 5) */}
      <div
        className="absolute w-22 h-22 rounded-full border-8 border-dashed border-gray-500 bg-gray-900 flex items-center justify-center animate-drift-slower"
        style={{ top: '18%', right: '12%', transform: 'rotate(-10deg)' }}
      >
        <div className="w-12 h-12 rounded-full border-4 border-double border-white/60 bg-transparent flex items-center justify-center">
          <span className="text-white/80 text-xs font-black">5</span>
        </div>
      </div>
    </div>
  );
};
