import React, { useState, useEffect } from 'react';

import { getDate, getMonth, getYear, getDay } from 'date-fns';

import {
  FormWrapper,
  TitleWrapper,
  AvailableTimeSlotWrapper,
  CalendarWrapper,
  AvailableTimeSlots,
} from './styles';
import { WrapperWithShadow } from '../../styles';
import {
  BackToWithArrow,
  RoundImageBackground,
  Calendar,
  Label,
  Divider,
  Button,
} from '@/components/common';

import Icon from '@/components/Icons';

import { GetTimeSlots } from '@/api/GetTimeSlots';

import { OnlineVisit } from '../OnlineAppointment';

import availableSlotsData from '@/mocks/timeSlots.json';
import { TestAppointment } from '../TestAppointment';
import { GetTimeSlotsForTestForTest } from '@/api/GetTimeSlotsForTest';
import { useUser } from '@/utils/UserContext';

type Props = {
  visitInfo: TestAppointment;
  setVisitInfo: (visitInfo: TestAppointment) => void;
  handleError: (serverResponse: 'somethingWrong') => void;
  handleBack: () => void;
};

const TestTimeAndDateForm = ({ visitInfo, setVisitInfo, handleError, handleBack }: Props) => {
  const today = new Date();
  const userAuth = useUser();
  const [chosenDate, setChosenDate] = useState<TimeAndDate>({
    date: {
      day: getDate(today),
      month: getMonth(today),
      year: getYear(today),
      dayOfWeek: getDay(today),
    },
    time: null,
  });
  const [activeTime, setActiveTime] = useState<string | null>(null);
  const [timeSlots, setTimeSlots] = useState<TimeSlots>(availableSlotsData.slots);
  const isFilled = chosenDate.date.day !== null && chosenDate.time !== null;

  const handleClick = (time: string) => {
    setChosenDate({ ...chosenDate, time });
    setActiveTime(time);
  };

  const handleSubmit = () => {
    setVisitInfo({
      ...visitInfo,
      timeAndDate: { date: chosenDate.date, time: chosenDate.time },
    });
    handleBack();
  };

  const handleCalendar = (selectedDate: Date) => {
    const dayOfWeek = getDay(selectedDate);
    const day = getDate(selectedDate);
    const month = getMonth(selectedDate);
    const year = getYear(selectedDate);

    setChosenDate({
      ...chosenDate,
      date: {
        day,
        month,
        year,
        dayOfWeek,
      },
    });
  };

  const handleTimeSlots = async () => {
    try {
      /*const response = GetTimeSlotsForTestForTest(
        userAuth.userData?.accessToken || '',
        visitInfo.test.id || 0,
        chosenDate.date.month,
        chosenDate.date.year,
      );
      */
      const response = availableSlotsData.slots;
      setTimeSlots(response);
    } catch (error: any) {
      handleError('somethingWrong');
    }
  };

  useEffect(() => {
    handleTimeSlots();
  }, []);

  return (
    <>
      <FormWrapper>
        <BackToWithArrow label="Back" onClick={handleBack} />
        <TitleWrapper>
          <RoundImageBackground $backgroundColor="lightBlue" $borderColor="lightBlue">
            <Icon type="timeAndDate" />
          </RoundImageBackground>
          <Label
            label="Time and date"
            fontWeight={700}
            fontSize="l"
            lineHeight="24px"
            color="darkGrey"
            margin="0 0 0 24px"
          />
        </TitleWrapper>
        <Divider $height="2px" $color="purple" />
        <Label
          label="Please select a date and time from the available options"
          fontWeight={400}
          fontSize="s"
          lineHeight="22px"
          color="darkGrey"
          margin="24px 0 16px"
        />
        <WrapperWithShadow $flexDirection="column">
          <CalendarWrapper>
            <Calendar
              timeSlots={timeSlots}
              onSubmit={(date: Date) => {
                handleCalendar(date);
              }}
            />
            <AvailableTimeSlots>
              {timeSlots[chosenDate.date.day].map((slot) => (
                <AvailableTimeSlotWrapper
                  key={slot}
                  $isActive={activeTime === slot}
                  onClick={() => handleClick(slot)}
                >
                  {slot}
                </AvailableTimeSlotWrapper>
              ))}
            </AvailableTimeSlots>
          </CalendarWrapper>
        </WrapperWithShadow>
      </FormWrapper>
      <Button
        label="Confirm time and date"
        buttonSize="l"
        disabled={!isFilled}
        onClick={handleSubmit}
      />
    </>
  );
};

export default TestTimeAndDateForm;

type TimeAndDate = {
  date: DateType;
  time: string | null;
};

type DateType = {
  day: number;
  month: number;
  year: number;
  dayOfWeek: number;
};

export type TimeSlots = Record<number, string[]>;

