import React from 'react';

import { isValid, intervalToDuration } from 'date-fns';

export const REG_EXP = {
  noSpecOrNum: /^[A-Za-z]+$/,
  latin: /^[A-Za-z0-9 ,\-\'/\\]*$/,
  alphanumeric: /^[A-Za-z0-9]*$/,
  noNum: /^[A-Za-z \-']*$/,
  onlyNum: /^[0-9]*$/,
  email: /^(?=.{10,50}$)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
  phone: /^\+\d \(\d{3}\) \d{3} \d{4}$/,
  password: /^(?=.*[A-Z])(?=.*[a-z])(?=.*[!\"#$%&'()*+,-./:;<=>?@[\\\]^_`{|}~])(?=.*\d).+$/,
};

const UserDataValidation = () => {
  const requiredField = (data: string) => {
    return !(data !== '') && 'required';
  };

  const length = (data: string, start: number, end: number) => {
    const length = data.length;
    const min = length >= start;
    const max = length <= end;
    return (!min || !max) && 'length';
  };

  const allowedChar = (data: string, validChars: keyof typeof REG_EXP) => {
    return !REG_EXP[validChars].test(data) && 'allowedChars';
  };

  const dateFormat = (data: string) => {
    return !isValid(new Date(data)) && 'dateFormat';
  };

  const ageLimit = (data: string) => {
    const age = intervalToDuration({
      start: data,
      end: new Date(),
    });

    const min = (age.years || 0) < 18;
    const max = (age.years || 111) > 110;

    return (min || max) && 'ageLimit';
  };

  return { requiredField, length, allowedChar, dateFormat, ageLimit };
};

export default UserDataValidation;
