import React from 'react';

import { VisitDataWrapper, CareTeamWrapper } from './styles';
import { Label, RoundImageBackground, Button } from '@/components/common';

import Icon from '@/components/Icons';

import Image from '@/mocks/DoctorAvatarTest.png';

type Props = {
  member: { name: string; surname: string; speciality: string; imageUrl: string | null };
};

const CareTeamMember = ({ member }: Props) => {
  return (
    <>
      <CareTeamWrapper>
        <VisitDataWrapper>
          <RoundImageBackground
            $backgroundImage={Image}
            $backgroundColor="white"
            $borderColor={Image ? 'white' : 'purple'}
          >
            {!Image && member.name[0] + member.surname[0]}
          </RoundImageBackground>
          <Label
            label={member.speciality}
            fontWeight={600}
            fontSize="s"
            lineHeight="20px"
            color="darkGrey"
            margin="0 0 0 16px"
          />
          <Label
            label={`Dr. ${member.name} ${member.surname}`}
            fontWeight={400}
            fontSize="s"
            lineHeight="22px"
            color="darkGrey"
            margin="0"
          />
          <Button label={<Icon type="arrowBack" />} buttonSize="xs" borderRadius="oval" />
        </VisitDataWrapper>
      </CareTeamWrapper>
    </>
  );
};

export default CareTeamMember;
