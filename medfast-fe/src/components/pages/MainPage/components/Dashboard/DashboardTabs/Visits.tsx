import React, { useEffect, useState } from 'react';

import { useUser } from '@/utils/UserContext';

import { Line } from '../components/styles';
import { DataSection, VisitRow, TabletWithoutData } from '../components';
import { Label, Loader } from '@/components/common';

import { GetVisits } from '@/api/GetVisits';

import { STATUS_COLORS } from '../components/Statuses';

const LABELS = {
  noUpcomingVisits: 'You have no upcoming visits Created an appointment for today',
  noPastVisits: 'This is where the history of your visits will be stored',
};

const ERROR_TEXT = {
  somethingWrong: 'Something went wrong! Please try again later',
};

const Visits = () => {
  const userAuth = useUser();
  const [visits, setVisits] = useState<{ upcomingVisits: VisitsType; pastVisits: VisitsType }>({
    upcomingVisits: [],
    pastVisits: [],
  });

  const [visitsToDisplay, setVisitsToDisplay] = useState({
    upcomingVisits: {
      data: visits.upcomingVisits.slice(0, 2),
      button: visits.upcomingVisits.length > 2,
    },
    pastVisits: {
      data: visits.pastVisits?.slice(0, 2),
      button: visits.pastVisits.length > 2,
    },
  });
  const [serverError, setServerError] = useState<'somethingWrong' | null>(null);
  const [isUpcomingLoading, setIsUpcomingLoading] = useState(false);
  const [isPastLoading, setIsPastLoading] = useState(false);

  const handleMoreData = (test: 'upcomingVisits' | 'pastVisits') => {
    setVisitsToDisplay({ ...visitsToDisplay, [test]: { data: visits[test], button: false } });
  };

  const handleUpcomingVisits = async () => {
    try {
      setIsUpcomingLoading(true);

      const upcomingVisitsResponse = await GetVisits(
        userAuth.userData?.accessToken || 'null',
        'UPCOMING',
      );

      setVisits({ ...visits, upcomingVisits: upcomingVisitsResponse.data });
    } catch (error) {
      setServerError('somethingWrong');
    }
    setIsUpcomingLoading(false);
  };

  const handlePastVisits = async () => {
    try {
      setIsPastLoading(true);

      const pastVisitsResponse = await GetVisits(userAuth.userData?.accessToken || 'null', 'PAST');

      setVisits({ ...visits, pastVisits: pastVisitsResponse.data });
    } catch (error) {
      setServerError('somethingWrong');
    }
    setIsPastLoading(false);
  };

  useEffect(() => {
    handleUpcomingVisits();
    handlePastVisits();
  }, []);

  return (
    <>
      <Line $height="10px" />
      <DataSection title="Upcoming visits">
        {isUpcomingLoading ? (
          <Loader />
        ) : visitsToDisplay.upcomingVisits.data.length > 0 ? (
          <>
            {visitsToDisplay.upcomingVisits.data?.map((data) => (
              <VisitRow key={data.id} data={data} />
            ))}
            {visitsToDisplay.upcomingVisits.button && (
              <Label
                label="See more"
                fontWeight={500}
                fontSize="s"
                lineHeight="22px"
                color="purple"
                margin="0"
                onClick={() => handleMoreData('upcomingVisits')}
              />
            )}
          </>
        ) : (
          <TabletWithoutData
            label={serverError ? ERROR_TEXT.somethingWrong : LABELS.noUpcomingVisits}
            buttonLabel={serverError ? null : '+ Make an appointment'}
            icon={serverError && 'doctorImage'}
          />
        )}
      </DataSection>
      <DataSection title="Past visits">
        {isPastLoading ? (
          <Loader />
        ) : visitsToDisplay.pastVisits.data.length > 0 ? (
          <>
            {visitsToDisplay.pastVisits.data?.map((data) => <VisitRow key={data.id} data={data} />)}
            {visitsToDisplay.pastVisits.button && (
              <Label
                label="Go to visits history"
                fontWeight={500}
                fontSize="s"
                lineHeight="22px"
                color="purple"
                margin="0"
                onClick={() => handleMoreData('pastVisits')}
              />
            )}
          </>
        ) : (
          <TabletWithoutData
            label={serverError ? ERROR_TEXT.somethingWrong : LABELS.noPastVisits}
            icon="doctorImage"
          />
        )}
      </DataSection>
    </>
  );
};

export default Visits;

export type Visit = {
  id: number;
  doctorsId: number;
  doctorsSpecialization: string;
  doctorsName: string;
  dateFrom: string;
  dateTo: string;
  location: string;
  status: keyof typeof STATUS_COLORS;
  type: string;
};

type VisitsType = Visit[] | [];

