import React from 'react';

import { TwoColumnsScreen, ReviewsDisplay } from '@/components/common';
import LogInForm from './LogInForm';

const LogInPage = () => {
  return (
    <TwoColumnsScreen>
      <LogInForm />
      <ReviewsDisplay />
    </TwoColumnsScreen>
  );
};

export default LogInPage;
