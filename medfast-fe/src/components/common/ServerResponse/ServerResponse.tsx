import React from 'react';

import { Wrapper, Line, ResponseMessage, CrossWrapper } from './styles';

import Icon from '@/components/Icons';

type Props = {
  serverError: keyof typeof ERROR_MESSAGES;
  hasCross?: boolean;
  onClick?: () => void;
};

type ErrorProps = {
  text: string;
  type: 'error' | 'warning' | 'success';
};

export const ERROR_MESSAGES: Record<string, ErrorProps> = {
  specializationNotFound: { text: 'Specialization has not been found.', type: 'error' },
  hasBeenChanged: { text: 'Password has been successfully changed.', type: 'success' },
  hasBeenUpdated: { text: 'Your information has been changed succesfully.', type: 'success' },
  tooManyRequests: { text: 'Too many requests. Try again in 30 minutes.', type: 'warning' },
  forbidden: { text: 'We cannot find an account with this email address.', type: 'error' },
  sameAsOld: { text: 'The new password cannot be the same as the old one.', type: 'error' },
  somethingWrong: { text: 'Something went wrong! Please try again later.', type: 'warning' },
  alreadyExist: { text: 'User with this email address already exists.', type: 'error' },
  alreadyVerified: { text: 'Your email address is already verified.', type: 'error' },
  wrongCode: { text: 'Code is wrong', type: 'error' },
  wrongFileExtension: {
    text: 'Upload a file with the following extensions: jpg, gif, png, jpeg.',
    type: 'error',
  },
  newVerificationCode: {
    text: 'Something went wrong! Please resend verification code.',
    type: 'warning',
  },
};

const ServerResponse = ({ serverError, hasCross, onClick }: Props) => {
  const error = ERROR_MESSAGES[serverError];

  return (
    <Wrapper $response={error.type}>
      <Line $response={error.type} />
      <Icon type={error.type} />
      <CrossWrapper onClick={onClick}>{hasCross && <Icon type="cross" />}</CrossWrapper>
      <ResponseMessage>{error.text}</ResponseMessage>
    </Wrapper>
  );
};

export default ServerResponse;

