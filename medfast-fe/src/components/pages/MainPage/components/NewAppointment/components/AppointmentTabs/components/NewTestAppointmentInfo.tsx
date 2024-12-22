import React from 'react';
import { format } from 'date-fns';

import { Label } from '@/components/common';
import { WrapperWithShadow } from '../../styles';
import { BottomInfo, LeftSide, RightSide, AppointmentInfoWrapper } from './styles';

import { TestAppointment } from '../TestAppointment';
import { formatDateToShow } from './utils/dateUtils';

type Props = {
  visitInfo: TestAppointment;
};

const NewTestAppointmentInfo = ({ visitInfo }: Props) => {
  const handleDateToShow = () => {
    const { year, month, day } = visitInfo.timeAndDate.date;
    const time = visitInfo.timeAndDate.time;

    return formatDateToShow({ year, month, day, time });
  };

  return (
    <>
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
                label="Test"
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
              <Label
                label="Location"
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
                label={visitInfo.test.name || ''}
                fontWeight={400}
                fontSize="s"
                lineHeight="22px"
                color="darkGrey"
                margin="0 0 12px"
              />
              <Label
                label={`${visitInfo.location.street_address}, ${visitInfo.location.house}, ${visitInfo.location.hospital_name}`}
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

export default NewTestAppointmentInfo;
