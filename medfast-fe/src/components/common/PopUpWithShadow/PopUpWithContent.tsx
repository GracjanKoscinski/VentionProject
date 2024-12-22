import React from 'react';

import { Label, Button, PopUpWithShadow } from '@/components/common';
import { ButtonWrapper, ChildrenWrapper } from './styles';

type Props = {
  title: string;
  message: string;
  children?: JSX.Element;
  confirmButton?: string;
  cancelButton?: string;
  cancelMethod?: () => void;
  confirmMethod?: () => void;
};

const PopUpWithContent = ({
  title,
  message,
  children,
  confirmButton,
  cancelButton,
  cancelMethod,
  confirmMethod,
}: Props) => {
  return (
    <PopUpWithShadow>
      <>{children}</>
      <Label
        label={title}
        fontWeight={700}
        fontSize="m"
        lineHeight="30px"
        color="darkGrey"
        margin="0 0 16px 0"
      />
      <Label
        label={message}
        fontWeight={400}
        fontSize="xs"
        lineHeight="24px"
        color="darkGrey"
        margin="0 0 16px 0"
      />
      <ButtonWrapper>
        {cancelButton && (
          <Button
            label={cancelButton || ''}
            buttonSize={children ? 'm' : 's'}
            primary
            onClick={cancelMethod}
          />
        )}
        {confirmButton && (
          <Button
            label={confirmButton || ''}
            buttonSize={children ? 'm' : 's'}
            onClick={confirmMethod}
          />
        )}
      </ButtonWrapper>
    </PopUpWithShadow>
  );
};

export default PopUpWithContent;
