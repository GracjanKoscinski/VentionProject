import React from 'react';

import { createBrowserRouter } from 'react-router-dom';

import {
  RegistrationPageUser,
  LogInPage,
  EmailConfirmation,
  PasswordResetPage,
  MainPage,
  RegistrationPageDoctor,
  AdminConsole
} from './components';

export const router = createBrowserRouter([
  {
    path: '/registration',
    element: <RegistrationPageUser />,
  },
  {
    path: '/set-permanent-password',
    element: <RegistrationPageDoctor stage={3} />,
  },
  {
    path: '/logIn',
    element: <LogInPage />,
  },
  {
    path: '/doctor-logIn',
    element: <LogInPage />,
  },
  {
    path: '/verify',
    element: <EmailConfirmation />,
  },
  {
    path: '/password-reset',
    element: <PasswordResetPage />,
  },
  {
    path: '/main',
    element: <MainPage />,
  },
  {
    path: '/admin-console',
    element: <AdminConsole />,
  },
  {
    path: '/admin-console/doctor-registration',
    element: <RegistrationPageDoctor />,
  },
]);
