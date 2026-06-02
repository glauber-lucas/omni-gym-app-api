import { black, transparent, white } from 'tailwindcss/colors';

export const colors = {
  primary: {
    100: '#6DDDD0',
    80: '#8AE4D9',
    60: '#A7EBE3',
    40: '#C5F1EC',
    20: '#E2F8F6',
  },
  secondary: {
    100: '#42A5FF',
    80: '#68B7FF',
    60: '#8EC9FF',
    40: '#B3DBFF',
    20: '#D9EDFF',
    10: '#EBF5FD',
  },
  neutral: {
    100: '#171616',
    80: '#454545',
    60: '#747373',
    40: '#A2A2A2',
    20: '#D1D0D0',
    10: '#E8E8E8',
  },
  other: {
    green: {
      100: '#39996C',
      80: '#57BC64',
    },
    red: {
      100: '#DE3737',
      80: '#FCEBEB',
    },
    blue: {
      100: '#387AF5',
      80: '#D7E4FD',
    },
    yellow: {
      100: '#D5BE0E',
      80: '#E7D956',
    },
    aqua: {
      100: '#1D8488',
      80: '#ECF9FC',
    },
  },
  alert: {
    success: {
      100: '#2DAC3E',
      80: '#ABDEB1',
    },
    error: {
      100: '#DE3737',
      80: '#FFD2D2',
    },
    warning: {
      100: '#E1CF36',
      80: '#FFFACB',
    },
  },

  transparent,
  black,
  white,
} as const;
