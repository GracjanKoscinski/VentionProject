import React, { useState } from 'react';

import { format, parse } from 'date-fns';

import { WrapperWithShadow } from '../styles';
import { Container, VisitInfoWrapper, BackButton } from './styles';
import { BackToWithArrow } from '@/components/common';
import { Button, Label, Divider, PopUpWithContent } from '@/components/common';
import { AppointmentInfoRow } from './components';

import Icon from '@/components/Icons';

import { useUser } from '@/utils/UserContext';

import { CreateOnlineAppointment } from '@/api/CreateOnlineAppointment';
import NewTestAppointmentInfo from './components/NewTestAppointmentInfo';
import TestTimeAndDateForm from './components/TestTimeAndDateForm';
import TestsForm from './components/TestsForm';
import TestLocationForm from './components/TestLocationForm';

type Form = 'test' | 'timeAndDate' | 'location';

type Props = {
  handleClose: (isOpen: boolean) => void;
  setServerResponse: (serverResponse: 'somethingWrong') => void;
};

const TestAppointment = ({ handleClose, setServerResponse }: Props) => {
  const userAuth = useUser();
  const days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
  const [openedForm, setOpenedForm] = useState<Form | null>(null);
  const [isPopUp, setIsPopUp] = useState<null | 'notAvailable' | 'cancel' | 'success' | 'error'>(
    null,
  );
  const [visitInfo, setVisitInfo] = useState<TestAppointment>({
    doctor: { name: null, speciality: null, imageUrl: null, id: null },
    type: 'Test appointment',
    test: { name: null, id: null },
    timeAndDate: { date: { day: null, month: null, year: null, dayOfWeek: null }, time: null },
    location: { id: null, hospital_name: null, street_address: null, house: null },
  });
  const [isAppointmentCreated, setIsAppointmentCreated] = useState(false);

  const forms = {
    timeAndDate: { component: TestTimeAndDateForm },
    test: { component: TestsForm },
    location: { component: TestLocationForm },
  };

  const CurrentBody = openedForm ? forms[openedForm].component : () => <></>;

  const isFilled =
    visitInfo.test.name !== null &&
    visitInfo.location.hospital_name !== null &&
    visitInfo.timeAndDate.date.day !== null &&
    visitInfo.timeAndDate.time !== null;

  const handleBack = () => {
    setOpenedForm(null);
  };

  const date =
    visitInfo.timeAndDate.date.year &&
    visitInfo.timeAndDate.date.month &&
    visitInfo.timeAndDate.date.day &&
    format(
      new Date(
        visitInfo.timeAndDate.date.year,
        visitInfo.timeAndDate.date.month,
        visitInfo.timeAndDate.date.day,
      ),
      'dd-MMM-yyyy',
    );

  const handleNewAppointment = async () => {
    const visitData = {
      doctorId: visitInfo.doctor.id || 0,
      testId: visitInfo.test.id || 0,
      locationId: visitInfo.location.id || 0,
      dateFrom: parse(
        `${date} ${visitInfo.timeAndDate.time}`,
        'dd-MMM-yyyy KK:mma',
        new Date(),
      ).toISOString(),
    };
    const token = userAuth.userData?.accessToken;

    try {
      //await CreateOnlineAppointment(visitData, token || '');

      setIsPopUp('success');
    } catch (error: any) {
      if (error.message === 409) {
        setIsPopUp('notAvailable');
      } else {
        setIsPopUp('error');
      }
    }
  };

  const handleServerError = (error: 'somethingWrong') => {
    setServerResponse(error);
    handleClose(false);
  };

  const handleBackButtonClick = () => {
    setIsPopUp('cancel');
  };

  const popUpData = {
    notAvailable: {
      title: 'Timeslot is not available',
      message:
        'The time you have selected is already booked or unavailable, please select an available option',
      confirmButton: 'Reschedule',
      cancelButton: 'Cancel',
      confirmMethod: () => {
        setIsPopUp(null);
        setIsAppointmentCreated(false);
      },
      cancelMethod: () => setIsPopUp('cancel'),
    },
    cancel: {
      title: 'Cancel an appointment?',
      message: 'Are you sure you want to cancel your appointment? Your data will not be saved',
      confirmButton: 'Yes, cancel',
      cancelButton: 'No, back',
      confirmMethod: () => {
        setIsPopUp(null);
        handleClose(false);
      },
      cancelMethod: () => {
        setIsPopUp('notAvailable');
      },
    },
    error: {
      children: <Icon type="newAppointmentError" />,
      title: 'Oops Failed!',
      message: 'Appointment failed. Please check your internet connection then try again.',
      confirmButton: 'Try again',
      cancelButton: 'Close',
      confirmMethod: () => setIsPopUp(null),
      cancelMethod: () => {
        setIsPopUp(null);
        handleClose(false);
      },
    },
    success: {
      children: <Icon type="newAppointmentCreated" />,
      title: 'Congratulations!',
      message:
        'Appointment successfully booked. You will receive a notification and the doctor you selected will contact you',
      cancelButton: 'Go to home page',
      cancelMethod: () => {
        handleClose(false);
      },
    },
  };

  return (
    <>
      {isPopUp && <PopUpWithContent {...popUpData[isPopUp]} />}
      <Container>
        {!openedForm ? (
          <>
            <VisitInfoWrapper>
              <BackToWithArrow label="Back" onClick={handleBackButtonClick} />
              <Label
                label="Test "
                fontWeight={700}
                fontSize="l"
                lineHeight="30px"
                color="darkGrey"
                margin="56px 0 16px"
              />
              <Divider $height="2px" $color="purple" />
              {isAppointmentCreated ? (
                <NewTestAppointmentInfo visitInfo={visitInfo} />
              ) : (
                <WrapperWithShadow $flexDirection="column">
                  <AppointmentInfoRow
                    icon="testsIcon"
                    title={visitInfo.test.name || 'Test'}
                    text={visitInfo.test.name || 'Type of test'}
                    onClick={() => setOpenedForm('test')}
                  />
                  <AppointmentInfoRow
                    icon="location"
                    title={visitInfo.location.hospital_name || 'Location'}
                    isAvailable={!!visitInfo.test.name}
                    text={
                      `${visitInfo.location.house ? visitInfo.location.house + ', ' : ''}${visitInfo.location.street_address || ''}` ||
                      "Test's location"
                    }
                    onClick={() => visitInfo.test.name && setOpenedForm('location')}
                  />
                  <AppointmentInfoRow
                    icon="timeAndDate"
                    title={
                      visitInfo.timeAndDate.date.day
                        ? `${date} | ${visitInfo.timeAndDate.time}`
                        : 'Time and Date'
                    }
                    isAvailable={!!visitInfo.location.hospital_name}
                    text={
                      visitInfo.timeAndDate.date.dayOfWeek
                        ? days[visitInfo.timeAndDate.date.dayOfWeek]
                        : 'Visit time'
                    }
                    isLast
                    onClick={() => visitInfo.location && setOpenedForm('timeAndDate')}
                  />
                </WrapperWithShadow>
              )}
            </VisitInfoWrapper>
            <Button
              label={isAppointmentCreated ? 'Book' : 'Create appointment'}
              buttonSize="l"
              disabled={!isFilled}
              onClick={
                isAppointmentCreated
                  ? handleNewAppointment
                  : () => {
                      setIsAppointmentCreated(true);
                    }
              }
            />
          </>
        ) : (
          <CurrentBody
            visitInfo={visitInfo}
            setVisitInfo={setVisitInfo}
            handleError={handleServerError}
            handleBack={handleBack}
          />
        )}
      </Container>
    </>
  );
};

export default TestAppointment;

export type TestAppointment = {
  doctor: {
    name: null | string;
    speciality: null | string[];
    imageUrl: null | string;
    id: null | number;
  };
  type: 'Test appointment';
  test: { name: string | null; id: number | null };
  timeAndDate: {
    date: {
      day: null | number;
      month: number | null;
      year: number | null;
      dayOfWeek: number | null;
    };
    time: null | string;
  };
  location: {
    id: number | null;
    hospital_name: string | null;
    street_address: string | null;
    house: string | null;
  };
};
