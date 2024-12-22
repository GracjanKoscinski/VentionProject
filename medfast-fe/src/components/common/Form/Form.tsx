import React from 'react';

import { FormElement } from './styles';

type Props = {
  children: JSX.Element | JSX.Element[];
  isBordered?: boolean;
  onSubmit: (event: React.FormEvent) => void;
};

const Form = ({ children, isBordered = true, onSubmit }: Props) => {
  return (
    <FormElement $isBordered={isBordered} onSubmit={onSubmit}>
      {children}
    </FormElement>
  );
};

export default Form;
