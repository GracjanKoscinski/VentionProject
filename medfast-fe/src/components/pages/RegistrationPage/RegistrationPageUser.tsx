import React, { useState, useEffect } from 'react';

import { Header } from '@/components/common';
import {
  PersonalDataForm,
  PasswordForm,
  PersonalDetailsForm,
  AddressForm,
  ContactsForm,
  SuccessSignUp,
} from './Forms';
import { default as TermsAndConditions } from './Forms/TermsAndConditions/TermsAndConditions';
import { FullScreenWrapper } from './FullScreenWrapper/styles';

import { format } from 'date-fns';

import { UserSignUp } from '@/api/UserSignUp';

const RegistrationPageUser = () => {
  const [userData, setUserData] = useState<UserData>({
    name: null,
    surname: null,
    birthday: null,
    street: null,
    house: null,
    apartment: null,
    city: null,
    state: null,
    ZIP: null,
    email: null,
    phone: null,
    citizenship: null,
    sex: 'Male',
    password: null,
    repeatedPassword: null,
    isChecked: false,
  });
  const [serverError, setServerError] = useState<'alreadyExist' | 'somethingWrong' | null>(null);
  const [isTermsShown, setIsTermsShown] = useState(false);
  const [isSuccessSignUp, setSuccessSignUp] = useState(false);
  const [stageNumber, setStageNumber] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const emailConfirmationMessage =
    "Thanks for signing up to Medfast. We're happy to have you with us. Please check your email to verify your account";
  const stages = [
    { id: 1, component: PersonalDataForm },
    { id: 2, component: AddressForm },
    { id: 3, component: ContactsForm },
    { id: 4, component: PersonalDetailsForm },
    { id: 5, component: PasswordForm },
  ];
  const CurrentBody = stages[stageNumber].component;

  const handleSignUp = async () => {
    try {
      setIsLoading(true);

      await UserSignUp({
        userData: {
          email: userData.email || '',
          password: userData.password || '',
          name: userData.name || '',
          surname: userData.surname || '',
          birthDate: userData.birthday ? format(userData.birthday, 'yyyy-MM-dd') : '',
          streetAddress: userData.street || '',
          house: userData.house || '',
          apartment: userData.apartment || '',
          city: userData.city || '',
          state: userData.state || '',
          zip: userData.ZIP || '',
          phone:
            userData.phone
              ?.split('')
              .filter((char) => !isNaN(+char))
              .filter((char) => char !== ' ')
              .join('') || '',
          sex: userData.sex?.toLocaleUpperCase() || '',
          citizenship: userData.citizenship || '',
          checkboxTermsAndConditions: userData.isChecked || false,
        },
      });

      setSuccessSignUp(true);
    } catch (error: any) {
      if (error.message === '500') {
        setServerError('alreadyExist');
      } else {
        setServerError('somethingWrong');
      }
    }
    setIsLoading(false);
  };

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();

    stageNumber <= stages.length - 2 ? setStageNumber(stageNumber + 1) : handleSignUp();
  };

  const handleClickBack = (event: React.MouseEvent) => {
    event.preventDefault();
    setStageNumber(stageNumber - 1);
  };

  useEffect(() => {
    setServerError(null);
  }, [userData]);

  return (
    <FullScreenWrapper>
      {!isTermsShown && !isSuccessSignUp && (
        <>
          <Header currentStage={stageNumber} stages={stages} />
          <CurrentBody
            userData={userData}
            setUserData={setUserData}
            onSubmit={handleSubmit}
            handleClickBack={handleClickBack}
            setIsTermsShown={setIsTermsShown}
            serverError={serverError}
            isLoading={isLoading}
          />
        </>
      )}
      {isSuccessSignUp && (
        <SuccessSignUp
          title="Please confirm your email address"
          message={emailConfirmationMessage}
        />
      )}
      {isTermsShown && <TermsAndConditions setIsTermsShown={setIsTermsShown} />}
    </FullScreenWrapper>
  );
};

export default RegistrationPageUser;

export type UserData = {
  name: string | null;
  surname: string | null;
  birthday: Date | null;
  street: string | null;
  house: string | null;
  apartment: string | null;
  city: string | null;
  state: string | null;
  ZIP: string | null;
  email: string | null;
  phone: string | null;
  citizenship: string | null;
  sex: string | null;
  password: string | null;
  repeatedPassword: string | null;
  isChecked: boolean;
};

