import styled from 'styled-components';

export const ButtonElement = styled.button<{
  $disabled: boolean;
  $primary: boolean;
  $buttonWidth: string;
  $borderRadius: string;
}>`
  ${({
    $buttonWidth,
    theme,
    $borderRadius,
  }) => `width: ${theme.buttonWidth[$buttonWidth]}; font-size: ${theme.fontSizes.s}; border-radius: ${theme.buttonShape[$borderRadius]};
`};

  ${({ $primary, theme }) =>
    $primary
      ? `background: ${theme.button.primary.background};
      border: ${theme.button.primary.border};
      color: ${theme.button.primary.color};`
      : `background: ${theme.button.default.background};
      border: ${theme.button.default.border};
      color: ${theme.button.default.color};`};

  ${({ $disabled, theme }) =>
    $disabled
      ? `background: ${theme.button.disabled.background};
      border: ${theme.button.disabled.border};
      color: ${theme.button.disabled.color};
            cursor: default;`
      : `&:active {
            background:${theme.button.active.background}; 
            color: ${theme.button.active.color};}`}

  height: 56px;
  padding: 8px 32px;
  cursor: pointer;
  line-height: 20px;
  font-weight: 700;
`;
