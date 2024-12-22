import React, { useState } from 'react';

import { AppointmentTypesWrapper } from './styles';
import { Label, Divider, RightSidePopUp } from '@/components/common';
import AppointmentType from './components/AppointmentType';

import OnlineAppointment from './components/AppointmentTabs/OnlineAppointment';
import OnSiteAppointment from './components/AppointmentTabs/OnSiteAppointment';
import TestAppointment from './components/AppointmentTabs/TestAppointment';

import { IconType } from '@/components/Icons';

type Props = {
  isNewAppointmentOpen: boolean;
  setIsNewAppointmentOpen: (isNewAppointmentOpen: boolean) => void;
  setServerResponse: (serverResponse: 'somethingWrong') => void;
};

type CurrentTab = 'Visit' | 'Online consultation' | 'Test';

const NewAppointment = ({
  isNewAppointmentOpen,
  setIsNewAppointmentOpen,
  setServerResponse,
}: Props) => {
  const [currentTab, setCurrentTab] = useState<CurrentTab | null>(null);

  const appointmentTypes: { icon: IconType; title: CurrentTab; text: string }[] = [
    {
      icon: 'onSiteVisit',
      title: 'Visit',
      text: 'Includes a personal visit to a doctor',
    },
    {
      icon: 'onlineVisit',
      title: 'Online consultation',
      text: 'Remote consultation via internet',
    },
    { icon: 'testVisit', title: 'Test', text: 'Signing up for certain tests' },
  ];

  const tabsToDisplay = {
    Visit: { component: OnSiteAppointment },
    'Online consultation': { component: OnlineAppointment },
    Test: { component: TestAppointment },
  };

  const CurrentBody = currentTab ? tabsToDisplay[currentTab].component : () => <></>;

  const handleClose = (isOpen: boolean) => {
    setCurrentTab(null);
    setIsNewAppointmentOpen(isOpen);
  };

  return (
    <RightSidePopUp isOpen={isNewAppointmentOpen} handleClose={handleClose}>
      {!currentTab ? (
        <>
          <Label
            label="Choose type of appointment"
            fontWeight={700}
            fontSize="l"
            lineHeight="30px"
            color="darkGrey"
            margin="56px 0 16px"
          />
          <Divider $height="2px" $color="purple" />
          <AppointmentTypesWrapper>
            {appointmentTypes.map((type) => (
              <AppointmentType
                key={type.icon}
                icon={type.icon}
                title={type.title}
                text={type.text}
                onClick={() => setCurrentTab(type.title)}
              />
            ))}
          </AppointmentTypesWrapper>
        </>
      ) : (
        <CurrentBody handleClose={handleClose} setServerResponse={setServerResponse} />
      )}
    </RightSidePopUp>
  );
};

export default NewAppointment;
