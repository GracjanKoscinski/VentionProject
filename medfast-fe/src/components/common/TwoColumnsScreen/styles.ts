import styled from 'styled-components';

export const TwoColumnsScreen = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: row;

  width: 100vw;
  height: 100vh;

  background-color: ${({ theme }) => theme.colors.white};
`;
