import styled from 'styled-components';

export const FullMainScreenWrapper = styled.div<{
  $padding?: string;
}>`
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  flex-direction: row;

  position: relative;

  width: 100vw;
  height: 100vh;
  padding: 0;

  background-color: ${({ theme }) => theme.colors.white};
`;

export const Wrapper = styled.div`
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  flex-direction: column;

  width: 100%;
  height: 100vh;
`;

export const Content = styled.div`
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  flex-direction: column;

  max-width: 1500px;
  width: 100%;
  margin: 0 auto;
  height: 100vh;
  overflow: hidden;
`;
