import styled from 'styled-components';

export const Wrapper = styled.div`
  width: 50%;
  height: 100%;
  padding: 24px;

  display: flex;
  flex-direction: column;
  align-items: center;
`;

export const ResetCodeWrapper = styled.div`
  max-width: 372px;
  width: 100%;

  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 17px;

  & input {
    max-width: 84px;
    height: 84px;

    font-weight: 500;
    font-size: 48px;
    line-height: 41px;
    text-align: center;
  }

  & input::-webkit-outer-spin-button,
  input::-webkit-inner-spin-button {
    -webkit-appearance: none;
    margin: 0;
  }

  & #error {
    display: none;
  }
`;

export const ResendWrapper = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  margin-bottom: 17px;
  cursor: pointer;
  min-height: 24px;

  S & svg {
    margin-left: 4px;
  }
`;
