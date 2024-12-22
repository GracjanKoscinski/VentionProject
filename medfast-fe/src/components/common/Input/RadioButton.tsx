import React, { useState } from 'react';

import Input from '@/components/common/Input/Input';
import Label from '@/components/common/Label/Label';
import { RadioButtonWrapper, RadioButtonsContainer } from './styles';

type Props = {
  label: string;
  options: string[];
  handleSubmit: (option: string) => void;
};

const RadioButton = ({ label, options, handleSubmit }: Props) => {
  const [isChecked, setIsChecked] = useState(options[0]);

  const handleClick = (option: string) => {
    setIsChecked(option);
    handleSubmit(option);
  };

  return (
    <>
      <Label
        label={label}
        fontWeight={600}
        fontSize="s"
        lineHeight="20px"
        color="darkGrey"
        margin="0 0 10px"
      />
      <RadioButtonsContainer>
        {options.map((option) => (
          <RadioButtonWrapper
            key={option}
            $isChecked={option === isChecked}
            onClick={() => handleClick(option)}
          >
            <Input readOnly label={option} name={option} type="radio" value={option} />
          </RadioButtonWrapper>
        ))}
      </RadioButtonsContainer>
    </>
  );
};

export default RadioButton;
