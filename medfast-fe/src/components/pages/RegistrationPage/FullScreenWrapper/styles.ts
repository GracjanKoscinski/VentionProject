import styled from 'styled-components';

export const FullScreenWrapper = styled.div`
  display: flex;
  justify-content: flex-start;
  align-items: center;
  flex-direction: column;

  max-width: 2000px;
  width: 100%;
  height: 100vh;
  padding: 25px;
  margin: 0 auto;

  background-color: ${({ theme }) => theme.colors.white};
`;
