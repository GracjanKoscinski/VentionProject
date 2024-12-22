import React from 'react';

import { Divider, Label } from '@/components/common';
import { WrapperWithShadow, InfoWrapper } from './styles';

import Icon from '@/components/Icons';
import { IconType } from '@/components/Icons';

type Props = {
  icon: IconType;
  title: string;
  text: string;
  onClick: () => void;
};

const AppointmentType = ({ icon, title, text, onClick }: Props) => {
  return (
    <WrapperWithShadow $flexDirection="row" onClick={onClick}>
      <Icon type={icon} />
      <InfoWrapper $margin="0 0 0 32px" $width="310px">
        <Label
          label={title}
          fontWeight={600}
          fontSize="m"
          lineHeight="22px"
          color="darkGrey"
          margin="0 0 16px"
        />
        <Divider $height="1px" $color="lightGrey" />
        <Label
          label={text}
          fontWeight={400}
          fontSize="s"
          lineHeight="22px"
          color="darkGrey"
          margin="16px 0 0"
        />
      </InfoWrapper>
    </WrapperWithShadow>
  );
};

export default AppointmentType;

