import React from 'react';

import { ButtonElement } from './styles';

type Props = {
  label: string | JSX.Element;
  disabled?: boolean;
  buttonSize: string;
  borderRadius?: string;
  primary?: boolean;
  onClick?: (event: React.MouseEvent) => void;
};

const Button = ({
  label,
  borderRadius = 'square',
  disabled = false,
  buttonSize,
  primary = false,
  onClick,
}: Props) => {
  return (
    <ButtonElement
      disabled={disabled}
      $primary={primary}
      $disabled={disabled}
      $buttonWidth={buttonSize}
      $borderRadius={borderRadius}
      onClick={onClick}
    >
      {label}
    </ButtonElement>
  );
};

export default Button;
