import styled from 'styled-components';

export const Wrapper = styled.div`
  width: 100%;
  height: 100%;

  padding: 32px;

  overflow-y: scroll;

  -ms-overflow-style: none;
  scrollbar-width: none;

  &::-webkit-scrollbar {
    display: none;
  }
`;

export const BottomData = styled.div`
  display: flex;
  flex-direction: row;
  align-items: baseline;
  justify-content: space-between;
  gap: 20px;

  margin-top: 32px;

  & > div {
    flex: 1;
  }

  @media (max-width: 1150px) {
    flex-direction: column;
    align-items: center;
    justify-content: center;

    & > div {
      width: 100%;
    }
  }
`;
