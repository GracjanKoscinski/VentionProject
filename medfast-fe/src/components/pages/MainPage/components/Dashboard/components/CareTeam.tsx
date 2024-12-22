import React, { useState } from 'react';

import { Label, TabletWithShadow, Divider } from '@/components/common';
import TabletWithoutData from './TabletWithoutData';
import { DataWrapper, InfoWrapper, MemberContainer } from './styles';
import CareTeamMember from './CareTeamMember';

import doctorsData from '@/mocks/doctors.json';

const CareTeam = () => {
  const doctors = doctorsData;
  const [careTeam, setCareTeam] = useState({ data: doctors.doctors.slice(0, 3), button: true });
  const handleMoreData = () => {
    setCareTeam({ data: doctors.doctors, button: false });
  };

  return (
    <DataWrapper>
      <Label
        label="Your care team"
        fontWeight={700}
        fontSize="m"
        lineHeight="22px"
        color="darkGrey"
        margin="0 0 16px 0"
      />
      {careTeam.data ? (
        <InfoWrapper>
          <TabletWithShadow $padding="7px 24px">
            {careTeam.data.map((member, index) => (
              <MemberContainer key={member.surname}>
                <CareTeamMember member={member} />
                {index !== careTeam.data.length - 1 && <Divider $height="1px" $color="lightGrey" />}
              </MemberContainer>
            ))}
          </TabletWithShadow>
          {careTeam.button && (
            <Label
              label="See more"
              fontWeight={500}
              fontSize="s"
              lineHeight="22px"
              color="purple"
              margin="0"
              onClick={handleMoreData}
            />
          )}
        </InfoWrapper>
      ) : (
        <TabletWithoutData label="There will be recommends for you" icon="tests" />
      )}
    </DataWrapper>
  );
};

export default CareTeam;

