import React from 'react';

import { TabletWithShadow, Label, Button } from '@/components/common';
import { WrapperRow, WrapperColumn } from './styles';

import Icon from '@/components/Icons';
import { IconType } from '@/components/Icons';

type Props = {
  buttonLabel?: string | null;
  label: string;
  icon?: IconType | null;
};

const TabletWithoutData = ({ buttonLabel, label, icon = 'doctorImage' }: Props) => {
  const Wrapper = buttonLabel ? WrapperRow : WrapperColumn;

  return (
    <TabletWithShadow $padding="24px">
      <Wrapper>
        <Label
          label={label}
          fontWeight={500}
          fontSize="s"
          lineHeight="22px"
          color="darkGrey"
          margin={buttonLabel ? '0' : '0 0 20px 0'}
        />
        {buttonLabel && <Button borderRadius="oval" label={buttonLabel} buttonSize="s" />}
        {icon && <Icon type={icon} />}
      </Wrapper>
    </TabletWithShadow>
  );
};

export default TabletWithoutData;

