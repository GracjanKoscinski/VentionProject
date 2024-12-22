import React, { useEffect, useState } from 'react';

import {
  Label,
  Button,
  Header,
  Form,
  ScreenWrapperCentred,
  ServerResponse,
  Input,
  GoToLabel,
  RoundImageBackground,
  Loader,
} from '@/components/common';
import { Wrapper, ResetCodeWrapper, ResendWrapper } from './styles';
import Icon from '@/components/Icons';

import { FormsProps } from './FormsProps';

import { PasswordResetEmailVerification } from '@/api/PasswordResetEmailVerification';
import { PasswordResetConfirmationCode } from '@/api/PasswordResetConfirmationCode';

const ConfirmationCodeForm = ({ userData, setUserData, onSubmit }: FormsProps) => {
  const [resendCode, setResendCode] = useState(false);
  const [isTimer, setIsTimer] = useState(false);
  const [attempts, setAttempts] = useState(0);
  const [seconds, setSeconds] = useState(60);
  const [serverError, setServerError] = useState<
    'wrongCode' | 'somethingWrong' | 'tooManyRequests' | null
  >(null);
  const [confirmationCode, setConfirmationCode] = useState<CodeDigits>({
    first: '',
    second: '',
    third: '',
    fourth: '',
  });
  const [isLoading, setIsLoading] = useState(false);

  const labelWithEmail = `We emailed a confirmation code with 4 numbers to ${userData.email} `;

  const isValid =
    confirmationCode.first !== '' &&
    confirmationCode.second !== '' &&
    confirmationCode.third !== '' &&
    confirmationCode.fourth !== '';

  const email = userData.email || '';

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    const code =
      confirmationCode.first +
      confirmationCode.second +
      confirmationCode.third +
      confirmationCode.fourth;

    setUserData({ ...userData, token: code });

    try {
      await PasswordResetConfirmationCode(email, code);

      onSubmit();
    } catch (error: any) {
      if (error.message === '400') {
        setServerError('wrongCode');
      } else if (error.message === '429') {
        setServerError('tooManyRequests');
      } else {
        setServerError('somethingWrong');
      }
    }
  };

  const updateConfirmationCode = (value: string, order: string) => {
    value.length <= 1 && setConfirmationCode({ ...confirmationCode, [order]: value });
  };

  const formatTime = (timeInSeconds: number) => {
    const minutes = Math.floor(timeInSeconds / 60)
      .toString()
      .padStart(2, '0');
    const seconds = (timeInSeconds % 60).toString().padStart(2, '0');

    return `${minutes}:${seconds}`;
  };

  useEffect(() => {
    if (seconds === 0) {
      setIsTimer(false);
      setResendCode(true);
    }
    const interval = setInterval(() => setSeconds(seconds - 1), 1000);

    return () => clearInterval(interval);
  }, [seconds]);

  const updateAttempts = () => {
    const currentAttempt = attempts + 1;
    setAttempts(currentAttempt);

    if (currentAttempt === 3) {
      localStorage.setItem('thirdAttempt', JSON.stringify(new Date().getTime()));
    } else {
      localStorage.setItem('currentAttempt', JSON.stringify(currentAttempt));
    }
  };

  const handleResendCode = async () => {
    const presentTime = new Date().getTime();
    const thirdAttemptTime = JSON.parse(localStorage.getItem('thirdAttempt') || 'null');
    const pastMinutes = Math.floor((presentTime - thirdAttemptTime) / 60000);

    if (pastMinutes < 30) {
      setServerError('tooManyRequests');
    } else {
      !isTimer && setSeconds(60);
      updateAttempts();

      try {
        setIsLoading(true);

        await PasswordResetEmailVerification(userData.email || '');

        setIsTimer(true);
      } catch (error: any) {
        if (error.message === '400') {
          setServerError('wrongCode');
        } else {
          setServerError('somethingWrong');
        }
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
            <Icon type="unreadMessageIcon" />
          </RoundImageBackground>
          <Label
            label="Check your email"
            fontWeight={700}
            fontSize="l"
            lineHeight="30px"
            color="darkGrey"
            margin="12px 0 12px"
          />
          <Label
            label={labelWithEmail}
            fontWeight={400}
            fontSize="s"
            lineHeight="22px"
            color="darkGrey"
            margin="0 0 24px"
          />
          <ResetCodeWrapper>
            <Input
              name="codeDigit"
              type="number"
              isDisabled={isLoading}
              value={confirmationCode.first}
              onChange={(event: React.ChangeEvent<HTMLInputElement>) =>
                updateConfirmationCode(event.target.value, 'first')
              }
            />
            <Input
              name="codeDigit"
              type="number"
              isDisabled={isLoading}
              value={confirmationCode.second}
              onChange={(event: React.ChangeEvent<HTMLInputElement>) =>
                updateConfirmationCode(event.target.value, 'second')
              }
            />
            <Input
              name="codeDigit"
              type="number"
              isDisabled={isLoading}
              value={confirmationCode.third}
              onChange={(event: React.ChangeEvent<HTMLInputElement>) =>
                updateConfirmationCode(event.target.value, 'third')
              }
            />
            <Input
              name="codeDigit"
              type="number"
              isDisabled={isLoading}
              value={confirmationCode.fourth}
              onChange={(event: React.ChangeEvent<HTMLInputElement>) =>
                updateConfirmationCode(event.target.value, 'fourth')
              }
            />
          </ResetCodeWrapper>
          <>
            {resendCode && (
              <ResendWrapper onClick={() => handleResendCode()}>
                <Label
                  label="Resend code"
                  fontWeight={400}
                  fontSize="s"
                  lineHeight="22px"
                  color="darkGrey"
                  margin="0 4px 0 0"
                />
                {!isTimer && <Icon type="redoIcon" />}
                {isTimer && (
                  <>
                    <Label
                      label="in"
                      fontWeight={400}
                      fontSize="s"
                      lineHeight="22px"
                      color="darkGrey"
                      margin="0 4px 0 0"
                    />
                    <Label
                      label={formatTime(seconds)}
                      fontWeight={600}
                      fontSize="s"
                      lineHeight="22px"
                      color="darkGrey"
                      margin="0"
                    />
                  </>
                )}
              </ResendWrapper>
            )}
          </>
          <>{serverError && <ServerResponse serverError={serverError} />}</>
          <Button label="Verify" buttonSize="m" disabled={!isValid} />
          <GoToLabel location="center" label="Back to" coloredLabel="Log In" navigateTo="/logIn" />
        </Form>
      </ScreenWrapperCentred>
    </Wrapper>
  );
};

export default ConfirmationCodeForm;

type CodeDigits = {
  first: string;
  second: string;
  third: string;
  fourth: string;
};

