import React from 'react';

import Input from './Input';
import { CheckboxWrapper } from './styles';

import Icon from '@/components/Icons';

type Props = {
  label: string;
  labelSize: string;
  isChecked: boolean;
  isDisabled?: boolean;
  setIsChecked: () => void;
};

const Checkbox = ({ label, labelSize, isChecked, isDisabled, setIsChecked }: Props) => {
  return (
    <CheckboxWrapper $labelSize={labelSize} $isChecked={isChecked} onClick={setIsChecked}>
      <Icon type="checkMarkIcon" />
      <Input label={label} name={label} type="checkbox" value={label} isDisabled={isDisabled} />
    </CheckboxWrapper>
  );
};

export default Checkbox;
