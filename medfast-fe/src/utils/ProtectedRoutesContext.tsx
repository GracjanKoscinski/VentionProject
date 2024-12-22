import React, { createContext, useContext, useEffect } from 'react';

import { useUser } from './UserContext';

type Props = {
  children: JSX.Element | JSX.Element[];
};

const ProtectedRoutesContext = createContext<ProtectedRoutesContextProviderProps>({
  isAuthenticated: false,
  isAllowedRouteNoAuth: false,
});

const ProtectedRoutesProvider = ({ children }: Props) => {
  const userAuth = useUser();

  const isAdmin = userAuth.userData?.role === 'ADMIN';
  const isDoctor = userAuth.userData?.role === 'DOCTOR';

  const accessToken = JSON.parse(localStorage.getItem('accessToken') || 'null');
  const isAuthenticated = !!accessToken;

  const adminRoute = 'admin';
  const doctorRoute = 'set-permanent-password';
  const allowedRoutesNoAuth = [
    'logIn',
    'doctor-logIn',
    'registration',
    'password-reset',
    'verify',
    'set-permanent-password',
  ];
  const userLocation = document.location.pathname.replace(/^\/+|\/+$/g, '');
  const isAllowedRouteNoAuth = !!allowedRoutesNoAuth.find((route) => route === userLocation);

  useEffect(() => {
    if (
      (isAuthenticated && isAllowedRouteNoAuth) ||
      (!isAdmin && userLocation.startsWith(adminRoute) && isAuthenticated) ||
      (!isDoctor && userLocation === doctorRoute && isAuthenticated)
    ) {
      window.location.replace(`${process.env.LOCAL_HOST}/main`);
    } else if (!isAuthenticated && !isAllowedRouteNoAuth) {
      window.location.replace(`${process.env.LOCAL_HOST}/logIn`);
    }
    [];
  }, []);

  return (
    <ProtectedRoutesContext.Provider value={{ isAuthenticated, isAllowedRouteNoAuth }}>
      {((!isAllowedRouteNoAuth && isAuthenticated && userLocation !== adminRoute) ||
        (isAllowedRouteNoAuth && !isAuthenticated) ||
        (!isAuthenticated && userLocation === doctorRoute)) &&
        children}
    </ProtectedRoutesContext.Provider>
  );
};

export const useProtectedRoutes = () =>
  useContext<ProtectedRoutesContextProviderProps>(ProtectedRoutesContext);

export default ProtectedRoutesProvider;

type ProtectedRoutesContextProviderProps = {
  isAuthenticated: boolean;
  isAllowedRouteNoAuth: boolean;
};