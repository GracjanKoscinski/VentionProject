import React from 'react';

import RegStatusComp from '@/components/common/RegStepsStatus/RegStatusComp';

import { DataType, FormsProps } from '@/components/pages/RegistrationPage/Forms/FormsProps';

import { Wrapper } from './styles';

import Icon from '@/components/Icons';

type Props<T> = {
  currentStage?: number | null;
  stages?: {
    id: number;
    component: ({ userData, setUserData, onSubmit }: FormsProps<T>) => JSX.Element;
  }[];
};

const Header = <T extends DataType>({ currentStage = null, stages = [] }: Props<T>) => {
  const isRegistrationPage = typeof currentStage === 'number';

  return (
    <Wrapper>
      <Icon type="logo" />
      {isRegistrationPage && <RegStatusComp currentStage={currentStage} stages={stages} />}
    </Wrapper>
  );
};

export default Header;

