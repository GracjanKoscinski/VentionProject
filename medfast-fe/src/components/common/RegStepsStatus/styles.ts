import styled from 'styled-components';

export const Wrapper = styled.div<{
  $isDone: boolean;
  $isNext: boolean;
}>`
  display: flex;
  align-items: center;
  justify-content: center;

  width: 32px;
  height: 32px;
  border-radius: 50%;

  font-weight: 500;
  font-size: 16px;
  line-height: 16px;

  ${({ $isNext, theme }) =>
    $isNext
      ? `border: 2px solid ${theme.colors.grey}; color: ${theme.colors.grey};`
      : `border: 2px solid ${theme.colors.purple}; color: ${theme.colors.purple};`}

  ${({ $isDone, theme }) =>
    $isDone
      ? `background-color: ${theme.colors.purple};`
      : `background-color: ${theme.colors.white};`}
  

    @media (max-width: 720px) {
    margin: 0 5px;
  }
`;

export const StatusChain = styled.div`
  display: flex;
  flex-direction: row;
`;

export const RegStepStatusWrapper = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;

  & svg:first-child {
    position: static;
  }
`;

export const Line = styled.div<{
  $isDone: boolean;
}>`
  background-color: ${({ $isDone, theme }) =>
    $isDone ? theme.colors.purple : theme.colors.lightGrey};

  width: 72px;
  height: 3px;
  margin: 0 8px;
  border-radius: 3px;

  @media (max-width: 930px) {
    width: 30px;
  }

  @media (max-width: 720px) {
    display: none;
  }
`;
