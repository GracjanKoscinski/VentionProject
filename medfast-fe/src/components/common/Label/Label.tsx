import React from 'react';

import { LabelElement } from './styles';

type Props = {
  label: string;
  fontWeight: number;
  fontSize: string;
  lineHeight: string;
  color: string;
  margin: string;
  onClick?: (event: React.MouseEvent) => void;
};

const Label = ({ label, fontWeight, fontSize, lineHeight, color, margin, onClick }: Props) => {
  return (
    <LabelElement
      id="highlightText"
      onClick={onClick}
      $fontWeight={fontWeight}
      $fontSize={fontSize}
      $lineHeight={lineHeight}
      $color={color}
      $margin={margin}
    >
      {label}
    </LabelElement>
  );
};

export default Label;
