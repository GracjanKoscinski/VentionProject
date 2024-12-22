import React from 'react';

import Label from '@/components/common/Label/Label';
import Review from './Review';
import { Wrapper } from './styles';

const ReviewsDisplay = () => {
  return (
    <Wrapper>
      <Label
        label="Patients have more options with review websites that help them find cheaper rates."
        fontWeight={800}
        fontSize="l"
        lineHeight="32px"
        color="white"
        margin="0 0 24px"
      />
      <Review userName="Sophie Hall (Medical center visitor)" />
    </Wrapper>
  );
};

export default ReviewsDisplay;
