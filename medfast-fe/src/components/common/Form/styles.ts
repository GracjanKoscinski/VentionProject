import styled from 'styled-components';

export const FormElement = styled.form<{
  $isBordered: boolean;
}>`
  ${({ $isBordered, theme }) =>
    $isBordered
      ? `padding: 40px;
        border-radius: 24px;
        box-shadow:  -webkit-box-shadow: 5px 0px 31px 0px ${theme.colors.lightBlue};
        -moz-box-shadow: 5px 0px 31px 0px ${theme.colors.lightBlue};
        box-shadow: 5px 0px 31px 0px ${theme.colors.lightBlue};`
      : `text-align: center;
      
      & div {
        max-width: 370px;
      }`}
`;
