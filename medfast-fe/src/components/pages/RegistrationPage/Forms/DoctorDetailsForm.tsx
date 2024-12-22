import React, { useEffect, useState } from 'react';

import { Form, Input, Button, Label, Select } from '@/components/common';
import { FormsProps, ButtonWrapper } from '.';
import UserDataValidation from '@/utils/UserDataValidation/UserCredentialValidation';

import { DoctorData } from '../RegistrationPageDoctor';

const ERROR_TEXT = {
  required: 'Please enter your data',
  length: 'The length must be 7 digits',
  allowedChars: 'Only digits allowed',
};

type Errors = {
  licenseNumber: null | string;
};

const DoctorDetails = <T extends DoctorData>({
  userData,
  setUserData,
  handleClickBack,
  onSubmit,
}: FormsProps<T>) => {
  const [formErrors, setFormErrors] = useState<Errors>({
    licenseNumber: null,
  });

  const isValid =
    formErrors.licenseNumber === '' &&
    userData.specialization !== '' &&
    userData.licenseNumber !== '';

  const validation = (data: string) => {
    const validationResp = [
      UserDataValidation().allowedChar(data, 'onlyNum'),
      UserDataValidation().length(data, 7, 7),
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

  useEffect(() => {
    const validationErrors = {
      licenseNumber: userData.licenseNumber && validation(userData.licenseNumber),
    };

    setFormErrors(validationErrors);
  }, [userData]);

  return (
    <>
      <Label
        label="Doctor Details"
        fontWeight={700}
        fontSize="l"
        lineHeight="30px"
        color="darkGrey"
        margin="0 0 32px"
      />
      <Form onSubmit={onSubmit}>
        <Label
          label="Specialization"
          fontWeight={600}
          fontSize="s"
          lineHeight="20px"
          color="darkGrey"
          margin="0 0 10px"
        />
        <Select
          name="specialization"
          options={[]}
          placeholder={userData.specialization || 'Enter specialization'}
          handleChange={(option: string) => {
            setUserData({ ...userData, specialization: option });
          }}
        />
        <Input
          name="licenseNumber"
          type="text"
          placeholder="Enter license number"
          value={userData.licenseNumber || ''}
          isInvalid={formErrors.licenseNumber || ''}
          label="Medical license number"
          onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
            setUserData({ ...userData, licenseNumber: event.target.value });
          }}
        />
        <ButtonWrapper>
          <Button
            label="Back"
            buttonSize="xxs"
            onClick={(event: React.MouseEvent) => handleClickBack && handleClickBack(event)}
          />
          <Button label="Next" disabled={!isValid} buttonSize="s" primary={false} />
        </ButtonWrapper>
      </Form>
    </>
  );
};
export default DoctorDetails;

