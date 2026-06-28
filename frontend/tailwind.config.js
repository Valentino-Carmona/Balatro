/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        paper: '#F5E6CE',
        ink: '#1A1A1A',
        vintageRed: '#D93829',
      },
      fontFamily: {
        'rubberhose': ['"Courier New"', 'Courier', 'monospace'], // Fallback hasta que se cargue una font web
      },
      animation: {
        'bounce-slight': 'bounce-slight 1s infinite',
        'wiggle': 'wiggle 1s ease-in-out infinite',
        'pop-in': 'pop-in 0.25s cubic-bezier(0.34, 1.56, 0.64, 1) both',
        'score-flash': 'score-flash 0.35s ease-out both',
        'mult-hit': 'mult-hit 0.4s ease-out both',
        'slide-up': 'slide-up 0.3s ease-out both',
        'glow-chip': 'glow-chip 0.4s ease-out both',
      },
      keyframes: {
        'bounce-slight': {
          '0%, 100%': { transform: 'translateY(-2%)' },
          '50%': { transform: 'translateY(0)' },
        },
        'wiggle': {
          '0%, 100%': { transform: 'rotate(-3deg)' },
          '50%': { transform: 'rotate(3deg)' },
        },
        'pop-in': {
          '0%': { transform: 'scale(0.7)', opacity: '0' },
          '100%': { transform: 'scale(1)', opacity: '1' },
        },
        'score-flash': {
          '0%': { transform: 'scale(1.4)', color: '#facc15' },
          '100%': { transform: 'scale(1)', color: 'inherit' },
        },
        'mult-hit': {
          '0%': { transform: 'scale(1.6)', color: '#ff4444', textShadow: '0 0 20px #ff0000' },
          '100%': { transform: 'scale(1)', color: 'inherit', textShadow: 'none' },
        },
        'slide-up': {
          '0%': { transform: 'translateY(12px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
        'glow-chip': {
          '0%': { transform: 'scale(1.3)', color: '#60a5fa', textShadow: '0 0 16px #3b82f6' },
          '100%': { transform: 'scale(1)', color: 'inherit', textShadow: 'none' },
        },
      },
      boxShadow: {
        'vintage': '4px 4px 0px 0px rgba(26,26,26,1)',
      }
    },
  },
  plugins: [],
}
