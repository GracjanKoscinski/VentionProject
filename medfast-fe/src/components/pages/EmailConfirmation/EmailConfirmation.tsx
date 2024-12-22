import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

import { ScreenWrapperCentred, Form, Label, Button, ServerResponse } from '@/components/common';
import { SuccessSignUp } from '@/components/pages/RegistrationPage/Forms';
import { Wrapper } from './styles';

import { UserEmailVerification } from '@/api/UserEmailVerification';
import { ReverifyUserEmail } from '@/api/ReverifyEmail';

const EmailConfirmation = () => {
  const location = useLocation();
  const [serverError, setServerError] = useState<'newVerificationCode' | 'alreadyVerified' | null>(
    null,
  );
  const [isReverified, setIsReverified] = useState(false);
  const [responseLabel, setResponseLabel] = useState('Your email address has been verified');
  const emailConfirmationMessage = 'Please check your email to verify your account';

  const navigate = useNavigate();

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    navigate('/login');
  };

  const handleEmailConfirmation = async () => {
    try {
      await UserEmailVerification(location.search);
    } catch (error: any) {
      // the error message should be replaced with '409' after BE error status fix
      if (error.message === '400') {
        setServerError('alreadyVerified');
      } else {
        setServerError('newVerificationCode');
        setResponseLabel('Your email address has not been verified');
      }
    }
  };

  const handleReverifyUserEmail = async (event: React.MouseEvent) => {
    event.preventDefault();

    const data = location.search;
    const email = data.substring(data.indexOf('=') + 1, data.lastIndexOf('&'));

    try {
      await ReverifyUserEmail(email);
      setIsReverified(true);
    } catch (error) {
      setServerError('newVerificationCode');
    }
  };

  useEffect(() => {
    handleEmailConfirmation();
  }, []);

  return (
    <ScreenWrapperCentred>
      {!isReverified && (
        <Wrapper>
          <Form onSubmit={(event: React.FormEvent) => handleSubmit(event)}>
            <Label
              label={responseLabel}
              fontWeight={700}
              fontSize="l"
              lineHeight="30px"
              color="darkGrey"
              margin="0 0 32px"
            />
            <>
              {serverError && (
                <>
                  <ServerResponse serverError={serverError} />
                  {serverError !== 'alreadyVerified' && (
                    <Button
                      label="Send new verification code"
                      buttonSize="m"
                      onClick={(event: React.MouseEvent) => {
                        handleReverifyUserEmail(event);
                      }}
                    />
                  )}
                </>
              )}
            </>
            <>
              {!serverError && (
                <Button disabled={!!serverError} label="Go to Log In" buttonSize="m" />
              )}
            </>
          </Form>
        </Wrapper>
      )}
      {isReverified && (
        <SuccessSignUp
          title="Please confirm your email address"
          message={emailConfirmationMessage}
        />
      )}
    </ScreenWrapperCentred>
  );
};

export default EmailConfirmation;

