import React, { useState, useEffect } from 'react';

import { AddressFormWrapper } from './styles';

import { Form, Input, Button, GoToLabel, Label } from '@/components/common';
import { FormsProps } from '.';
import UserDataValidation, { REG_EXP } from '@/utils/UserDataValidation/UserCredentialValidation';

import { UserData } from '../RegistrationPageUser';

const ERROR_TEXT = {
  street: 'Please enter a valid street name',
  house: 'Please enter a valid house number',
  apartment: 'Please enter a valid apartment number',
  city: 'Please enter a valid city name',
  state: 'Please enter a valid state name',
  ZIP: 'Please enter a valid ZIP',
};

type Errors = {
  street: null | string;
  house: null | string;
  apartment: null | string;
  city: null | string;
  state: null | string;
  ZIP: null | string;
};

const AddressForm = <T extends UserData>({
  userData,
  handleClickBack,
  setUserData,
  onSubmit,
}: FormsProps<T>) => {
  const [formErrors, setFormErrors] = useState<Errors>({
    street: null,
    house: null,
    apartment: null,
    city: null,
    state: null,
    ZIP: null,
  });

  const isValid =
    formErrors.street === '' &&
    formErrors.house === '' &&
    formErrors.apartment === '' &&
    formErrors.city === '' &&
    formErrors.state === '' &&
    formErrors.ZIP === '' &&
    userData.street !== '' &&
    userData.house !== '' &&
    userData.apartment !== '' &&
    userData.city !== '' &&
    userData.state !== '' &&
    userData.ZIP !== '';

  const validation = (
    data: string,
    inputName: keyof typeof formErrors,
    start: number,
    end: number,
    validChars: keyof typeof REG_EXP,
  ) => {
    const validationResp = [
      UserDataValidation().requiredField(data),
      UserDataValidation().allowedChar(data, validChars),
      UserDataValidation().length(data, start, end),
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
      street: userData.street && validation(userData.street, 'street', 2, 50, 'latin'),
      house: userData.house && validation(userData.house, 'house', 1, 20, 'alphanumeric'),
      apartment:
        userData.apartment && validation(userData.apartment, 'apartment', 1, 20, 'alphanumeric'),
      city: userData.city && validation(userData.city, 'city', 1, 20, 'noNum'),
      state: userData.state && validation(userData.state, 'state', 2, 50, 'noNum'),
      ZIP: userData.ZIP && validation(userData.ZIP, 'ZIP', 5, 5, 'onlyNum'),
    };

    setFormErrors(validationErrors);
  }, [userData]);

  return (
    <>
      <Label
        label="Address"
        fontWeight={700}
        fontSize="l"
        lineHeight="30px"
        color="darkGrey"
        margin="0 0 32px"
      />
      <Form onSubmit={onSubmit}>
        <AddressFormWrapper>
          <Input
            name="street"
            type="text"
            placeholder="Your street address"
            isInvalid={formErrors.street || ''}
            value={userData.street || ''}
            label="Street Address"
            onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
              setUserData({ ...userData, street: event.target.value });
            }}
          />
          <Input
            name="city"
            type="text"
            placeholder="Your city"
            isInvalid={formErrors.city || ''}
            value={userData.city || ''}
            label="City"
            onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
              setUserData({ ...userData, city: event.target.value });
            }}
          />
          <Input
            name="house"
            type="text"
            placeholder="Your house"
            value={userData.house || ''}
            isInvalid={formErrors.house || ''}
            label="House"
            onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
              setUserData({ ...userData, house: event.target.value });
            }}
          />
          <Input
            name="state"
            type="text"
            placeholder="Your state"
            value={userData.state || ''}
            isInvalid={formErrors.state || ''}
            label="State"
            onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
              setUserData({ ...userData, state: event.target.value });
            }}
          />
          <Input
            name="apartment"
            type="text"
            placeholder="Your apartment"
            value={userData.apartment || ''}
            isInvalid={formErrors.apartment || ''}
            label="Apartment"
            onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
              setUserData({ ...userData, apartment: event.target.value });
            }}
          />
          <Input
            name="ZIP"
            type="text"
            placeholder="******"
            value={userData.ZIP || ''}
            isInvalid={formErrors.ZIP || ''}
            label="ZIP"
            onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
              setUserData({ ...userData, ZIP: event.target.value });
            }}
          />
          <Button
            label="Back"
            buttonSize="m"
            primary
            onClick={(event: React.MouseEvent) => handleClickBack && handleClickBack(event)}
          />
          <Button label="Next" disabled={!isValid} buttonSize="m" primary={false} />
        </AddressFormWrapper>
        <GoToLabel label="Back to" coloredLabel="Log In" navigateTo="/logIn" />
      </Form>
    </>
  );
};
export default AddressForm;

