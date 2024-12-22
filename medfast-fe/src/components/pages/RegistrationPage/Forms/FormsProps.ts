import { UserData } from '../RegistrationPageUser';
import { DoctorData } from '../RegistrationPageDoctor';

export type DataType = UserData | DoctorData;

export type FormsProps<T> = {
  userData: T;
  serverError?: 'alreadyExist' | 'somethingWrong' | 'specializationNotFound' | null;
  isLoading: boolean;
  handleClickBack?: (event: React.MouseEvent) => void;
  setUserData: (userData: T) => void;
  onSubmit: (event: React.FormEvent) => void;
  setIsTermsShown?: (isTermsShown: boolean) => void;
};
