import React, { useEffect, useState } from 'react';

import { format } from 'date-fns';

import { Form, Input, Button, GoToLabel, Label, CalendarElement } from '@/components/common';
import { FormsProps } from '.';
import UserDataValidation from '@/utils/UserDataValidation/UserCredentialValidation';

import { useUser } from '@/utils/UserContext';

import { DataType } from './FormsProps';

const ERROR_TEXT = {
  required: 'Please enter your data',
  length: 'The length must be from 2 to 50 characters',
  allowedChars: 'Only Latin letters allowed, no digits or special symbols',
  dateFormat: 'Invalid date',
  ageLimit: 'Age limit from 18 to 110 years',
};

type Errors = {
  name: null | string;
  surname: null | string;
  birthday: null | string;
};

const PersonalDataForm = <T extends DataType>({
  userData,
  setUserData,
  onSubmit,
}: FormsProps<T>) => {
  const userAuth = useUser();
  const isUserRole = userAuth.userData?.role === 'user';
  const [formErrors, setFormErrors] = useState<Errors>({
    name: null,
    surname: null,
    birthday: null,
  });

  const isValid =
    formErrors.name === '' &&
    formErrors.surname === '' &&
    formErrors.birthday === '' &&
    userData.name !== '' &&
    userData.surname !== '';

  const validation = (data: string) => {
    const validationResp = [
      UserDataValidation().allowedChar(data, 'noSpecOrNum'),
      UserDataValidation().length(data, 2, 50),
      UserDataValidation().requiredField(data),
    ].filter(Boolean);

    const errors = validationResp.filter((error) => typeof error === 'string');
    const errorToShow = (errors as Array<keyof typeof ERROR_TEXT>)[0];

    if (errors.length === 3) {
      return ERROR_TEXT.required;
    } else if (errors.length === 0) {
      return '';
    } else {
      return ERROR_TEXT[errorToShow];
    }
  };

  const calendarValidation = (data: string) => {
    const validationResp = [
      UserDataValidation().dateFormat(data),
      UserDataValidation().ageLimit(data),
      UserDataValidation().requiredField(data),
    ].filter(Boolean);

    const errors = validationResp.filter((error) => typeof error === 'string');
    const errorToShow = (errors as Array<keyof typeof ERROR_TEXT>)[0];

    if (errors.length === 0) {
      return '';
    } else if (errors.length === 3) {
      return ERROR_TEXT.required;
    } else {
      return ERROR_TEXT[errorToShow];
    }
  };

  useEffect(() => {
    const validationErrors = {
      name: userData.name && validation(userData.name),
      surname: userData.surname && validation(userData.surname),
      birthday: userData.birthday && calendarValidation(format(userData.birthday, 'MM/dd/yyyy')),
    };

    setFormErrors(validationErrors);
  }, [userData]);

  return (
    <>
      <Label
        label="Personal Data"
        fontWeight={700}
        fontSize="l"
        lineHeight="30px"
        color="darkGrey"
        margin="0 0 32px"
      />
      <Form onSubmit={onSubmit}>
        <Input
          name="userName"
          type="text"
          placeholder={isUserRole ? 'Your name' : 'Name'}
          isInvalid={formErrors.name || ''}
          value={userData.name || ''}
          label="Name"
          onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
            setUserData({ ...userData, name: event.target.value });
          }}
        />
        <Input
          name="userSurname"
          type="text"
          placeholder={isUserRole ? 'Your surname' : 'Surname'}
          value={userData.surname || ''}
          isInvalid={formErrors.surname || ''}
          label="Surname"
          onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
            setUserData({ ...userData, surname: event.target.value });
          }}
        />
        <CalendarElement
          error={formErrors.birthday}
          userData={userData}
          setUserData={setUserData}
        />
        <Button label="Next" disabled={!isValid} buttonSize="m" primary={false} />
        <>{isUserRole && <GoToLabel label="Back to" coloredLabel="Log In" navigateTo="/logIn" />}</>
      </Form>
    </>
  );
};
export default PersonalDataForm;

