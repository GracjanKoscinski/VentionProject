import styled from 'styled-components';

export const Wrapper = styled.div<{ $response: string }>`
  width: 372px;
  height: 76px;
  border-radius: 8px;
  padding: 16px 12px;
  position: relative;
  margin-bottom: 24px;

  display: flex;
  justify-content: center;
  align-items: center;

  ${({ theme, $response }) => ` background-color: ${theme.colors[$response].backgroundColor};`}

  & div:last-child {
    max-width: 260px;
    width: 100%;
  }

  & svg:nth-child(2) {
    position: absolute;
    top: 24px;
    left: 20px;
  }

  & > div:nth-child(3) {
    position: absolute;
    top: 22px;
    right: 15px;

    cursor: pointer;
  }
`;

export const Line = styled.div<{ $response: string }>`
  width: 4px;
  height: 76px;
  border-radius: 8px;
  position: absolute;
  top: 0;
  left: 0;

  ${({ theme, $response }) => `background-color: ${theme.colors[$response].color};`}
`;

export const ResponseMessage = styled.div`
  font-weight: 400;
  line-height: 22px;
  text-align: start;
  ${({ theme }) => `font-size: ${theme.fontSizes.s};`}
`;

export const CrossWrapper = styled.div``;
