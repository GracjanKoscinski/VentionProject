import React from 'react';

import Checkbox from '@/components/common/Input/Checkbox';
import Label from '@/components/common/Label/Label';
import { Wrapper } from './styles';

type Props = {
  label: string;
  linkLabel: string;
  labelSize: string;
  isChecked: boolean;
  isDisabled?: boolean;
  right?: string;
  setIsChecked: () => void;
  onRedirect: (event: React.MouseEvent) => void;
};

const CheckboxWithLink = ({
  label,
  labelSize,
  linkLabel,
  isChecked,
  isDisabled,
  right = '59px',
  setIsChecked,
  onRedirect,
}: Props) => {
  const handleRedirect = (event: React.MouseEvent) => {
    event.stopPropagation();
    onRedirect(event);
  };

  return (
    <Wrapper $right={right}>
      <Checkbox
        isDisabled={isDisabled}
        labelSize={labelSize}
        label={label}
        isChecked={isChecked}
        setIsChecked={setIsChecked}
      />
      <Label
        label={linkLabel}
        fontWeight={400}
        fontSize={labelSize}
        lineHeight="20px"
        color="purple"
        margin="0"
        onClick={(event: React.MouseEvent) => handleRedirect(event)}
      />
    </Wrapper>
  );
};

export default CheckboxWithLink;

