import styled from 'styled-components';

export const Wrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: row;
  width: 100%;
  height: 45px;
  position: relative;
  margin-bottom: 32px;

  & svg {
    position: absolute;
    left: 0;
    top: 0;
  }
`;
