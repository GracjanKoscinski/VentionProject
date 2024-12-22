import styled from 'styled-components';

export const Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  max-width: 372px;
  width: 100%;

  margin: 0;
`;

export const InputElement = styled.input<{
  $isInvalid: boolean;
  $isDisabled?: boolean;
}>`
  ${({ $isInvalid, theme }) =>
    $isInvalid
      ? `color:  ${theme.colors.grey};
    border: 1px solid ${theme.colors.grey};`
      : `color:  ${theme.colors.darkGrey};
    border: 1px solid ${theme.colors.error.color};`}

  font-size: ${({ theme }) => theme.fontSizes.s};
  font-weight: 400;
  line-height: 24px;

  outline: none;
  border-radius: 12px;
  padding: 16px;
  width: 372px;
  height: 56px;
  margin-bottom: 5px;

  &:focus {
    ${({ $isInvalid, theme }) =>
      $isInvalid
        ? `border-color: ${theme.colors.darkBlue}; &::placeholder {color: ${theme.colors.darkBlue};}`
        : `border-color: ${theme.colors.error.color}; `}

    ${({ theme }) => `color: ${theme.colors.darkGrey};
    background-color: ${theme.colors.blue};`}
  }

  &::placeholder {
    ${({ theme }) => `color: ${theme.colors.grey};
    font-size: ${theme.fontSizes.s};`}
    font-weight: 400;
    line-height: 24px;
  }

  ${({ $isDisabled, theme }) =>
    $isDisabled &&
    `color: ${theme.colors.darkGrey};
    border: 2px solid ${theme.colors.disabled};`}

  &::-webkit-calendar-picker-indicator {
  }
`;

export const ErrorElement = styled.div`
  ${({ theme }) => `color: ${theme.colors.error.color};`}

  width: 100%;
  font-size: 13px;
  font-weight: 400;
  line-height: 15px;
  min-height: 30px;
  text-align: left;
`;

export const InputLabelElement = styled.label`
  ${({ theme }) => `font-size: ${theme.fontSizes.s}; color: ${theme.colors.darkGrey};`}

  line-height: 20px;
  font-weight: 600;
  text-align: left;
  margin: 0 0 8px;
  max-width: 372px;
  width: 100%;
`;

export const RadioButtonsContainer = styled.div`
  margin-bottom: 24px;
`;

export const RadioButtonWrapper = styled.div<{
  $isChecked: boolean;
}>`
  display: flex;
  flex-direction: row;
  align-items: center;
  max-width: 250px;
  width: 100%;
  cursor: pointer;

  & #error {
    display: none;
  }

  & input {
    opacity: 0;
    width: 0;
    height: 0;
  }

  & label {
    position: relative;
    display: inline-block;
    padding-left: 35px;
    cursor: pointer;
    font-weight: 400;
  }

  & label::before,
  label::after {
    position: absolute;
    content: '';
    display: inline-block;
    cursor: pointer;
  }

  & label::before {
    height: 24px;
    width: 24px;

    ${({ $isChecked, theme }) =>
      $isChecked
        ? `border: 2px solid ${theme.colors.purple};`
        : `border: 2px solid ${theme.colors.grey};`}

    border-radius: 50%;
    left: 0;
    bottom: -3px;
  }

  & label::after {
    left: 6px;
    top: 1px;
    width: 16px;
    height: 16px;
    border-radius: 50%;
    ${({ $isChecked, theme }) =>
      $isChecked ? `display: block; background-color: ${theme.colors.purple};` : `display: none;`}
  }
`;

export const PasswordInputWrapper = styled.div`
  position: relative;

  & svg {
    position: absolute;
    bottom: 50px;
    right: 45px;
  }
`;

export const IconsWrapper = styled.div`
  position: absolute;
  bottom: 50px;
  right: 16px;
  cursor: pointer;

  & svg {
    bottom: 0;
    right: 0;
  }
`;

export const CheckboxWrapper = styled.div<{
  $isChecked: boolean;
  $labelSize: string;
}>`
  display: flex;
  flex-direction: row;
  max-width: 372px;
  width: 100%;
  cursor: pointer;
  margin-bottom: 20px;
  position: relative;

  & input {
    opacity: 0;
    width: 0;
    height: 0;
  }

  & #error {
    display: none;
  }

  & label {
    position: relative;
    display: inline-block;
    padding-left: 35px;
    cursor: pointer;
    font-weight: 400;
    line-height: 20px;
    ${({ theme, $labelSize }) =>
      `color: ${theme.colors.darkGrey}; font-size: ${theme.fontSizes[$labelSize]}`}
  }

  & label::before {
    position: absolute;
    content: '';
    display: inline-block;
    cursor: pointer;
  }

  & label::before {
    height: 20px;
    width: 20px;

    ${({ $isChecked, theme }) =>
      $isChecked
        ? `background-color:${theme.colors.purple};`
        : `background-color: ${theme.colors.white};`}

    ${({ theme }) => `border: 2px solid ${theme.colors.purple};`}

    border-radius: 4px;
    left: 0;
    top: 0;
  }

  & svg {
    ${({ $isChecked }) => ($isChecked ? `display: block;` : `display: none;`)}
    width: 20px;
    height: 20px;
    position: absolute;
    top: 0;
    left: 2px;
    z-index: 100;
  }
`;
