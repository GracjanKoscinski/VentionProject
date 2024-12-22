import styled from 'styled-components';

export const TitleWrapper = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;

  margin-bottom: 16px;

  & > div:first-child {
    margin: 0;
  }
`;

export const Wrapper = styled.div<{
  $isActive: boolean;
}>`
  width: 100%;
  padding: 16px;
  cursor: pointer;
  margin-bottom: 12px;
  ${({ theme, $isActive }) =>
    $isActive
      ? `border: 1px solid ${theme.colors.purple}; 
        background-color: ${theme.colors.lightBlue};
        
        & #image {
            border-color: ${theme.colors.purple};
            background-color: ${theme.colors.lightBlue};
        }`
      : `border: 1px solid ${theme.colors.grey};`}
  border-radius: 10px;

  display: flex;
  justify-content: space-between;

  & > div:nth-child(2) {
    min-width: 90px;
    max-width: 100px;
    width: 100%;
    text-align: end;
  }

  &:hover {
    ${({ theme }) => `border-color: ${theme.colors.purple};
                      background-color: ${theme.colors.lightBlue};

    & #image {
    border-color: ${theme.colors.purple};
    background-color: ${theme.colors.lightBlue};
    }
  `}
  }
`;

export const ServicesWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-start;

  margin-top: 20px;

  overflow-y: scroll;
  -ms-overflow-style: none;
  scrollbar-width: none;
`;

export const FormWrapper = styled.div`
  display: flex;
  flex-direction: column;
  height: calc(100% - 56px);
  width: 100%;
`;

export const Dot = styled.div`
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-left: 8px;
  flex-shrink: 0;
  ${({ theme }) => `background-color: ${theme.colors.purple};`}
`;

export const InfoWrapper = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: flex-start;

  width: 100%;
  margin-top: 8px;

  & #highlightText {
    height: 20px;
    overflow: hidden;
  }
`;

export const Container = styled.div<{
  $isLast: boolean;
}>`
  display: flex;
  flex-direction: row;

  ${({ $isLast }) => ($isLast ? `margin-bottom: 0` : `margin-bottom: 24px`)};

  & > div:first-child {
    margin: 0;
  }
`;

export const DoctorWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
`;

export const DoctorsWrapper = styled.div`
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
`;

export const AvailableTimeSlotWrapper = styled.div<{
  $isActive: boolean;
}>`
  display: flex;
  align-items: center;
  justify-content: center;

  padding: 9px 15px;
  min-width: 103px;
  border-radius: 40px;
  cursor: pointer;

  line-height: 22px;
  font-weight: 400;

  ${({ theme }) =>
    `border: 1px solid ${theme.colors.lightGrey}; 
    font-size: ${theme.fontSizes.s};`}

  ${({ theme, $isActive }) =>
    $isActive
      ? `color:${theme.colors.white};  
          background-color: ${theme.colors.purple};`
      : `color:${theme.colors.darkGrey};  
          background-color: ${theme.colors.white};`}



  &:hover {
    ${({ theme }) => `
          color:${theme.colors.white};
          background-color: ${theme.colors.purple};`}
`;

export const CalendarWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;

  & > div:first-child {
    position: static;
  }
`;

export const AvailableTimeSlots = styled.div`
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;

  margin-top: 40px;
`;

export const InputWrapper = styled.div`
  position: relative;
  width: 100%;

  & > div:first-child {
    max-width: 100%;
  }

  & input {
    background-color: #f3f5ff;
    border: none;
    padding: 16px 16px 16px 50px;
  }

  & > svg {
    position: absolute;
    top: 24px;
    left: 16px;
  }
`;

export const NoResultsWrapper = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;

  padding: 200px 10px;

  width: 100%;
`;

export const NoResultsTitleWrapper = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;

  text-align: center;

  max-width: 300px;
  width: 100%;
`;

export const TopInfo = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  width: 100%;

  padding: 0;
  margin: 0;

  & > div:first-child {
    width: 80px;
    height: 80px;
  }
`;

export const BottomInfo = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: flex-start;

  width: 100%;
`;

export const LeftSide = styled.div`
  display: flex;
  flex: 1;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;

  & > div {
    min-height: 44px;
  }
`;

export const RightSide = styled.div`
  display: flex;
  flex: 3;
  flex-direction: column;
  justify-content: center;
  align-items: flex-end;

  & > div {
    min-height: 44px;
  }
`;

export const AppointmentInfoWrapper = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: flex-start;

  width: 100%;
`;
