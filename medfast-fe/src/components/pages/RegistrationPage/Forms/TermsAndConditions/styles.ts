import styled from 'styled-components';

export const Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-start;
  width: 100%;
`;

export const LabelWrapper = styled.div`
  width: 100%;
  height: 52px;

  ${({ theme }) => `background-color: ${theme.colors.lightBlue};`}

  border-radius: 8px;
  padding: 16px;
`;

export const TextWrapper = styled.div`
  & div {
    font-weight: 400;
    line-height: 22px;
    margin: 0 0 16px;

    ${({ theme }) =>
      `font-size: ${theme.fontSizes.s};
      color: ${theme.colors.darkGrey};`}
  }
`;
