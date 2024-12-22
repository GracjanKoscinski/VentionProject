import styled from 'styled-components';

export const Wrapper = styled.div`
  position: relative;

  & label {
    display: none;
  }

  & input {
    max-width: 320px;
    border: none;
    background-color: #f3f5ff;
    padding: 16px 16px 16px 50px;
  }

  & svg {
    position: absolute;
    top: 16px;
    left: 16px;
  }
`;

export const OptionsWrapper = styled.div`
  ${({ theme }) =>
    `background-color: ${theme.colors.white};
      box-shadow:  -webkit-box-shadow:  0px 4.86px 22.7px 0px ${theme.colors.formShadow};
      -moz-box-shadow:  0px 4.86px 22.7px 0px #130A2E08;
      box-shadow:  0px 4.86px 22.7px 0px #130A2E08;`}

  min-width: 150px;
  max-width: 320px;
  width: 100%;
  max-height: 430px;
  z-index: 100;
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

export const OptionWrapper = styled.div`
  max-width: 356px;
  width: 100%;
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

  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: flex-start;

  & div:first-child {
    margin: 0;
  }
`;

export const LabelWrapper = styled.div`
  display: flex;
  flex-direction: column;

  margin-left: 15px;
`;

export const OptionGroupWrapper = styled.div`
  display: flex;
  flex-direction: column;
`;

