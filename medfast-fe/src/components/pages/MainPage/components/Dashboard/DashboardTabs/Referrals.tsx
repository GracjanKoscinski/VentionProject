import React, { useEffect, useState } from 'react';

import { DataSection, ReferralRow, TabletWithoutData } from '../components';
import { Label } from '@/components/common';
import { Line } from '../components/styles';

import { STATUS_COLORS } from '../components/Statuses';

import { GetReferrals } from '@/api/GetReferrals';
import { useUser } from '@/utils/UserContext';

type ReferralSectionParam = 'UPCOMING' | 'PAST';
type LoadReferralsParam = 'FIRST' | 'REMAINING';

const Referrals = () => {
  const userAuth = useUser();

  const [referralsToDisplay, setReferralsToDisplay] = useState({
    upcoming: [] as Referral[],
    past: [] as Referral[],
    buttonUpcoming: false,
    buttonPast: false,
  });

  const loadUpcomingReferrals = (param: LoadReferralsParam) => {
    GetReferrals(userAuth.userData?.accessToken || '', param, 'UPCOMING').then((res) => {
      setReferralsToDisplay({
        ...referralsToDisplay,
        upcoming: res.data,
      });
    });
  };

  const loadPastReferrals = (param: LoadReferralsParam) => {
    GetReferrals(userAuth.userData?.accessToken || '', param, 'PAST').then((res) => {
      setReferralsToDisplay({
        ...referralsToDisplay,
        past: res.data,
      });
    });
  };

  const resetReferrals = (param: ReferralSectionParam) => {
    if (param === 'UPCOMING')
      setReferralsToDisplay({
        ...referralsToDisplay,
        upcoming: [],

        // TODO: getting info from backend if there are more referrals
        buttonUpcoming: false,
      });
    if (param === 'PAST')
      setReferralsToDisplay({
        ...referralsToDisplay,
        past: [],

        // TODO: getting info from backend if there are more referrals
        buttonPast: false,
      });
  };

  const handleMoreData = (param: ReferralSectionParam) => {
    if (param === 'PAST')
      GetReferrals(userAuth.userData?.accessToken || '', 'REMAINING', 'PAST').then((res) => {
        setReferralsToDisplay({
          ...referralsToDisplay,
          past: referralsToDisplay.past.concat(res.data),
          buttonPast: false,
        });
      });
    if (param === 'UPCOMING')
      GetReferrals(userAuth.userData?.accessToken || '', 'REMAINING', 'PAST').then((res) => {
        setReferralsToDisplay({
          ...referralsToDisplay,
          past: referralsToDisplay.upcoming.concat(res.data),
          buttonUpcoming: false,
        });
      });
  };

  return (
    <>
      <Line $height="10px" />
      <DataSection title="Upcoming referrals" onOpen={() => loadUpcomingReferrals('FIRST')}>
        {referralsToDisplay.upcoming.length > 0 ? (
          <>
            {referralsToDisplay.upcoming
              ?.sort(
                (a, b) => new Date(b.dateOfIssue).getTime() - new Date(a.dateOfIssue).getTime(),
              )
              .map((data) => <ReferralRow key={data.id} data={data} />)}
            {referralsToDisplay.buttonUpcoming && (
              <Label
                label="See more"
                fontWeight={500}
                fontSize="s"
                lineHeight="22px"
                color="purple"
                margin="0"
                onClick={() => handleMoreData('UPCOMING')}
              />
            )}
          </>
        ) : (
          <TabletWithoutData
            label="You have no upcoming referrals at the moment"
            icon="doctorImage"
          />
        )}
      </DataSection>
      <DataSection title="Past referrals" onOpen={() => loadPastReferrals('FIRST')}>
        {referralsToDisplay.past.length > 0 ? (
          <>
            {referralsToDisplay.past
              ?.sort(
                (a, b) => new Date(b.dateOfIssue).getTime() - new Date(a.dateOfIssue).getTime(),
              )
              .map((data) => <ReferralRow key={data.id} data={data} />)}
            {referralsToDisplay.buttonPast && (
              <Label
                label="See more"
                fontWeight={500}
                fontSize="s"
                lineHeight="22px"
                color="purple"
                margin="0"
                onClick={() => handleMoreData('PAST')}
              />
            )}
          </>
        ) : (
          <TabletWithoutData label="You have no past referrals at the moment" icon="doctorImage" />
        )}
      </DataSection>
    </>
  );
};

export default Referrals;

export type Referral = {
  id: number;
  appointmentId: number;
  name: string;
  issuedBy: string;
  dateOfIssue: string;
  expirationDate: string;
  specialization: string;
  appointmentStatus: keyof typeof STATUS_COLORS;
};

