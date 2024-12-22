import React, { useState } from 'react';

import Input from './Input';
import { PasswordInputWrapper, IconsWrapper } from './styles';

import Icon from '@/components/Icons';

type Props = {
  name: string;
  value: string;
  error: string;
  isDisabled?: boolean;
  onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
};

const PasswordInput = ({ name, value, error, isDisabled, onChange }: Props) => {
  const [isVisible, setIsVisible] = useState(false);
  const [inputType, setInputType] = useState<'password' | 'text'>('password');
  const [capsLock, setCapsLock] = useState(false);

  const handleClick = () => {
    setIsVisible(!isVisible);

    inputType === 'text' ? setInputType('password') : setInputType('text');
  };

  const handleCapsLock = (event: React.KeyboardEvent<HTMLInputElement>) => {
    const capsLock = event.getModifierState('CapsLock');

    if (capsLock) {
      setCapsLock(true);
    } else {
      setCapsLock(false);
    }
  };

  return (
    <PasswordInputWrapper>
      <Input
        name={name}
        type={inputType}
        placeholder={name}
        value={value}
        isInvalid={error}
        label={name}
        isDisabled={isDisabled}
        onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
          onChange(event);
        }}
        onKeyUp={(event: React.KeyboardEvent<HTMLInputElement>) => {
          handleCapsLock(event);
        }}
      />
      {capsLock && <Icon type="capsLockIcon" />}
      <IconsWrapper onClick={handleClick}>
        {isVisible && <Icon type="passwordVisible" />}
        {!isVisible && <Icon type="passwordInvisible" />}
      </IconsWrapper>
    </PasswordInputWrapper>
  );
};
export default PasswordInput;
