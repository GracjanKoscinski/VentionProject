import React, { createContext, useContext, useState } from 'react';

import { UserLogIn } from '@/api/UserLogIn';
import type { UserDataType } from '@/api/UserLogIn';
import { UserLogOut } from '@/api/UserLogOut';

type Props = {
  children: JSX.Element | JSX.Element[];
};

const User = createContext<AuthContextProviderProps>({
  userData: null,
  logIn: () => {},
  logOut: () => {},
});

const AuthContextProvider = ({ children }: Props) => {
  const [userData, setUserData] = useState<null | UserData>({
    accessToken: JSON.parse(localStorage.getItem('accessToken') || 'null'),
    refreshToken: JSON.parse(localStorage.getItem('refreshToken') || 'null'),
    role: JSON.parse(localStorage.getItem('role') || 'null'),
    termsAndConditions: JSON.parse(localStorage.getItem('terms') || 'null'),
  });

  const logIn = async (userData: UserDataType) => {
    try {
      const response = await UserLogIn(userData);

      setUserData({
        accessToken: response.data.accessToken,
        refreshToken: response.data.refreshToken,
        role: response.data.role,
        //TODO: replace value with API data
        termsAndConditions: false,
      });

      localStorage.setItem('accessToken', JSON.stringify(response.data.accessToken));
      localStorage.setItem('refreshToken', JSON.stringify(response.data.refreshToken));
      localStorage.setItem('role', JSON.stringify(response.data.role));
      //TODO: replace value with API data
      localStorage.setItem('terms', JSON.stringify(false));
    } catch (error: any) {
      throw new Error(error);
    }
  };

  const logOut = async (token: string) => {
    try {
      await UserLogOut(token);

      setUserData(null);

      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('role');
      localStorage.removeItem('terms');
    } catch (error: any) {
      throw new Error(error);
    }
  };

  return <User.Provider value={{ userData, logIn, logOut }}>{children}</User.Provider>;
};

export const useUser = () => useContext<AuthContextProviderProps>(User);

export default AuthContextProvider;

type UserData = {
  accessToken: string;
  refreshToken: string;
  role: string | null;
  termsAndConditions: boolean;
};

type AuthContextProviderProps = {
  userData: null | UserData;
  logIn: (userData: UserDataType) => void;
  logOut: (token: string) => void;
};

