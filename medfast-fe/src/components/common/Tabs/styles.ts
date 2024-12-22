import styled from 'styled-components';

export type TabsContainerSize = 'max';

export const TabsContainer = styled.div<{
  $size?: TabsContainerSize;
}>`
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;

  max-width: ${({ $size }) => $size || '420px'};
  width: fit-content;
  overflow: hidden;
  border-radius: 4px;
  margin-bottom: 19px;
  ${({ theme }) => `border: 1px solid ${theme.colors.lightGrey};`}
`;

export const TabWrapper = styled.div<{
  $isCurrentTab: boolean;
}>`
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;

  padding: 8px 24px;
  cursor: pointer;

  ${({ $isCurrentTab, theme }) =>
    $isCurrentTab
      ? `background-color: ${theme.colors.purple}; color: ${theme.colors.white};`
      : `background-color: ${theme.colors.white}; color: ${theme.colors.grey};`}
`;

