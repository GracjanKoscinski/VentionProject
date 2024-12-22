import React from 'react';

import { Label } from '@/components/common';
import { Wrapper } from './styles';

type Props = {
  name: string;
  address: string;
  isActive: boolean;
  onClick: () => void;
};

const Location = ({ name, address, isActive, onClick }: Props) => {
  return (
    <Wrapper onClick={onClick} $isActive={isActive}>
      <Label
        label={name}
        fontWeight={400}
        fontSize="s"
        lineHeight="22px"
        color="darkGrey"
        margin="0"
      />
      <Label
        label={address}
        fontWeight={400}
        fontSize="s"
        lineHeight="22px"
        color="darkGrey"
        margin="0"
      />
    </Wrapper>
  );
};

export default Location;
