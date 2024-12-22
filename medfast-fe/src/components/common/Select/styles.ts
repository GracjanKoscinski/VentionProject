import styled from 'styled-components';

export const Arrow = styled.div<{
  $isFocused: boolean;
}>`
  ${({ $isFocused, theme }) =>
    $isFocused
      ? `border: solid ${theme.colors.darkBlue};
      transform: rotate(225deg);
      -webkit-transform: rotate(225deg);
       top: 27px;`
      : `border: solid ${theme.colors.grey};
      transform: rotate(45deg);
      -webkit-transform: rotate(45deg);
       top: 24px;`}
  border-width: 0 3px 3px 0;
  display: inline-block;
  padding: 3px;
  position: absolute;
  right: 20px;
  border-radius: 2px;
  cursor: pointer;
`;

export const Wrapper = styled.div`
  position: relative;

  & label {
    display: none;
  }
`;

export const OptionsWrapper = styled.div`
  ${({ theme }) =>
    `background-color: ${theme.colors.white};
      box-shadow:  -webkit-box-shadow:  0px 4.86px 22.7px 0px ${theme.colors.formShadow};
      -moz-box-shadow:  0px 4.86px 22.7px 0px #130A2E08;
      box-shadow:  0px 4.86px 22.7px 0px #130A2E08;`}

  min-width: 150px;
  width: 100%;
  max-height: 230px;
  padding: 8px;
  border-radius: 8px;
  overflow-y: scroll;

  position: absolute;
  top: 64px;
  left: 0;

  -ms-overflow-style: none;
  scrollbar-width: none;

  &::-webkit-scrollbar {
    display: none;
  }
`;

export const Option = styled.div`
  max-width: 356px;
  width: 100%;
  height: 38px;
  border-radius: 8px;
  padding: 8px 16px;
  cursor: pointer;

  ${({ theme }) =>
    `background-color: ${theme.colors.white};
        color: ${theme.colors.darkGrey};
        font-size: ${theme.fontSizes.s};
        color: ${theme.colors.darkGrey};
    
    &:hover{
        background-color: ${theme.colors.lightBlue};
        color: ${theme.colors.darkBlue};
    }
  `}

  font-weight: 500;
  line-height: 22px;
`;

export const HighlightedOptionsWrapper = styled.div`
  width: 100%;
  ${({ theme }) => `background-color: ${theme.colors.blue};
  
  & div {
    background-color: ${theme.colors.blue};}
  }`}
`;
