import React from 'react';
import { format } from 'date-fns';

import { Label, Divider, RoundImageBackground } from '@/components/common';
import { WrapperWithShadow } from '../../styles';
import { TopInfo, BottomInfo, LeftSide, RightSide, AppointmentInfoWrapper } from './styles';

import { OnlineVisit } from '../OnlineAppointment';

import Image from '@/mocks/DoctorAvatarTest.png';
import { formatDateToShow } from './utils/dateUtils';

type Props = {
  visitInfo: OnlineVisit;
};

const NewAppointmentInfo = ({ visitInfo }: Props) => {
  const nameArray = visitInfo.doctor.name?.split(' ') || [];
  const doctorInitials =
    nameArray.length > 0 && nameArray[0][0] + nameArray[nameArray.length - 1][0];

  const handleDateToShow = () => {
    const { year, month, day } = visitInfo.timeAndDate.date;
    const time = visitInfo.timeAndDate.time;

    return formatDateToShow({ year, month, day, time });
  };

  return (
    <>
      <WrapperWithShadow $flexDirection="column">
        <TopInfo>
          <RoundImageBackground
            $backgroundColor="lightBlue"
            $borderColor="lightBlue"
            $backgroundImage={Image || visitInfo.doctor.imageUrl}
          >
            {!visitInfo.doctor.imageUrl && doctorInitials}
          </RoundImageBackground>
          <Label
            label={`Dr. ${visitInfo.doctor.name}`}
            fontWeight={600}
            fontSize="m"
            lineHeight="22px"
            color="darkGrey"
            margin="15px 0 24px"
          />
          <Divider $height="2px" $color="lightGrey" />
          <Label
            label={visitInfo.doctor.speciality?.join(', ') || ''}
            fontWeight={400}
            fontSize="s"
            lineHeight="22px"
            color="darkGrey"
            margin="24px 0 0"
          />
        </TopInfo>
      </WrapperWithShadow>
      <WrapperWithShadow $flexDirection="column">
        <BottomInfo>
          <Label
            label="General information"
            fontWeight={600}
            fontSize="m"
            lineHeight="22px"
            color="darkGrey"
            margin="12px 0 25px"
          />
          <AppointmentInfoWrapper>
            <LeftSide>
              <Label
                label="Type"
                fontWeight={500}
                fontSize="s"
                lineHeight="22px"
                color="darkGrey"
                margin="0 0 12px"
              />
              <Label
                label="Date & Time"
                fontWeight={500}
                fontSize="s"
                lineHeight="22px"
                color="darkGrey"
                margin="0 0 12px"
              />
              <Label
                label="Service"
                fontWeight={500}
                fontSize="s"
                lineHeight="22px"
                color="darkGrey"
                margin="0 0 12px"
              />
              <Label
                label="Duration"
                fontWeight={500}
                fontSize="s"
                lineHeight="22px"
                color="darkGrey"
                margin="0 0 12px"
              />
            </LeftSide>
            <RightSide>
              <Label
                label={visitInfo.type}
                fontWeight={400}
                fontSize="s"
                lineHeight="22px"
                color="darkGrey"
                margin="0 0 12px"
              />
              <Label
                label={handleDateToShow()}
                fontWeight={400}
                fontSize="s"
                lineHeight="22px"
                color="darkGrey"
                margin="0 0 12px"
              />
              <Label
                label={visitInfo.service.service || ''}
                fontWeight={400}
                fontSize="s"
                lineHeight="22px"
                color="darkGrey"
                margin="0 0 12px"
              />
              <Label
                label={visitInfo.service.duration || ''}
                fontWeight={400}
                fontSize="s"
                lineHeight="22px"
                color="darkGrey"
                margin="0 0 12px"
              />
            </RightSide>
          </AppointmentInfoWrapper>
        </BottomInfo>
      </WrapperWithShadow>
    </>
  );
};

export default NewAppointmentInfo;

