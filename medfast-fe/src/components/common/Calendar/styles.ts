import styled from 'styled-components';

export const Wrapper = styled.div`
  width: 345px;
  min-height: 368px;
  padding: 15px;

  position: absolute;
  bottom: 70px;
  left: 330px;
  border-radius: 10px;

  ${({ theme }) =>
    `box-shadow: -webkit-box-shadow: 5px 0px 31px 0px ${theme.colors.lightBlue};
      -moz-box-shadow: 5px 0px 31px 0px ${theme.colors.lightBlue};
      box-shadow: 5px 0px 31px 0px ${theme.colors.lightBlue};
      background-color: ${theme.colors.white};`}

  & input {
    width: 150px;
    height: 56px;
    border-radius: 6px;
    border-color: transparent;
    margin: 0;
    color: ${({ theme }) => theme.colors.darkGrey};

    &::placeholder {
      color: ${({ theme }) => theme.colors.darkGrey};
      font-weight: 500;
      line-height: 22px;
    }
  }

  @media (max-width: 1050px) {
    width: 370px;
    position: static;
    margin-bottom: 30px;
  }
`;

export const SelectWrapper = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: space-between;
`;

export const DayElement = styled.div<{
  $isToday: boolean;
  $isAvailable?: boolean;
}>`
  display: flex;
  align-items: center;
  justify-content: center;
  width: 45px;
  height: 32px;
  font-weight: 500;
  font-size: 15px;
  line-height: 24px;
  cursor: pointer;

  ${({ $isAvailable, theme }) =>
    $isAvailable
      ? `color: ${theme.colors.darkGrey}; 
  `
      : `color: ${theme.colors.lightGrey};`}

  ${({ $isToday, theme }) =>
    $isToday
      ? `color: ${theme.colors.white};
        background-color: ${theme.colors.purple};
        border-radius: 16px;
  `
      : `background-color: ${theme.colors.white};`}

  margin: 10px 0 8px;
`;

export const WeekDay = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  width: 45px;
  height: 18px;
  font-weight: 600;
  font-size: 13px;
  line-height: 18px;

  ${({ theme }) => `color: ${theme.colors.grey};
  background-color:${theme.colors.white};`}
`;

export const WeekDaysWrapper = styled.div`
  display: flex;
  flex-direction: row;
  margin: 10px 0;
`;

export const MonthWrapper = styled.div<{
  $firstDay: number;
}>`
  display: grid;
  grid-template-columns: repeat(7, 1fr);

  & div:first-child {
    grid-column: ${({ $firstDay }) => $firstDay};
  }
`;

export const CalendarWrapper = styled.div`
  margin: 0;
  position: relative;
  cursor: pointer;

  & input {
    cursor: pointer;
  }

  & svg {
    position: absolute;
    top: 44px;
    right: 16px;
    cursor: pointer;
    pointer-events: none;
  }
`;

export const ArrowWrapper = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;

  width: 65px;
  height: 40px;
  padding: 15px;
`;

export const Arrow = styled.div<{
  $rotate: string;
  $disabled?: boolean;
}>`
  ${({ $disabled, theme }) =>
    $disabled
      ? `border: solid ${theme.colors.lightGrey};`
      : `border: solid ${theme.colors.darkGrey};
        cursor: pointer;
      `}
  ${({ $rotate }) => `transform: rotate(${$rotate}deg);
      -webkit-transform: rotate(${$rotate}deg);`}
  border-width: 0 3px 3px 0;
  display: inline-block;
  padding: 3px;
  border-radius: 2px;
`;

export const MonthPickerWrapper = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;

  margin: bottom: 10px;
`;
