import js from '@eslint/js';
import react from 'eslint-plugin-react';
import globals from 'globals';
import reactRecommended from 'eslint-plugin-react/configs/recommended.js';
import tseslint from 'typescript-eslint';

export default [
  js.configs.recommended,
  ...tseslint.configs,
  ...tseslint.configs,

  {
    files: ['**/*.{js,jsx,mjs,cjs,ts,tsx}'],
    ignores: ['/*.config.*', '**/*.test.*'],
    ...reactRecommended,
    plugins: {
      react,
    },
    languageOptions: {
      ...reactRecommended.languageOptions,
      parserOptions: {
        ecmaFeatures: {
          jsx: true,
        },
      },
      globals: {
        ...globals.browser,
      },
    },
    rules: {
      'no-undef': 'warn',
      'no-var': 'error',
      '@typescript-eslint/no-unused-vars': 'warn',
      'react/jsx-uses-react': 'error',
      'react/jsx-uses-vars': 'error',
      '@typescript-eslint/no-non-null-assertion': 'off',
    },
  },
];
