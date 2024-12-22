import React from 'react';
import { createRoot } from 'react-dom/client';

import { RouterProvider } from 'react-router-dom';
import { router } from './router';

import AuthContextProvider from './utils/UserContext';
import ProtectedRoutesProvider from './utils/ProtectedRoutesContext';

import { Theme } from './components';

import './style.css';

const container = document.getElementById('root');
const root = createRoot(container!);

root.render(
  <AuthContextProvider>
    <ProtectedRoutesProvider>
      <Theme>
        <RouterProvider router={router} />
      </Theme>
    </ProtectedRoutesProvider>
  </AuthContextProvider>,
);
