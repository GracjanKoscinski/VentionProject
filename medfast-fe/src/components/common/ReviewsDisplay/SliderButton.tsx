import React from 'react';

import { Round, Arrow, Line } from './styles';

type Props = {
  rotate: string;
};

const SliderButton = ({ rotate }: Props) => {
  return (
    <Round $rotate={rotate}>
      <Arrow />
      <Line />
    </Round>
  );
};

export default SliderButton;
