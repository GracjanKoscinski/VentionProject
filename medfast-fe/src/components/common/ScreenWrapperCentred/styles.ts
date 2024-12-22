import styled from 'styled-components';

export const ScreenWrapperCentred = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;

  width: 100vw;
  height: 100vh;
  padding: 25px;

  background-color: ${({ theme }) => theme.colors.white};
`;
