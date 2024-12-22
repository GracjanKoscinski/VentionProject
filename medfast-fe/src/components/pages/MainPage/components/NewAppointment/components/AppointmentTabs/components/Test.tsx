import React from 'react';

import { Label } from '@/components/common';
import { Wrapper } from './styles';

type Props = {
  test: string;
  isActive: boolean;
  onClick: () => void;
};

const Test = ({ test, isActive, onClick }: Props) => {
  return (
    <Wrapper onClick={onClick} $isActive={isActive}>
      <Label
        label={test}
        fontWeight={400}
        fontSize="s"
        lineHeight="22px"
        color="darkGrey"
        margin="0"
      />
    </Wrapper>
  );
};

export default Test;
