import styled from 'styled-components';

export const CurrentPasswordWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: center;

  & #error {
    display: none;
  }

  & svg {
    top: 5px;
    bottom: 0;
  }

  & #highlightText {
    cursor: pointer;
  }
`;

export const HeaderWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;

  width: 100%;

  margin-bottom: 40px;
`;

export const Wrapper = styled.div`
  display: flex;
  justify-content: flex-start;
  align-items: center;
  flex-direction: column;
      overflow-y: scroll;
    -ms-overflow-style: none;
  scrollbar-width: none;


  max-width: 1500px;
  width: 100%;
  height: 100vh;
  padding: 0 25px;
  margin-top: 32px;

  background-color: ${({ theme }) => theme.colors.white};
`;

export const NoPermanentPasswordWrapper = styled.div`
  text-align: center;
`;
