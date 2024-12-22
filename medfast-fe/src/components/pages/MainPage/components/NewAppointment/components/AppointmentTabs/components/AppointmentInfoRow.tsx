import React from 'react';

import { Label, RoundImageBackground, Divider } from '@/components/common';
import { InfoWrapper } from '../../styles';
import { Container } from './styles';

import Icon, { IconType } from '@/components/Icons';

type Props = {
  icon?: IconType;
  imageUrl?: string | null;
  title: string;
  text: string;
  isLast?: boolean;
  isAvailable?: boolean;
  onClick: () => void;
};

const AppointmentInfoRow = ({
  icon,
  imageUrl,
  title,
  text,
  isAvailable = true,
  isLast = false,
  onClick,
}: Props) => {
  return (
    <InfoWrapper $margin={isLast ? '0' : '0 0 24px'} $isAvailable={isAvailable} onClick={onClick}>
      <Container $isLast={isLast}>
        <RoundImageBackground
          $backgroundColor={isAvailable ? 'lightBlue' : 'lightGrey'}
          $borderColor={isAvailable ? 'lightBlue' : 'lightGrey'}
          $backgroundImage={imageUrl}
        >
          {icon && !imageUrl && <Icon type={icon} />}
        </RoundImageBackground>
        <InfoWrapper $margin="0 0 0 16px">
          <Label
            label={title}
            fontWeight={600}
            fontSize="m"
            lineHeight="22px"
            color={isAvailable ? 'darkGrey' : 'grey'}
            margin="0 0 8px"
          />
          <Label
            label={text}
            fontWeight={400}
            fontSize="s"
            lineHeight="22px"
            color={isAvailable ? 'darkGrey' : 'grey'}
            margin="0"
          />
        </InfoWrapper>
      </Container>
      {!isLast && <Divider $height="1px" $color="lightGrey" />}
    </InfoWrapper>
  );
};

export default AppointmentInfoRow;

