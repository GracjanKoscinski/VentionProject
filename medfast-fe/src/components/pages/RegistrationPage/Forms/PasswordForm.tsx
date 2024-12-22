import React, { useEffect, useState } from 'react';

import {
  Form,
  PasswordInput,
  Button,
  GoToLabel,
  Label,
  CheckboxWithLink,
  ServerResponse,
  Loader,
} from '@/components/common';
import { FormsProps, ButtonWrapper } from '.';

import { useUser } from '@/utils/UserContext';

import { DataType } from './FormsProps';

import UserDataValidation from '@/utils/UserDataValidation/UserCredentialValidation';

const ERROR_TEXT = {
  password:
    'Password must be 10-50 characters, with an uppercase letter, an lowercase letters, special symbol, and a digit',
  repeat: 'Password must be the same',
};

type Errors = {
  password: null | string;
  repeat: null | string;
};

const PasswordForm = <T extends DataType>({
  userData,
  serverError,
  isLoading,
  handleClickBack,
  setUserData,
  onSubmit,
  setIsTermsShown,
}: FormsProps<T>) => {
  const userAuth = useUser();
  const isUserRole = userAuth.userData?.role !== 'DOCTOR' && userAuth.userData?.role !== 'ADMIN';
  const [formErrors, setFormErrors] = useState<Errors>({
    password: null,
    repeat: null,
  });

  const isValid =
    userData.password === userData.repeatedPassword &&
    userData.isChecked &&
    userData.password !== '' &&
    userData.repeatedPassword !== '' &&
    userData.password !== null &&
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
      password: userData.password && validation(userData.password, 'password'),
      repeat: userData.repeatedPassword && validation(userData.repeatedPassword, 'repeat'),
    };

    setFormErrors(validationErrors);
  }, [userData]);

  return (
    <>
      <Label
        label="Password"
        fontWeight={700}
        fontSize="l"
        lineHeight="30px"
        color="darkGrey"
        margin="0 0 32px"
      />
      <Form onSubmit={onSubmit}>
        <PasswordInput
          name="New password"
          isDisabled={isLoading}
          error={formErrors.password || ''}
          value={userData.password || ''}
          onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
            setUserData({ ...userData, password: event.target.value });
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
        <CheckboxWithLink
          isDisabled={isLoading}
          label='By clicking the "Register" button, you agree to our'
          labelSize="s"
          linkLabel="Terms and Conditions"
          isChecked={userData.isChecked}
          setIsChecked={() => setUserData({ ...userData, isChecked: !userData.isChecked })}
          onRedirect={() => setIsTermsShown && setIsTermsShown(true)}
        />
        <>{serverError && <ServerResponse serverError={serverError} />}</>
        <ButtonWrapper>
          {isUserRole && (
            <Button
              label="Back"
              disabled={isLoading}
              buttonSize="xxs"
              primary
              onClick={(event: React.MouseEvent) => handleClickBack && handleClickBack(event)}
            />
          )}
          <Button
            label={
              isLoading ? (
                <Loader size="30" color="#fff" />
              ) : isUserRole ? (
                'Complete registration'
              ) : (
                'Set permanent password'
              )
            }
            disabled={!isValid}
            buttonSize={isUserRole ? 's' : 'm'}
            primary={false}
          />
        </ButtonWrapper>
        <>{isUserRole && <GoToLabel label="Back to" coloredLabel="Log In" navigateTo="/logIn" />}</>
      </Form>
    </>
  );
};
export default PasswordForm;

