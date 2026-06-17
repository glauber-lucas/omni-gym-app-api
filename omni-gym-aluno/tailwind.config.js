/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: {
          100: '#1687F5',
          90: '#2F9BFF',
          80: '#55ACFF',
          60: '#8AC9FF',
          40: '#BCE0FF',
          20: '#DDF0FF',
          10: '#EFF8FF'
        },
        secondary: {
          100: '#24C7B8',
          90: '#43D5C8',
          80: '#69DFD4',
          60: '#9BECE5',
          40: '#C9F6F1',
          20: '#E5FBF8',
          10: '#F3FDFC'
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
        card: '0 18px 45px rgba(22, 135, 245, 0.10)',
        glow: '0 24px 70px rgba(36, 199, 184, 0.22)'
      }
    }
  },
  plugins: []
};
