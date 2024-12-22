import React from 'react';

import SliderButton from './SliderButton';
import { StarsWrapper, ButtonWrapper, Rating, ReviewWrapper } from './styles';
import Label from '@/components/common/Label/Label';

import Icon from '@/components/Icons';

type Props = { userName: string };

const Review = ({ userName }: Props) => {
  return (
    <ReviewWrapper>
      <Rating>
        <Label
          label={userName}
          fontWeight={600}
          fontSize="s"
          lineHeight="32px"
          color="white"
          margin="0"
        />
        <StarsWrapper>
          <Icon type="starIcon" />
          <Icon type="starIcon" />
          <Icon type="starIcon" />
          <Icon type="starIcon" />
          <Icon type="starIcon" />
        </StarsWrapper>
      </Rating>
      <ButtonWrapper>
        <SliderButton rotate="0" />
        <SliderButton rotate="180deg" />
      </ButtonWrapper>
    </ReviewWrapper>
  );
};

export default Review;
