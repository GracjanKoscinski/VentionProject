import styled from 'styled-components';

export const Wrapper = styled.div<{
  $location: string;
}>`
  display: flex;
  flex-direction: row;
  align-items: center;
  ${({ $location }) => `justify-content: ${$location};`}

  width: 100%;
  margin: 12px 0 0;

  & div:first-child {
    margin-right: 8px;
  }

  & div:last-child {
    cursor: pointer;
  }
`;
