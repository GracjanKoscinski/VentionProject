import React from 'react';
import { createPortal } from 'react-dom';

import { FullScreenShadow, TabletWithShadow } from '@/components/common';
import { Wrapper } from './styles';

type Props = {
  children: JSX.Element | JSX.Element[];
};

const PopUpWithShadow = ({ children }: Props) => {
  return createPortal(
    <Wrapper>
      <FullScreenShadow>
        <TabletWithShadow $width="376px">{children}</TabletWithShadow>
      </FullScreenShadow>
    </Wrapper>,
    document.body,
  );
};

export default PopUpWithShadow;
