import styled from 'styled-components';

export const TabletWithShadow = styled.div<{
  $shape?: string;
  $padding?: string;
  $width?: string;
  $height?: string;
  $flex?: string;
  $margin?: string;
}>`
  display: flex;
  flex-direction: column;
  padding: 40px;
  border-radius: 10px;
  width: 100%;
  transition: flex 0.3s;

  ${({ theme }) =>
    `box-shadow: -webkit-box-shadow: 5px 0px 31px 0px ${theme.colors.lightBlue};
      -moz-box-shadow: 5px 0px 31px 0px ${theme.colors.lightBlue};
      box-shadow: 5px 0px 31px 0px ${theme.colors.lightBlue};
      background-color: ${theme.colors.white};
      `}

  ${({ $shape }) => $shape && `border-radius:${$shape};`}
  ${({ $padding }) => $padding && `padding: ${$padding};`}
  ${({ $width }) => $width && `max-width: ${$width};`}
  ${({ $height }) => $height && `height: ${$height}; `}
  ${({ $flex }) => $flex && `flex: ${$flex}; `}
  ${({ $margin }) => $margin && `margin: ${$margin}; `}
`;
