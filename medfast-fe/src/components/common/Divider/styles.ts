import styled from 'styled-components';

export const Divider = styled.div<{
  $height: string;
  $color: string;
}>`
  width: 100%;

  ${({ theme, $height, $color }) =>
    `background-color: ${theme.colors[$color]};
    height: ${$height};`}
`;
