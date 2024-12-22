import React from 'react';

import { Label } from '@/components/common';
import { Wrapper } from './styles';

type Props = {
  service: string;
  duration: string;
  isActive: boolean;
  onClick: () => void;
};

const Service = ({ service, duration, isActive, onClick }: Props) => {
  return (
    <Wrapper onClick={onClick} $isActive={isActive}>
      <Label
        label={service}
        fontWeight={400}
        fontSize="s"
        lineHeight="22px"
        color="darkGrey"
        margin="0"
      />
      <Label
        label={duration}
        fontWeight={400}
        fontSize="s"
        lineHeight="22px"
        color="darkGrey"
        margin="0"
      />
    </Wrapper>
  );
};

export default Service;
