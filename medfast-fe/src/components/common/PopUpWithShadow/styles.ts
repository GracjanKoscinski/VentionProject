import styled from 'styled-components';

export const ButtonWrapper = styled.div`
  display: flex;
  flex-direction: row;
  gap: 12px;

  & > button {
    padding: 8px 12px;
  }
`;

export const Wrapper = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 100;

  text-align: center;
`;

export const ChildrenWrapper = styled.div`
  width: 100%;

  display: flex;
  align-items: center;
`;
