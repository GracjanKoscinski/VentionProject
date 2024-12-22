import styled from 'styled-components';

export const Wrapper = styled.div`
  width: 100%;
  height: 100px;

  display: flex;
  justify-content: flex-end;

  & #error {
    display: none;
  }

  & button {
    padding: 18px 22px;
    margin-left: 16px;
  }

  & input {
    margin: 0 8px 0 0;
  }
`;

export const ContentWrapper = styled.div`
  width: 100%;

  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
`;

export const UserWrapper = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;

  cursor: pointer;

  @media (max-width: 1130px) {
    & div:nth-child(2) {
      display: none;
    }
  }
`;

export const NotificationWrapper = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;

  width: 112px;
  margin: 0 24px 0 10px;

  & div {
    cursor: pointer;
  }

  @media (max-width: 975px) {
    margin-right: 10px;
  }

  @media (max-width: 960px) {
    display: none;
  }
`;

export const Side = styled.div`
  display: flex;
  flex-direction: row;

  @media (max-width: 1300px) {
    & input {
      max-width: 260px;
    }

    & button {
      width: 154px;
      padding: 8px 12px;
    }
  }

  @media (max-width: 840px) {
    & button {
      width: 56px;
      margin-right: 10px;
      font-size: 35px;
    }
  }
`;
