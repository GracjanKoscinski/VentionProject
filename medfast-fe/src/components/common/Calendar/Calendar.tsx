import React, { useState } from 'react';
import {
  eachYearOfInterval,
  eachDayOfInterval,
  getMonth,
  getYear,
  getDay,
  getDate,
  setMonth,
  setYear,
  setDate,
  startOfMonth,
  endOfMonth,
  isSameDay,
} from 'date-fns';

import Label from '@/components/common/Label/Label';
import Select from '@/components/common/Select/Select';
import {
  Wrapper,
  SelectWrapper,
  WeekDaysWrapper,
  WeekDay,
  MonthWrapper,
  DayElement,
  ArrowWrapper,
  Arrow,
  MonthPickerWrapper,
} from './styles';
import { Divider } from '@/components/common/Divider/styles';

import { TimeSlots } from '@/components/pages/MainPage/components/NewAppointment/components/AppointmentTabs/components/TimeAndDateForm';

type Props = {
  initialDate?: Date | null;
  timeSlots?: TimeSlots;
  onSubmit: (date: Date) => void;
};

const Calendar = ({ initialDate, timeSlots, onSubmit }: Props) => {
  const week = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN'];
  const monthOptions = [
    'January',
    'February',
    'March',
    'April',
    'May',
    'June',
    'July',
    'August',
    'September',
    'October',
    'November',
    'December',
  ];
  const [selectedDay, setSelectedDay] = useState(initialDate || new Date());
  const [viewDate, setViewDate] = useState(initialDate || new Date());

  const monthDates = eachDayOfInterval({
    start: startOfMonth(viewDate),
    end: endOfMonth(viewDate),
  });
  const monthDays = monthDates.map((date) => ({ date, dayOfMonth: getDate(new Date(date)) }));

  const years = eachYearOfInterval({
    start: new Date(1900, 1, 6),
    end: new Date(2050, 7, 10),
  });
  const yearsToShow = years.map((year) => getYear(year).toString());

  const handleClick = (monthDay: number, event: React.MouseEvent) => {
    event.stopPropagation();
    const dateToShow = setDate(viewDate, monthDay);
    setSelectedDay(dateToShow);
    onSubmit(dateToShow);
  };

  const handleNextMonth = () => {
    setViewDate(setMonth(viewDate, monthOptions.indexOf(monthOptions[getMonth(viewDate)]) + 1));
  };

  const handlePreviousMonth = () => {
    getMonth(viewDate) !== getMonth(new Date()) &&
      setViewDate(setMonth(viewDate, monthOptions.indexOf(monthOptions[getMonth(viewDate)]) - 1));
  };

  const handleAvailableDates = (day: number) => {
    return timeSlots && timeSlots[day].length > 0;
  };

  return (
    <Wrapper>
      {!timeSlots ? (
        <SelectWrapper>
          <Select
            name="month"
            options={monthOptions}
            placeholder={monthOptions[getMonth(viewDate)]}
            handleChange={(option: string) => {
              setViewDate(setMonth(viewDate, monthOptions.indexOf(option)));
            }}
          />
          <Select
            name="year"
            options={yearsToShow}
            placeholder={getYear(viewDate).toString()}
            handleChange={(option: string, event: React.MouseEvent) => {
              event.stopPropagation();
              setViewDate(setYear(viewDate, +option));
            }}
          />
        </SelectWrapper>
      ) : (
        <>
          <MonthPickerWrapper>
            <Label
              label={monthOptions[getMonth(viewDate)]}
              fontWeight={500}
              fontSize="s"
              lineHeight="22px"
              color="darkGrey"
              margin="0"
            />
            <ArrowWrapper>
              <Arrow
                $rotate="135"
                $disabled={getMonth(viewDate) === getMonth(new Date())}
                onClick={handlePreviousMonth}
              />
              <Arrow $rotate="315" onClick={handleNextMonth} />
            </ArrowWrapper>
          </MonthPickerWrapper>
          <Divider $height="1px" $color="lightGrey" />
        </>
      )}
      <WeekDaysWrapper>
        {week.map((day) => (
          <WeekDay key={day}>{day}</WeekDay>
        ))}
      </WeekDaysWrapper>
      <MonthWrapper $firstDay={getDay(startOfMonth(viewDate))}>
        {monthDays.map(({ date, dayOfMonth }) => (
          <DayElement
            key={dayOfMonth}
            $isToday={isSameDay(date, selectedDay)}
            $isAvailable={handleAvailableDates(getDate(date))}
            onClick={(event: React.MouseEvent) => handleClick(dayOfMonth, event)}
          >
            {dayOfMonth}
          </DayElement>
        ))}
      </MonthWrapper>
    </Wrapper>
  );
};

export default Calendar;

