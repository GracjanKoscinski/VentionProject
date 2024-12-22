import { UserData } from '../PasswordResetPage';

export type FormsProps = {
  userData: UserData;
  setUserData: (userData: UserData) => void;
  onSubmit: () => void;
};
