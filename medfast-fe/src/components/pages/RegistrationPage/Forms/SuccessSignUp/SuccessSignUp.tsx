import React from 'react';

import { ScreenWrapperCentred, TabletWithShadow, Label } from '@/components/common';
import { Wrapper } from './styles';

type Props = {
  title: string;
  message: string;
  children?: JSX.Element | JSX.Element[];
};

const SuccessSignUp = ({ title, message, children }: Props) => {
  return (
    <ScreenWrapperCentred>
      <Wrapper>
        <TabletWithShadow>
          <Label
            label={title}
            fontWeight={700}
            fontSize="l"
            lineHeight="30px"
            color="darkGrey"
            margin="0 0 30px"
          />
          <Label
            label={message}
            fontWeight={400}
            fontSize="s"
            lineHeight="22px"
            color="darkGrey"
            margin="0 0 16px 0"
          />
          {children}
        </TabletWithShadow>
      </Wrapper>
    </ScreenWrapperCentred>
  );
};

export default SuccessSignUp;

