import styled from 'styled-components';

export const ContentWrapper = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: flex-start;

  height: 100%;
`;

export const IconWrapper = styled.div`
  display: flex;
  justify-content: flex-start;
  align-items: center;

  margin: 0 0 50px 31px;
`;

export const TabsWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: space-between;

  height: 390px;
  width: 100%;

  & > div:first-child {
    transition: 0.3s;
  }

  @media (max-width: 960px) {
    height: 526px;
  }
`;

export const OtherWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: space-between;

  height: 156px;
  width: 100%;

  & > div:first-child {
    transition: 0.3s;
  }
`;

export const Wrapper = styled.div`
  display: flex;

  position: relative;
  z-index: 100;

  max-width: 110px;
  width: 100%;
  height: 100vh;
  margin-right: 32px;
  transition: 0.3s;
`;

export const TabWrapper = styled.div<{
  $isCurrentTab?: boolean;
  $isSideBarOpen: boolean;
}>`
  overflow-x: hidden;
  cursor: pointer;
  height: 78px;
  width: 100%;
  ${({ $isCurrentTab, theme }) =>
    $isCurrentTab
      ? `border-left: 4px solid ${theme.colors.yellow};`
      : `border-left: 4px solid transparent;`}

  display: flex;
  align-items: center;
  justify-content: flex-start;

  & svg {
    margin-left: 36px;
    flex: 0 0 30px;

    ${({ $isCurrentTab, theme }) =>
      $isCurrentTab ? `color:${theme.colors.purple};` : `color:${theme.colors.grey};`}
  }

  & > div {
    white-space: nowrap;
    transition: 0.3s;

    ${({ $isSideBarOpen }) => ($isSideBarOpen ? `opacity: 1;` : `opacity: 0;`)}
  }

  & > div:first-child {
    opacity: 1;
  }
`;

export const Wrap = styled.div`
  position: relative;

  & div {
    top: -4px;
    right: -10px;
  }
`;
