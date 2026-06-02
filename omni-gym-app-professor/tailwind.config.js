import { colors } from './src/global/colors';
import { fontFamily } from './src/global/fontFamily';

const colorUtilities = ['bg', 'border', 'text'];

const flattenColorTokens = (palette, prefix = []) => {
  return Object.entries(palette).flatMap(([key, value]) => {
    const path = [...prefix, key];

    if (typeof value === 'string') {
      return path.join('-');
    }

    if (value && typeof value === 'object') {
      return flattenColorTokens(value, path);
    }

    return [];
  });
};

const colorSafelist = flattenColorTokens(colors).flatMap(color =>
  colorUtilities.map(utility => `${utility}-${color}`),
);

const fontSafelist = Object.keys(fontFamily).map(font => `font-${font}`);

/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{js,jsx,ts,tsx}'],
  safelist: [...colorSafelist, ...fontSafelist],
  presets: [require('nativewind/preset')],
  theme: {
    extend: {
      colors,
      fontFamily,
    },
  },
  plugins: [],
};
