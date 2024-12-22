import React, { useEffect, useState } from 'react';

import { Form, Input, Button, GoToLabel, Label, Loader, ServerResponse } from '@/components/common';
import { FormsProps, ButtonWrapper } from '.';
import UserDataValidation from '@/utils/UserDataValidation/UserCredentialValidation';

import { useUser } from '@/utils/UserContext';

import { DataType } from './FormsProps';

const ERROR_TEXT = {
  email: 'Please enter a valid email',
  phone: 'Please enter a valid phone number',
};

type Errors = {
  email: null | string;
  phone: null | string;
};

const ContactsForm = <T extends DataType>({
  userData,
  isLoading,
  handleClickBack,
  serverError,
  setUserData,
  onSubmit,
}: FormsProps<T>) => {
  const userAuth = useUser();
  const isUserRole = userAuth.userData?.role !== 'DOCTOR' && userAuth.userData?.role !== 'ADMIN';
  const [formErrors, setFormErrors] = useState<Errors>({
    email: null,
    phone: null,
  });

  const isValid =
    formErrors.email === '' &&
    formErrors.phone === '' &&
    userData.email !== '' &&
    userData.phone !== '';

  const validation = (data: string, inputName: keyof typeof formErrors) => {
    const validationResp = [
      UserDataValidation().allowedChar(data, inputName),
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
      email: userData.email && validation(userData.email, 'email'),
      phone: userData.phone && validation(userData.phone, 'phone'),
    };

    setFormErrors(validationErrors);
  }, [userData]);

  return (
    <>
      <Label
        label="Contacts"
        fontWeight={700}
        fontSize="l"
        lineHeight="30px"
        color="darkGrey"
        margin="0 0 32px"
      />
      <Form onSubmit={onSubmit}>
        <Input
          name="email"
          type="email"
          placeholder="Name@gmail.com"
          isInvalid={formErrors.email || ''}
          value={userData.email || ''}
          label="Email"
          onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
            setUserData({ ...userData, email: event.target.value });
          }}
        />
        <Input
          name="phone"
          type="tel"
          placeholder="+1 (xxx) xxx xxxx"
          value={userData.phone || ''}
          isInvalid={formErrors.phone || ''}
          label="Phone number"
          onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
            setUserData({ ...userData, phone: event.target.value });
          }}
        />
        <>{serverError && <ServerResponse serverError={serverError} />}</>
        <ButtonWrapper>
          <Button
            label="Back"
            buttonSize="xxs"
            primary
            onClick={(event: React.MouseEvent) => handleClickBack && handleClickBack(event)}
          />
          <Button
            label={
              isLoading ? (
                <Loader size="30" color="#fff" />
              ) : isUserRole ? (
                'Next'
              ) : (
                'Complete registration'
              )
            }
            disabled={!isValid}
            buttonSize="s"
            primary={false}
          />
        </ButtonWrapper>
        <>{isUserRole && <GoToLabel label="Back to" coloredLabel="Log In" navigateTo="/logIn" />}</>
      </Form>
    </>
  );
};
export default ContactsForm;

