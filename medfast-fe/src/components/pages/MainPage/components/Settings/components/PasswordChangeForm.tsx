import React, { useState, useEffect } from 'react';

import { useUser } from '@/utils/UserContext';

import {
  Form,
  Label,
  Button,
  PasswordInput,
  ServerResponse,
  TabletWithShadow,
} from '@/components/common';
import { CurrentPasswordWrapper, NoPermanentPasswordWrapper } from './styles';

import UserDataValidation from '@/utils/UserDataValidation/UserCredentialValidation';

import { ChangePassword } from '@/api/ChangePassword';

type Errors = {
  password: null | string;
  repeat: null | string;
};

const ERROR_TEXT = {
  password:
    'Password must be 10-50 characters, with an uppercase letter, an lowercase letters, special symbol, and a digit',
  repeat: 'Password must be the same',
};

type Props = {
  setIsPortal: (isPortal: boolean) => void;
  setServerResponse: (serverResponse: 'hasBeenChanged') => void;
  setIsSettings: (isSettings: boolean) => void;
};

const PasswordChangeForm = ({ setIsPortal, setServerResponse, setIsSettings }: Props) => {
  const userAuth = useUser();
  const role = userAuth.userData?.role;
  const [formErrors, setFormErrors] = useState<Errors>({
    password: null,
    repeat: null,
  });
  const [userData, setUserData] = useState<UserData>({
    currentPassword: null,
    newPassword: null,
    repeatedPassword: null,
  });
  const [serverError, setServerError] = useState<'sameAsOld' | 'somethingWrong' | null>(null);

  const isValid =
    formErrors.password === '' &&
    formErrors.repeat === '' &&
    userData.currentPassword !== null &&
    userData.newPassword !== null &&
    userData.repeatedPassword !== null &&
    userData.currentPassword !== '' &&
    userData.newPassword !== '' &&
    userData.repeatedPassword !== '';

  const validation = (data: string, inputName: keyof typeof ERROR_TEXT) => {
    const validationResp = [
      UserDataValidation().allowedChar(data, 'password'),
      UserDataValidation().length(data, 10, 50),
      UserDataValidation().requiredField(data),
    ].filter(Boolean);

    const errors = validationResp.filter((error) => typeof error === 'string');

    if (errors.length === 0) {
      return '';
    } else {
      return ERROR_TEXT[inputName];
    }
  };

  useEffect(() => {
    const validationErrors = {
      password: userData.newPassword && validation(userData.newPassword, 'password'),
      repeat: userData.repeatedPassword && validation(userData.repeatedPassword, 'repeat'),
    };

    setFormErrors(validationErrors);
  }, [userData]);

  const newData = {
    currentPassword: userData.currentPassword || '',
    newPassword: userData.newPassword || '',
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    try {
      await ChangePassword(userAuth.userData?.accessToken || 'null', newData);

      setIsSettings(false);

      setServerResponse('hasBeenChanged');
    } catch (error: any) {
      if (error.message === '409') {
        setServerError('sameAsOld');
      } else {
        setServerError('somethingWrong');
      }
    }
  };

  return role === 'DOCTOR' && !userAuth.userData?.termsAndConditions ? (
    <TabletWithShadow $width="500px" $margin="50px auto 0">
      <NoPermanentPasswordWrapper>
        <Label
          label="Set permanent password"
          fontWeight={600}
          fontSize="l"
          lineHeight="22px"
          color="darkGrey"
          margin="0 0 32px"
        />
        <Label
          label="Please check your email and use the link inside to set permanent password."
          fontWeight={400}
          fontSize="m"
          lineHeight="22px"
          color="darkGrey"
          margin="0"
        />
      </NoPermanentPasswordWrapper>
    </TabletWithShadow>
  ) : (
    <Form onSubmit={(event: React.FormEvent) => handleSubmit(event)}>
      <CurrentPasswordWrapper>
        <PasswordInput
          name="Current password"
          error=""
          value={userData.currentPassword || ''}
          onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
            setUserData({ ...userData, currentPassword: event.target.value });
          }}
        />
        <Label
          label="Forgot your password?"
          fontWeight={500}
          fontSize="xs"
          lineHeight="20px"
          color="purple"
          margin="0 0 16px 0"
          onClick={() => setIsPortal(true)}
        />
      </CurrentPasswordWrapper>
      <PasswordInput
        name="New password"
        error={formErrors.password || ''}
        value={userData.newPassword || ''}
        onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
          setUserData({ ...userData, newPassword: event.target.value });
        }}
      />
      <PasswordInput
        name="Repeat password"
        value={userData.repeatedPassword || ''}
        error={formErrors.repeat || ''}
        onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
          setUserData({ ...userData, repeatedPassword: event.target.value });
        }}
      />
      <>{serverError && <ServerResponse serverError={serverError} />}</>
      <Button label="Change" buttonSize="m" disabled={!isValid} />
    </Form>
  );
};

export default PasswordChangeForm;

export type UserData = {
  currentPassword: string | null;
  newPassword: string | null;
  repeatedPassword: string | null;
};

