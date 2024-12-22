import React, { useState, useEffect } from 'react';

import {
  Label,
  Input,
  Button,
  Header,
  Form,
  GoToLabel,
  ScreenWrapperCentred,
  ServerResponse,
  RoundImageBackground,
  Loader,
} from '@/components/common';
import { Wrapper } from './styles';
import Icon from '@/components/Icons';

import UserDataValidation from '@/utils/UserDataValidation/UserCredentialValidation';

import { FormsProps } from './FormsProps';

import { PasswordResetEmailVerification } from '@/api/PasswordResetEmailVerification';

const ERROR_TEXT = {
  email: 'Please enter valid email address',
};

type Errors = {
  email: null | string;
};

const EmailConfirmationForm = ({ userData, setUserData, onSubmit }: FormsProps) => {
  const [serverError, setServerError] = useState<'forbidden' | 'somethingWrong' | null>(null);
  const [formErrors, setFormErrors] = useState<Errors>({
    email: null,
  });
  const [isLoading, setIsLoading] = useState(false);

  const isValid = userData.email !== null && userData.email !== '' && formErrors.email === '';

  const validation = (data: string) => {
    const validationResp = [
      UserDataValidation().allowedChar(data, 'email'),
      UserDataValidation().requiredField(data),
    ].filter(Boolean);

    const errors = validationResp.filter((error) => typeof error === 'string');

    if (errors.length === 0) {
      setFormErrors({ email: '' });
    } else {
      setFormErrors({ email: ERROR_TEXT.email });
    }
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    try {
      setIsLoading(true);

      await PasswordResetEmailVerification(userData.email || '');

      onSubmit();
    } catch (error: any) {
      if (error.message === '403') {
        setServerError('forbidden');
      } else {
        setServerError('somethingWrong');
      }

      setIsLoading(false);
    }
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
            label="Forgot your password?"
            fontWeight={700}
            fontSize="l"
            lineHeight="30px"
            color="darkGrey"
            margin="12px 0 12px"
          />
          <Label
            label="Please enter your email address. We will send a code to change your password"
            fontWeight={400}
            fontSize="s"
            lineHeight="22px"
            color="darkGrey"
            margin="0 0 24px"
          />
          <Input
            name="email"
            type="email"
            isDisabled={isLoading}
            placeholder="Name@gmail.com"
            isInvalid={formErrors.email || ''}
            value={userData.email || ''}
            label="Email"
            onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
              setUserData({ ...userData, email: event.target.value });
              validation(event.target.value);
            }}
          />
          <>{serverError && <ServerResponse serverError={serverError} />}</>
          <Button label="Send code" buttonSize="m" disabled={!isValid} />
          <GoToLabel location="center" label="Back to" coloredLabel="Log In" navigateTo="/logIn" />
        </Form>
      </ScreenWrapperCentred>
    </Wrapper>
  );
};

export default EmailConfirmationForm;

