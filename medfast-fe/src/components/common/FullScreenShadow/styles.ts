import styled from 'styled-components';

export const FullScreenShadow = styled.div`
  width: 100vw;
  height: 100vh;

  position: absolute;
  z-index: 10;

  background-color: ${({ theme }) => theme.colors.shadow};

  display: flex;
  align-items: center;
  justify-content: center;
`;
