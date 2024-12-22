import React, { useEffect, useRef } from 'react';
import { createPortal } from 'react-dom';

import { FullScreenShadow, TabletWithShadow } from '@/components/common';
import { PopUpWrapper, Wrapper } from './styles';

type Props = {
  isOpen: boolean;
  handleClose: (isOpen: boolean) => void;
  children: JSX.Element | JSX.Element[];
};

const RightSidePopUp = ({ isOpen, handleClose, children }: Props) => {
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (ref.current && ref.current.contains(event.target as Node)) {
        handleClose(false);
      }
    };
    document.addEventListener('click', handleClickOutside);

    return () => document.removeEventListener('click', handleClickOutside);
  }, []);

  return createPortal(
    <PopUpWrapper $isOpen={isOpen}>
      <FullScreenShadow ref={ref} />
      <Wrapper>
        <TabletWithShadow $shape="56px 0 0 56px" $height="100vh">
          {children}
        </TabletWithShadow>
      </Wrapper>
    </PopUpWrapper>,
    document.body,
  );
};

export default RightSidePopUp;
