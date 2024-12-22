import styled from 'styled-components';

export const WrapperWithShadow = styled.div<{
  $flexDirection: string;
}>`
  display: flex;
  ${({ $flexDirection }) => `flex-direction: ${$flexDirection};`}

  padding: 24px;
  width: 100%;
  border-radius: 8px;
  margin-bottom: 16px;

  overflow: auto;

  ${({ theme }) => `
  box-shadow: -webkit-box-shadow: 5px 0px 72px 0px ${theme.colors.lightShadow};
              -moz-box-shadow: 5px 0px 72px 0px ${theme.colors.lightShadow};
               box-shadow: 5px 0px 72px 0px ${theme.colors.lightShadow};
  `}

  & > svg {
    flex-shrink: 0;
  }

  & input {
    max-width: 100%;
    width: 100%;
  }

  & > div > div:nth-child(2) {
    max-width: 100%;
  }

  #error {
    display: none;
  }
`;

export const InfoWrapper = styled.div<{
  $margin: string;
  $width?: string;
  $isAvailable?: boolean;
}>`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;

  cursor: pointer;

  ${({ $margin }) => `margin: ${$margin};`}
  ${({ $width }) => $width && `max-width: ${$width}`}
  width: 100%;

  & svg {
    ${({ $isAvailable, theme }) => !$isAvailable && `color: ${theme.colors.white};`}
  }
`;
