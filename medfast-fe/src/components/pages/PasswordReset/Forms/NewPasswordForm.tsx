import React, { useState, useEffect } from 'react';

import {
  Label,
  PasswordInput,
  Button,
  Header,
  Form,
  ScreenWrapperCentred,
  ServerResponse,
  GoToLabel,
  RoundImageBackground,
  Loader,
} from '@/components/common';
import { Wrapper } from './styles';
import Icon from '@/components/Icons';

import UserDataValidation from '@/utils/UserDataValidation/UserCredentialValidation';

import { FormsProps } from './FormsProps';

import { PasswordResetNewPassword } from '@/api/PasswordResetNewPassword';

const ERROR_TEXT = {
  password:
    'Password must be 10-50 characters, with an uppercase letter, an lowercase letters, special symbol, and a digit',
  repeat: 'Password must be the same',
};

type Errors = {
  password: null | string;
  repeat: null | string;
};

const NewPasswordForm = ({ userData, setUserData, onSubmit }: FormsProps) => {
  const [serverError, setServerError] = useState<'sameAsOld' | 'somethingWrong' | null>(null);
  const [formErrors, setFormErrors] = useState<Errors>({
    password: null,
    repeat: null,
  });
  const [isLoading, setIsLoading] = useState(false);

  const isValid =
    userData.newPassword === userData.repeatedPassword &&
    userData.newPassword !== '' &&
    userData.repeatedPassword !== '' &&
    userData.newPassword !== null &&
    userData.repeatedPassword !== null &&
    formErrors.password === '' &&
    formErrors.repeat === '';

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
    otp: userData.token || '',
    newPassword: userData.newPassword || '',
    email: userData.email || '',
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    try {
      setIsLoading(true);

      await PasswordResetNewPassword(newData);

      onSubmit();
    } catch (error: any) {
      if (error.message === '409') {
        setServerError('sameAsOld');
      } else {
        setServerError('somethingWrong');
      }
    }
    setIsLoading(false);
  };

  useEffect(() => {
    setServerError(null);
  }, [userData]);

  return (
    <Wrapper>
      <Header />
      <ScreenWrapperCentred>
        <Form isBordered={false} onSubmit={(event: React.FormEvent) => handleSubmit(event)}>
          <RoundImageBackground $backgroundColor="lightBlue" $borderColor="white">
            <Icon type="keyIcon" />
          </RoundImageBackground>
          <Label
            label="Set new password?"
            fontWeight={700}
            fontSize="l"
            lineHeight="30px"
            color="darkGrey"
            margin="12px 0 12px"
          />
          <Label
            label="Please enter your new password"
            fontWeight={400}
            fontSize="s"
            lineHeight="22px"
            color="darkGrey"
            margin="0 0 24px"
          />
          <PasswordInput
            name="New password"
            isDisabled={isLoading}
            error={formErrors.password || ''}
            value={userData.newPassword || ''}
            onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
              setUserData({ ...userData, newPassword: event.target.value });
            }}
          />
          <PasswordInput
            name="Repeat password"
            isDisabled={isLoading}
            value={userData.repeatedPassword || ''}
            error={formErrors.repeat || ''}
            onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
              setUserData({ ...userData, repeatedPassword: event.target.value });
            }}
          />
          <>{serverError && <ServerResponse serverError={serverError} />}</>
          <Button label="Sent and login" buttonSize="m" disabled={!isValid} />
          <GoToLabel location="center" label="Back to" coloredLabel="Log In" navigateTo="/logIn" />
        </Form>
      </ScreenWrapperCentred>
    </Wrapper>
  );
};

export default NewPasswordForm;

