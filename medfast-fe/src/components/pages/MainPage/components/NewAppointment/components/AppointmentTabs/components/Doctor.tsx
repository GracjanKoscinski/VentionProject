import React, { useState } from 'react';

import { Label, RoundImageBackground } from '@/components/common';
import { Wrapper, Dot, TitleWrapper, InfoWrapper, DoctorWrapper } from './styles';

import Icon from '@/components/Icons';

type Props = {
  name: string;
  speciality: string[];
  location: string;
  slots: number;
  isActive: boolean;
  imageUrl: string | null;
  onClick: () => void;
};

const Doctor = ({ name, speciality, location, slots, imageUrl, isActive, onClick }: Props) => {
  const nameArray = name.split(' ');
  const doctorInitials = nameArray[0][0] + nameArray[nameArray.length - 1][0];
  const availableSlotsInfo = `${slots} ${slots === 1 ? 'slot' : 'slots'} available today`;

  return (
    <Wrapper onClick={onClick} $isActive={isActive}>
      <DoctorWrapper>
        <TitleWrapper>
          <RoundImageBackground
            id="image"
            $backgroundImage={imageUrl}
            $backgroundColor="white"
            $borderColor={imageUrl ? 'white' : 'purple'}
          >
            {!imageUrl && doctorInitials}
          </RoundImageBackground>
          <DoctorWrapper>
            <Label
              label={name}
              fontWeight={700}
              fontSize="s"
              lineHeight="20px"
              color="darkGrey"
              margin="0 0 0 8px"
            />
            <Label
              label={speciality.join(', ')}
              fontWeight={500}
              fontSize="xs"
              lineHeight="20px"
              color="darkGrey"
              margin="0 0 0 8px"
            />
          </DoctorWrapper>
        </TitleWrapper>
        <InfoWrapper>
          <Icon type="location" />
          <Label
            label={location}
            fontWeight={400}
            fontSize="xs"
            lineHeight="20px"
            color="darkGrey"
            margin="0 0 0 6px"
          />
        </InfoWrapper>
        <InfoWrapper>
          <Dot />
          <Label
            label={availableSlotsInfo}
            fontWeight={400}
            fontSize="xs"
            lineHeight="20px"
            color="darkGrey"
            margin="0 0 0 11px"
          />
        </InfoWrapper>
      </DoctorWrapper>
    </Wrapper>
  );
};

export default Doctor;

