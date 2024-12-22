import styled from 'styled-components';

export const LabelElement = styled.div<{
  $fontWeight: number;
  $fontSize: string;
  $lineHeight: string;
  $color: string;
  $margin: string;
}>`
  ${({ $fontWeight, $fontSize, $lineHeight, $color, $margin, theme }) =>
    `font-size: ${theme.fontSizes[$fontSize]};
      font-weight: ${$fontWeight};
      line-height: ${$lineHeight};
      color: ${theme.colors[$color]};
      margin: ${$margin};
      `}
`;
