import React from 'react';
import { useNavigate } from 'react-router-dom';

import Label from '@/components/common/Label/Label';
import { Wrapper } from './styles';

type Props = {
  label: string;
  coloredLabel: string;
  location?: string;
  navigateTo: string;
};

const GoToLabel = ({ label, coloredLabel, location = 'flex-end', navigateTo }: Props) => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(navigateTo);
  };

  return (
    <Wrapper $location={location}>
      <Label
        label={label}
        fontWeight={400}
        fontSize="xs"
        lineHeight="20px"
        color="darkGrey"
        margin="0"
      />
      <Label
        onClick={handleClick}
        label={coloredLabel}
        fontWeight={600}
        fontSize="xs"
        lineHeight="20px"
        color="purple"
        margin="0"
      />
    </Wrapper>
  );
};

export default GoToLabel;

