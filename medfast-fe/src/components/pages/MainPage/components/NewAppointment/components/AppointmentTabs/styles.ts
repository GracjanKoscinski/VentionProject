import styled from 'styled-components';

export const Container = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: space-between;
  overflow-y: scroll;
  -ms-overflow-style: none;
  scrollbar-width: none;

  height: 100%;

  & button {
    width: 100%;
  }
`;

export const VisitInfoWrapper = styled.div`
  width: 100%;

  & > div:nth-child(3) {
    margin-top: 24px;
  }
`;

export const BackButton = styled.div`
  display: flex;
`;
