/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: {
          100: '#21BFAF',
          90: '#36D1C1',
          80: '#63DDD2',
          60: '#98EAE3',
          40: '#C8F5F0',
          20: '#E4FAF7',
          10: '#F2FDFC'
        },
        secondary: {
          100: '#1687F5',
          90: '#2F9BFF',
          80: '#55ACFF',
          60: '#8AC9FF',
          40: '#BCE0FF',
          20: '#DDF0FF',
          10: '#EFF8FF'
        },
        ink: {
          100: '#0F172A',
          80: '#1E293B',
          60: '#475569',
          40: '#94A3B8'
        }
      },
      boxShadow: {
        soft: '0 20px 60px rgba(15, 23, 42, 0.10)',
        card: '0 18px 45px rgba(33, 191, 175, 0.12)',
        glow: '0 24px 70px rgba(22, 135, 245, 0.20)'
      }
    }
  },
  plugins: []
};
