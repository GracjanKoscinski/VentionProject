import React, { useEffect, useRef, useState } from 'react';
import { format, isValid } from 'date-fns';

import Calendar from './Calendar';
import Input from '../Input/Input';
import { CalendarWrapper } from './styles';

import Icon from '@/components/Icons';
import { DataType } from '@/components/pages/RegistrationPage/Forms/FormsProps';

type Props<T> = {
  error: string | null;
  userData: T;
  setUserData: (userData: T) => void;
};

const CalendarElement = <T extends DataType>({ error, userData, setUserData }: Props<T>) => {
  const [isCalendarOpen, setIsCalendarOpen] = useState(false);
  const [manualValue, setManualValue] = useState<string>('');
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (ref.current && !ref.current.contains(event.target as Node)) {
        setIsCalendarOpen(false);
      }
    };
    document.addEventListener('click', handleClickOutside);

    return () => document.removeEventListener('click', handleClickOutside);
  }, []);

  const handleClick = (event: React.MouseEvent) => {
    event.stopPropagation();
    setIsCalendarOpen(true);
  };

  const isInputCorrect = error ? error : '';

  return (
    <CalendarWrapper onClick={handleClick} ref={ref}>
      <Input
        isInvalid={isInputCorrect}
        name="birthday"
        type="text"
        placeholder="MM/DD/YYYY"
        value={
          manualValue
            ? manualValue
            : userData.birthday
              ? format(userData.birthday, 'MM/dd/yyyy')
              : ''
        }
        label="Date of birth"
        onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
          userData && setUserData({ ...userData, birthday: null });
          const date = event.target.value;
          setManualValue(date);
        }}
        onBlur={() => {
          if (isValid(new Date(manualValue))) {
            setUserData({ ...userData, birthday: new Date(manualValue) });
          }
          setManualValue('');
        }}
      />
      <Icon type="calendarIcon" />
      {isCalendarOpen && (
        <Calendar
          initialDate={userData.birthday ? userData.birthday : null}
          onSubmit={(date: Date) => {
            setUserData({ ...userData, birthday: date });
            setIsCalendarOpen(false);
          }}
        />
      )}
    </CalendarWrapper>
  );
};

export default CalendarElement;
