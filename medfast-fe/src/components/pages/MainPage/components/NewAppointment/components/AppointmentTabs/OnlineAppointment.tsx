import React, { useState } from 'react';

import { format, parse } from 'date-fns';

import { WrapperWithShadow } from '../styles';
import { Container, VisitInfoWrapper } from './styles';
import { Button, Label, Divider, PopUpWithContent } from '@/components/common';
import { BackToWithArrow } from '@/components/common';
import { AppointmentInfoRow, ServicesForm, DoctorsForm, TimeAndDateForm } from './components';
import NewAppointmentInfo from './components/NewAppointmentInfo';

import Icon from '@/components/Icons';

import { useUser } from '@/utils/UserContext';

import { CreateOnlineAppointment } from '@/api/CreateOnlineAppointment';

type Form = 'doctor' | 'service' | 'timeAndDate';

type Props = {
  handleClose: (isOpen: boolean) => void;
  setServerResponse: (serverResponse: 'somethingWrong') => void;
};

const OnlineAppointment = ({ handleClose, setServerResponse }: Props) => {
  const userAuth = useUser();
  const days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
  const [openedForm, setOpenedForm] = useState<Form | null>(null);
  const [isPopUp, setIsPopUp] = useState<null | 'notAvailable' | 'cancel' | 'success' | 'error'>(
    null,
  );
  const [visitInfo, setVisitInfo] = useState<OnlineVisit>({
    doctor: { name: null, speciality: null, imageUrl: null, id: null },
    type: 'Online consultation',
    service: { service: null, id: null, duration: null },
    timeAndDate: { date: { day: null, month: null, year: null, dayOfWeek: null }, time: null },
  });
  const [isAppointmentCreated, setIsAppointmentCreated] = useState(false);

  const forms = {
    service: { component: ServicesForm },
    doctor: { component: DoctorsForm },
    timeAndDate: { component: TimeAndDateForm },
  };

  const CurrentBody = openedForm ? forms[openedForm].component : () => <></>;

  const isFilled =
    visitInfo.doctor.name !== null &&
    visitInfo.service.service !== null &&
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
    if (!isAppointmentCreated) {
      setIsAppointmentCreated(true);

      return;
    }
    const visitData = {
      doctorId: visitInfo.doctor.id || 0,
      serviceId: visitInfo.service.id || 0,
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
                label="Online consultation"
                fontWeight={700}
                fontSize="l"
                lineHeight="30px"
                color="darkGrey"
                margin="56px 0 16px"
              />
              <Divider $height="2px" $color="purple" />
              {isAppointmentCreated ? (
                <NewAppointmentInfo visitInfo={visitInfo} />
              ) : (
                <WrapperWithShadow $flexDirection="column">
                  <AppointmentInfoRow
                    icon="stethoscope"
                    title={visitInfo.service.service || 'Service'}
                    text={visitInfo.service.service || 'Type of service'}
                    onClick={() => setOpenedForm('service')}
                  />
                  <AppointmentInfoRow
                    icon="person"
                    imageUrl={visitInfo.doctor.imageUrl}
                    title={visitInfo.doctor.name || 'Doctor'}
                    isAvailable={!!visitInfo.service.service}
                    text={visitInfo.doctor.speciality?.join(', ') || "Doctor's speciality"}
                    onClick={() => visitInfo.service.service && setOpenedForm('doctor')}
                  />
                  <AppointmentInfoRow
                    icon="timeAndDate"
                    title={
                      visitInfo.timeAndDate.date.day
                        ? `${date} | ${visitInfo.timeAndDate.time}`
                        : 'Time and Date'
                    }
                    isAvailable={!!visitInfo.doctor.name}
                    text={
                      visitInfo.timeAndDate.date.dayOfWeek
                        ? days[visitInfo.timeAndDate.date.dayOfWeek]
                        : 'Visit time'
                    }
                    isLast
                    onClick={() =>
                      visitInfo.doctor.name && visitInfo.service && setOpenedForm('timeAndDate')
                    }
                  />
                </WrapperWithShadow>
              )}
            </VisitInfoWrapper>
            <Button
              label={isAppointmentCreated ? 'Book' : 'Create appointment'}
              buttonSize="l"
              disabled={!isFilled}
              onClick={handleNewAppointment}
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
export default OnlineAppointment;

export type OnlineVisit = {
  doctor: {
    name: null | string;
    speciality: null | string[];
    imageUrl: null | string;
    id: null | number;
  };
  type: 'Online consultation';
  service: { service: string | null; id: number | null; duration: null | string };
  timeAndDate: {
    date: {
      day: null | number;
      month: number | null;
      year: number | null;
      dayOfWeek: number | null;
    };
    time: null | string;
  };
};

