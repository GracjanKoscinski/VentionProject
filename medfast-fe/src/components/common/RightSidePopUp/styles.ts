import styled from 'styled-components';

export const PopUpWrapper = styled.div<{
  $isOpen: boolean;
}>`
  display: flex;
  position: fixed;
  z-index: 100;
  top: 0;
  right: 0;
  left: 0;
  bottom: 0;

  transform: translate(${({ $isOpen }) => ($isOpen ? 0 : 100)}%);
  opacity: ${({ $isOpen }) => ($isOpen ? 1 : 0)};
  transition: 0.3s;

  width: 100%;
  height: 100vh;
`;

export const Wrapper = styled.div`
  width: 50%;
  height: 100vh;

  position: fixed;
  top: 0;
  right: 0;
  z-index: 200;
`;
