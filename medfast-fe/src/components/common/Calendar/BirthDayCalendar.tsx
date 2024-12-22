import React, { useEffect, useRef, useState } from 'react';
import { format, isValid } from 'date-fns';

import Calendar from './Calendar';
import Input from '../Input/Input';
import { CalendarWrapper } from './styles';

import Icon from '../../Icons';
import { UserInfoType } from '../../pages/MainPage/components/Profile/components/Interface/UserInfoType';
import { PersonalInfoType } from '../../pages/MainPage/components/Profile/components/Interface/PersonalInfoType';

type Props = {
  error: string | null;
  userInfo: UserInfoType;
  setPersonalInfo: (personalInfo: PersonalInfoType) => void;
};

const BirthDayCalendar = ({ error, userInfo, setPersonalInfo }: Props) => {
  const [isCalendarOpen, setIsCalendarOpen] = useState(false);
  const [manualValue, setManualValue] = useState<string>('');
  const ref = useRef<HTMLDivElement>(null);
  const personalInfo = userInfo.personalInfo;
  const handleClick = (event: React.MouseEvent) => {
    event.stopPropagation();
    setIsCalendarOpen(true);
  };
  const handleBlur = () => {
    if (isValid(new Date(manualValue))) {
      setPersonalInfo({
        ...personalInfo,
        birthDate: new Date(manualValue),
      });
    }
    setManualValue('');
  };

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (ref.current && !ref.current.contains(event.target as Node)) {
        setIsCalendarOpen(false);
      }
    };
    document.addEventListener('click', handleClickOutside);

    return () => document.removeEventListener('click', handleClickOutside);
  }, []);

  return (
    <CalendarWrapper onClick={handleClick} ref={ref}>
      <Input
        isInvalid={error || ''}
        name="birthday"
        type="text"
        placeholder="MM/DD/YYYY"
        value={
          manualValue
            ? manualValue
            : personalInfo?.birthDate
              ? format(new Date(personalInfo.birthDate), 'MM/dd/yyyy')
              : ''
        }
        label="Date of birth"
        onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
          const date = event.target.value;
          setManualValue(date);
        }}
        onBlur={handleBlur}
      />
      <Icon type="calendarIcon" />
      {isCalendarOpen && (
        <Calendar
          initialDate={personalInfo?.birthDate ? new Date(personalInfo.birthDate) : null}
          onSubmit={(date: Date) => {
            setPersonalInfo({
              ...personalInfo,
              birthDate: date,
            });
            setIsCalendarOpen(false);
          }}
        />
      )}
    </CalendarWrapper>
  );
};

export default BirthDayCalendar;
