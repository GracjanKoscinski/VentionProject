import React from 'react';

import { Line } from './styles';

type Props = {
  stageNumber: number;
  currentStage: number;
};

const LineDivider = ({ stageNumber, currentStage }: Props) => {
  const isDone = stageNumber < currentStage + 1;

  return <Line $isDone={isDone} />;
};

export default LineDivider;
