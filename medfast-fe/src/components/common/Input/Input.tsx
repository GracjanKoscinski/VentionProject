import React from 'react';

import { Wrapper, InputElement, ErrorElement } from './styles';
import InputLabel from './InputLabel';

type Props = {
  name: string;
  type: string;
  placeholder?: string;
  value: string;
  label?: string;
  readOnly?: boolean;
  isDisabled?: boolean;
  isInvalid?: string | null;
  onChange?: (event: React.ChangeEvent<HTMLInputElement>) => void;
  onBlur?: (event: React.FocusEvent<HTMLInputElement>) => void;
  onKeyUp?: (event: React.KeyboardEvent<HTMLInputElement>) => void;
};

const Input = ({
  name,
  type,
  placeholder,
  value,
  readOnly,
  isInvalid = '',
  isDisabled = false,
  label = '',
  onChange,
  onBlur,
  onKeyUp,
}: Props) => {
  return (
    <Wrapper>
      <InputLabel label={label} htmlFor={name} />
      <InputElement
        readOnly={readOnly}
        disabled={isDisabled}
        $isDisabled={isDisabled}
        name={name}
        type={type}
        placeholder={placeholder}
        $isInvalid={isInvalid === ''}
        value={value}
        onChange={(event: React.ChangeEvent<HTMLInputElement>) => onChange && onChange(event)}
        onBlur={onBlur}
        onKeyUp={onKeyUp}
      />
      <ErrorElement id="error">{isInvalid}</ErrorElement>
    </Wrapper>
  );
};

export default Input;
