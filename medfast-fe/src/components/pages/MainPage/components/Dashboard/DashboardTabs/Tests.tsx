import React, { useState, useEffect } from 'react';

import { useUser } from '@/utils/UserContext';

import { Line } from '../components/styles';
import { DataSection, TestRow, TabletWithoutData } from '../components';
import { Label, Loader, ServerResponsePopUp } from '@/components/common';
import { GetTests } from '@/api/GetTests';
import { GetTestResult } from '@/api/GetTestResult';

const LABELS = {
  noData: 'You have no tests at the moment',
  noUpcomingTests: 'You have no upcoming tests at the moment',
  noPastTests: 'You have no past tests at the moment',
};

const ERROR_TEXT = {
  somethingWrong: 'Something went wrong! Please try again later',
};

type Test = {
  id: number;
  testName: string;
  doctorsName: string | null;
  dateOfTest: string;
  hasPdfResult: boolean;
};

type TestsType = Test[];

type TestsToDisplayType = {
  data: TestsType;
  button: boolean;
};

const Tests = () => {
  const userAuth = useUser();
  const [tests, setTests] = useState<{ upcomingTests: TestsType; pastTests: TestsType }>({
    upcomingTests: [],
    pastTests: [],
  });

  const [testsToDisplay, setTestsToDisplay] = useState<{
    upcomingTests: TestsToDisplayType;
    pastTests: TestsToDisplayType;
  }>({
    upcomingTests: {
      data: [],
      button: false,
    },
    pastTests: { data: [], button: false },
  });

  const [serverError, setServerError] = useState<'somethingWrong' | null>(null);
  const [isUpcomingLoading, setIsUpcomingLoading] = useState(true);
  const [isPastLoading, setIsPastLoading] = useState(true);

  const [upcomingAmount, setUpcomingAmount] = useState(2);
  const [pastAmount, setPastAmount] = useState(2);

  const handleMoreData = (testType: 'upcomingTests' | 'pastTests') => {
    if (testType === 'upcomingTests') {
      setUpcomingAmount((prevAmount) => prevAmount + 2);
    } else {
      setPastAmount((prevAmount) => prevAmount + 2);
    }
  };

  const handleDownload = async (testId: number) => {
    try {
      await GetTestResult(userAuth.userData?.accessToken || 'null', testId);
    } catch (error) {
      setServerError('somethingWrong');
    }
  };

  const handleUpcomingTests = async () => {
    try {
      const upcomingTestsResponse = await GetTests(
        userAuth.userData?.accessToken || 'null',
        'UPCOMING',
        upcomingAmount,
      );

      const checkIfThereIsMore = await GetTests(
        userAuth.userData?.accessToken || 'null',
        'UPCOMING',
        pastAmount + 1,
      );

      setTests((prevTests) => {
        const updatedTests = { ...prevTests, upcomingTests: upcomingTestsResponse.data };

        setTestsToDisplay((prevDisplay) => ({
          ...prevDisplay,
          upcomingTests: {
            data: upcomingTestsResponse.data,
            button: checkIfThereIsMore.data.length > upcomingAmount,
          },
        }));

        return updatedTests;
      });
    } catch (error) {
      setServerError('somethingWrong');
    }
    setIsUpcomingLoading(false);
  };

  const handlePastTests = async () => {
    try {
      const pastTestsResponse = await GetTests(
        userAuth.userData?.accessToken || 'null',
        'PAST',
        pastAmount,
      );

      const checkIfThereIsMore = await GetTests(
        userAuth.userData?.accessToken || 'null',
        'PAST',
        pastAmount + 1,
      );

      setTests((prevTests) => {
        const updatedTests = { ...prevTests, pastTests: pastTestsResponse.data };

        setTestsToDisplay((prevDisplay) => ({
          ...prevDisplay,
          pastTests: {
            data: pastTestsResponse.data,
            button: checkIfThereIsMore.data.length > pastAmount,
          },
        }));

        return updatedTests;
      });
    } catch (error) {
      setServerError('somethingWrong');
    }
    setIsPastLoading(false);
  };

  useEffect(() => {
    handleUpcomingTests();
    handlePastTests();
  }, []);

  useEffect(() => {
    if (upcomingAmount > 2) {
      handleUpcomingTests();
    }
  }, [upcomingAmount]);

  useEffect(() => {
    if (pastAmount > 2) {
      handlePastTests();
    }
  }, [pastAmount]);

  const isAnyTests = tests.upcomingTests.length >= 1 || tests.pastTests.length >= 1;

  return isPastLoading || isUpcomingLoading ? (
    <Loader />
  ) : !isAnyTests ? (
    <TabletWithoutData
      label={serverError ? ERROR_TEXT.somethingWrong : LABELS.noData}
      icon="doctorImage"
    />
  ) : (
    <>
      <ServerResponsePopUp serverResponse={serverError} onClick={() => setServerError(null)} />
      <Line $height="10px" />
      <DataSection title="Upcoming tests">
        {isUpcomingLoading ? (
          <Loader />
        ) : testsToDisplay.upcomingTests.data.length > 0 ? (
          <>
            {testsToDisplay.upcomingTests.data.map((data) => (
              <TestRow key={data.id} data={data} />
            ))}
            {testsToDisplay.upcomingTests.button && (
              <Label
                label="See more"
                fontWeight={500}
                fontSize="s"
                lineHeight="22px"
                color="purple"
                margin="0"
                onClick={() => handleMoreData('upcomingTests')}
              />
            )}
          </>
        ) : (
          <TabletWithoutData
            label={serverError ? ERROR_TEXT.somethingWrong : LABELS.noUpcomingTests}
            icon="doctorImage"
          />
        )}
      </DataSection>
      <DataSection title="Past tests">
        {isPastLoading ? (
          <Loader />
        ) : testsToDisplay.pastTests.data.length > 0 ? (
          <>
            {testsToDisplay.pastTests.data.map((data) => (
              <TestRow key={data.id} data={data} onClick={() => handleDownload(data.id)} />
            ))}
            {testsToDisplay.pastTests.button && (
              <Label
                label="See more"
                fontWeight={500}
                fontSize="s"
                lineHeight="22px"
                color="purple"
                margin="0"
                onClick={() => handleMoreData('pastTests')}
              />
            )}
          </>
        ) : (
          <TabletWithoutData
            label={serverError ? ERROR_TEXT.somethingWrong : LABELS.noPastTests}
            icon="doctorImage"
          />
        )}
      </DataSection>
    </>
  );
};

export default Tests;

