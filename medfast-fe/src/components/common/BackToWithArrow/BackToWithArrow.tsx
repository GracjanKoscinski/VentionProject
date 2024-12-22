import React from 'react';

import Label from '@/components/common/Label/Label';
import Icon from '@/components/Icons';

import { ButtonWrapper } from './styles';

type Props = {
  label: string;
  onClick: () => void;
};

const BackToWithArrow = ({ label, onClick }: Props) => {
  return (
    <ButtonWrapper onClick={onClick}>
      <Icon type="arrowBack" />
      <Label
        label={label}
        fontWeight={500}
        fontSize="s"
        lineHeight="20px"
        color="darkGrey"
        margin="0 0 0 20px"
      />
    </ButtonWrapper>
  );
};

export default BackToWithArrow;

