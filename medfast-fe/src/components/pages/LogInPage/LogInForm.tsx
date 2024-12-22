import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

import { useUser } from '@/utils/UserContext';

import {
  Label,
  Input,
  PasswordInput,
  Button,
  Header,
  Form,
  GoToLabel,
  CheckboxWithLink,
  ScreenWrapperCentred,
  ServerResponse,
  Loader,
} from '@/components/common';
import { Wrapper } from './styles';

import UserDataValidation from '@/utils/UserDataValidation/UserCredentialValidation';

const ERROR_TEXT = {
  email: 'Please enter valid email address',
  password:
    'Password must be 10-50 characters, with an uppercase letter, an lowercase letters, special symbol, and a digit',
};

type Errors = {
  email: null | string;
  password: null | string;
};

const LogInForm = () => {
  const userAuth = useUser();
  const navigate = useNavigate();
  const location = useLocation();
  const [userData, setUserData] = useState<UserData>({
    email: null,
    password: null,
    keepLogged: false,
  });
  const [formErrors, setFormErrors] = useState<Errors>({
    email: null,
    password: null,
  });
  const [serverError, setServerError] = useState<'forbidden' | 'somethingWrong' | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const credentials = {
    userData: {
      email: userData.email || '',
      password: userData.password || '',
    },
  };

  const isValid =
    userData.email !== null &&
    userData.password !== null &&
    userData.email !== '' &&
    userData.password !== '' &&
    formErrors.email === '' &&
    formErrors.password === '';

  const isPatientLogin = location.pathname === '/logIn';


  const validation = (data: string) => {
    const validationResp = [
      UserDataValidation().allowedChar(data, 'email'),
      UserDataValidation().requiredField(data),
    ].filter(Boolean);

    const errors = validationResp.filter((error) => typeof error === 'string');

    if (errors.length === 0) {
      setFormErrors({ ...formErrors, email: '' });
    } else {
      setFormErrors({ ...formErrors, email: ERROR_TEXT.email });
    }
  };

  const passwordValidation = (data: string) => {
    const validationResp = [
      UserDataValidation().allowedChar(data, 'password'),
      UserDataValidation().length(data, 10, 50),
      UserDataValidation().requiredField(data),
    ].filter(Boolean);

    const errors = validationResp.filter((error) => typeof error === 'string');

    if (errors.length === 0) {
      setFormErrors({ ...formErrors, password: '' });
    } else {
      setFormErrors({ ...formErrors, password: ERROR_TEXT.password });
    }
  };

  const handleRedirect = (event: React.MouseEvent) => {
    event.stopPropagation();
    navigate('/password-reset');
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    try {
      setIsLoading(true);

      await userAuth.logIn(credentials);

      if (userAuth.userData?.role === 'ADMIN') {
        navigate('/admin-console', { replace: true });
      } else {
        navigate('/main', { replace: true });
      }
    } catch (error: any) {
      if (error.message === '401') {
        setServerError('forbidden');
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
          <Label
            label="Welcome to Medfast"
            fontWeight={700}
            fontSize="l"
            lineHeight="30px"
            color="darkGrey"
            margin="0 0 12px"
          />
          <Label
            label="We happy to see you! To use your account, you should log in first"
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
          <PasswordInput
            name="Enter password"
            error={formErrors.password || ''}
            isDisabled={isLoading}
            value={userData.password || ''}
            onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
              setUserData({ ...userData, password: event.target.value });
              passwordValidation(event.target.value);
            }}
          />
          <CheckboxWithLink
            label="Keep me logged in"
            right="0"
            labelSize="xs"
            isDisabled={isLoading}
            linkLabel="Forgot your password?"
            isChecked={userData.keepLogged}
            setIsChecked={() => setUserData({ ...userData, keepLogged: !userData.keepLogged })}
            onRedirect={(event: React.MouseEvent) => handleRedirect(event)}
          />
          <>{serverError && <ServerResponse serverError={serverError} />}</>
          <Button
            label={isLoading ? <Loader color="#fff" size="30" /> : 'Log In'}
            buttonSize="m"
            disabled={!isValid}
          />
          {isPatientLogin ? (
          <GoToLabel
            location="center"
            label="Don't have an account?"
            coloredLabel="Register"
            navigateTo="/registration"
          />
        ) : <div/>}
        </Form>
      </ScreenWrapperCentred>
    </Wrapper>
  );
};

export default LogInForm;

export type UserData = {
  email: null | string;
  password: null | string;
  keepLogged: boolean;
};

