import React from 'react';
import { ThemeProvider } from 'styled-components';

const theme = {
  colors: {
    purple: '#8E68F3',
    grey: '#9A9A9A',
    lightGrey: '#E0E0E0',
    darkGrey: '#313131',
    disabled: '#F5F8F9',
    white: '#fff',
    lightBlue: '#E4F3FF',
    red: '#F54A3F',
    darkBlue: '#0065DE',
    blue: '#F9FDFF',
    skyBlue: '#87BEFF',
    formShadow: '#130A2E08',
    shadow: '#89898980',
    lightShadow: '#F1F1F1',
    yellow: '#FBBC41',
    error: { color: '#F54A3F', backgroundColor: '#FFF8F4' },
    warning: { color: '#FBBC41', backgroundColor: '#FFF7E8' },
    success: { color: '#27AE60', backgroundColor: '#F4FEF9' },
  },
  fontSizes: {
    xxs: '12px',
    xs: '14px',
    s: '16px',
    m: '18px',
    l: '24px',
  },
  buttonWidth: {
    l: '634px',
    m: '372px',
    s: '248px',
    xs: '154px',
    xxs: '112px',
  },
  button: {
    default: {
      background:
        '-webkit-linear-gradient(0deg, #7A77FF, #7A77FF), linear-gradient(180deg, rgba(29, 26, 152, 0.2) 0%, rgba(0, 0, 0, 0) 71.15%);',
      border: 'none',
      color: '#fff',
    },
    primary: { background: '#fff', border: '2px solid #8E68F3', color: '#8E68F3' },
    active: {
      background:
        '-webkit-linear-gradient(0deg, #5f5cdb, #5f5cdb),linear-gradient(180deg, rgba(11, 11, 40, 0.2) 19.64%, rgba(255, 255, 255, 0) 91.07%)',
      color: '#fff',
    },
    disabled: {
      background:
        ' -webkit-linear-gradient(0deg, #D5D3D3, #D5D3D3), linear-gradient(180deg, rgba(128, 128, 128, 0.2) 0%, rgba(204, 204, 204, 0.2) 71.15%);',
      border: 'none',
      color: '#fff',
    },
  },
  buttonShape: {
    oval: '40px',
    square: '8px',
  },
};

type Props = {
  children: JSX.Element | JSX.Element[];
};

const Theme = ({ children }: Props) => {
  return <ThemeProvider theme={theme}>{children}</ThemeProvider>;
};

export default Theme;
