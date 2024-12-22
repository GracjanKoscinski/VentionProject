import React from 'react';

import { InputLabelElement } from './styles';

type Props = { label: string; htmlFor: string };

const InputLabel = ({ label, htmlFor }: Props) => {
  return <InputLabelElement htmlFor={htmlFor}>{label}</InputLabelElement>;
};

export default InputLabel;
