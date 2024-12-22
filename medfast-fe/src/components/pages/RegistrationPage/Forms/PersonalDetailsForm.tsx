import React from 'react';
import { countries } from 'countries-list';

import { Form, Select, Button, GoToLabel, Label, RadioButton } from '@/components/common';
import { FormsProps, ButtonWrapper } from '.';

import { UserData } from '../RegistrationPageUser';

const PersonalDetailsForm = <T extends UserData>({
  userData,
  handleClickBack,
  setUserData,
  onSubmit,
}: FormsProps<T>) => {
  const frequentlyUsedCountries = ['Canada', 'Mexico', 'United States'];
  const allCountries = Object.values(countries).map((country) => country.name);
  const otherCountries = allCountries.filter(
    (country) => !frequentlyUsedCountries.includes(country),
  );
  const options = ['Male', 'Female', 'Choose not to disclose'];
  const isValid = userData.citizenship !== null && userData.sex !== null;

  return (
    <>
      <Label
        label="Personal Details"
        fontWeight={700}
        fontSize="l"
        lineHeight="30px"
        color="darkGrey"
        margin="0 0 32px"
      />
      <Form onSubmit={onSubmit}>
        <RadioButton
          label="Legal Sex"
          options={options}
          handleSubmit={(option: string) => setUserData({ ...userData, sex: option })}
        />
        <Label
          label="Citizenship"
          fontWeight={600}
          fontSize="s"
          lineHeight="20px"
          color="darkGrey"
          margin="0 0 10px"
        />
        <Select
          name="citizenship"
          highlightedOptions={frequentlyUsedCountries}
          options={otherCountries}
          placeholder={userData.citizenship || 'Choose from options'}
          handleChange={(option: string) => {
            setUserData({ ...userData, citizenship: option });
          }}
        />
        <ButtonWrapper>
          <Button
            label="Back"
            buttonSize="xxs"
            primary
            onClick={(event: React.MouseEvent) => handleClickBack && handleClickBack(event)}
          />
          <Button label="Next" disabled={!isValid} buttonSize="s" primary={false} />
        </ButtonWrapper>
        <GoToLabel label="Back to" coloredLabel="Log In" navigateTo="/logIn" />
      </Form>
    </>
  );
};
export default PersonalDetailsForm;
