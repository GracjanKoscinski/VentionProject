import styled from 'styled-components';

export const RoundImageBackground = styled.div<{
  $backgroundColor: string;
  $backgroundImage?: string | null;
  $borderColor: string;
  $margin?: string;
}>`
  width: 52px;
  height: 52px;
  border-radius: 50%;

  display: flex;
  align-items: center;
  justify-content: center;

  ${({ theme, $backgroundColor, $backgroundImage }) =>
    $backgroundImage
      ? `background-image: url(${$backgroundImage});
      background-repeat: no-repeat;
      background-size: cover;
      background-position: center;`
      : $backgroundColor !== 'success'
        ? `background-color: ${theme.colors[$backgroundColor]};`
        : `background-color: ${theme.colors[$backgroundColor].backgroundColor};`}

  ${({ theme, $borderColor }) => `
    border: 2px solid ${theme.colors[$borderColor]};
    font-size: ${theme.fontSizes.s};
    color: ${theme.colors.purple};
    `}

  font-weight: 600;
  line-height: 20px;
  margin: auto;
  position: relative;
  flex-shrink: 0;

  & div {
    top: -5px;
    right: 0;
  }
`;
