/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: {
          100: '#6DDDD0',
          80: '#8AE4D9',
          60: '#A7EBE3',
          40: '#C5F1EC',
          20: '#E2F8F6'
        },
        secondary: {
          100: '#42A5FF',
          80: '#68B7FF',
          60: '#8EC9FF',
          40: '#B3DBFF',
          20: '#D9EDFF',
          10: '#EBF5FD'
        }
      },
      boxShadow: {
        soft: '0 18px 60px rgba(27, 42, 73, 0.12)'
      }
    }
  },
  plugins: []
};
