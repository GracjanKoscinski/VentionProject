import styled from 'styled-components';

export const HeaderWrapper = styled.div`
  width: 100%;
  height: 100px;

  display: flex;
  justify-content: flex-end;

  & button {
    padding: 18px 22px;
    margin-left: 16px;
  }

  & input {
    margin: 0 8px 0 0;
  }
`;

export const HeaderContentWrapper = styled.div`
  width: 100%;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
`;

export const UserWrapper = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  @media (max-width: 1130px) {
    & div:nth-child(2) {
      display: none;
    }
  }
`;


export const Side = styled.div`
  display: flex;
  flex-direction: row;

  @media (max-width: 1300px) {
    & input {
      max-width: 260px;
    }

    & button {
      width: 154px;
      padding: 8px 12px;
    }
  }

  @media (max-width: 840px) {
    & button {
      width: 56px;
      margin-right: 10px;
      font-size: 35px;
    }
  }
`;

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

export const Input = styled.input`
  width: 100%;
  max-width: 320px;
  padding: 10px;
  margin: 10px 0;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #f3f5ff;
  font-size: 16px;

  &:focus {
    outline: none;
    border-color: #aaa;
  }
`  
export const ListOfOptions = styled.div`
  label {
    margin-left: auto;
    padding-right: 10px;
    font-size: ${({ theme }) => theme.fontSizes.xs};
    color: #333;
    text-align: right;
    
  }

  option {
    margin-right: 10px;
    font-size: ${({ theme }) => theme.fontSizes.xs};
    color: #333;
  }

  select {
    padding: 10px;
    font-size: ${({ theme }) => theme.fontSizes.xs};
    border: 1px solid #ccc;
    border-radius: 4px;
    background-color: ${({ theme }) => theme.colors.white};
    cursor: pointer;
    &:hover {
      border-color: ${({ theme }) => theme.colors.lightGrey};
    }
    &:focus {
      outline: none;
      border-color: ${({ theme }) => theme.colors.lightGrey};
    }
  }`
;