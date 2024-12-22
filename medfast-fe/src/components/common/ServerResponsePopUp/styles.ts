import styled from 'styled-components';

export const Wrapper = styled.div<{ $serverResponse: boolean }>`
  position: fixed;
  top: 132px;
  right: 32px;

  transform: translate(${({ $serverResponse }) => ($serverResponse ? 0 : 100)}%);
  opacity: ${({ $serverResponse }) => ($serverResponse ? 1 : 0)};
  transition: 0.3s;
`;
