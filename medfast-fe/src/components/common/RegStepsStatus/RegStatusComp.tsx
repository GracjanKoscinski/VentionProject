import React from 'react';

import LineDivider from './LineDivider';
import RegStepStatus from './RegStepStatus';

import { FormsProps } from '@/components/pages/RegistrationPage/Forms';
import { StatusChain, RegStepStatusWrapper } from './styles';

import { DataType } from '@/components/pages/RegistrationPage/Forms/FormsProps';

type Props<T> = {
  currentStage: number;
  stages: {
    id: number;
    component: ({ userData, setUserData, onSubmit }: FormsProps<T>) => JSX.Element;
  }[];
};

const RegStatusComp = <T extends DataType>({ currentStage, stages }: Props<T>) => {
  return (
    <StatusChain>
      {stages.map((stage, index) => (
        <RegStepStatusWrapper key={stage.id}>
          <RegStepStatus stageNumber={index + 1} currentStage={currentStage} />
          {index < stages.length - 1 && (
            <LineDivider currentStage={currentStage} stageNumber={index + 1} />
          )}
        </RegStepStatusWrapper>
      ))}
    </StatusChain>
  );
};

export default RegStatusComp;
