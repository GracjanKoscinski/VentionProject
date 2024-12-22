import styled from 'styled-components';

export const Wrapper = styled.div<{
  $right: string;
}>`
  position: relative;

  & #highlightText {
    cursor: pointer;
    position: absolute;
    bottom: 12px;

    ${({ theme, $right }) => `border-bottom: 1px solid ${theme.colors.purple}; right: ${$right};`}
  }
`;
