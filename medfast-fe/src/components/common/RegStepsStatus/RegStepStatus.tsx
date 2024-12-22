import React from 'react';

import { Wrapper } from './styles';

import Icon from '@/components/Icons';

type Props = {
  stageNumber: number;
  currentStage: number;
};

const RegStepStatus = ({ stageNumber, currentStage }: Props) => {
  const isDone = stageNumber < currentStage + 1;
  const isNext = stageNumber > currentStage + 1;

  return (
    <Wrapper $isDone={isDone} $isNext={isNext}>
      {isDone ? <Icon type="checkMarkIcon" /> : stageNumber}
    </Wrapper>
  );
};

export default RegStepStatus;
