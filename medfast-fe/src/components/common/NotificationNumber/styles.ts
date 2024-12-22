import styled from 'styled-components';

export const NotificationNumber = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  position: absolute;

  width: 20px;
  height: 20px;
  border-radius: 50%;

  ${({ theme }) =>
    `background-color: ${theme.colors.error.color};
    color: ${theme.colors.white};
    font-size: ${theme.fontSizes.xxs};
    font-weight: 700;
    line-height: 15px;`}
`;
