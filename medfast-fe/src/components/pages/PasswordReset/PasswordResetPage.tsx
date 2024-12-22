import React, { useState } from 'react';

import { TwoColumnsScreen, ReviewsDisplay } from '@/components/common';
import {
  EmailConfirmationForm,
  ConfirmationCodeForm,
  NewPasswordForm,
  SuccessReset,
} from './Forms';

const PasswordResetPage = () => {
  const [userData, setUserData] = useState<UserData>({
    email: null,
    token: null,
    newPassword: null,
    repeatedPassword: null,
  });
  const [stageNumber, setStageNumber] = useState(0);
  const stages = [EmailConfirmationForm, ConfirmationCodeForm, NewPasswordForm, SuccessReset];
  const CurrentBody = stages[stageNumber];

  const handleSubmit = () => {
    setStageNumber(stageNumber + 1);
  };

  return (
    <TwoColumnsScreen>
      <CurrentBody userData={userData} setUserData={setUserData} onSubmit={handleSubmit} />
      <ReviewsDisplay />
    </TwoColumnsScreen>
  );
};

export default PasswordResetPage;

export type UserData = {
  email: string | null;
  token: string | null;
  newPassword: string | null;
  repeatedPassword: string | null;
};
