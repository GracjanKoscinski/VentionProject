import styled from 'styled-components';

import Img from '../images/ReviewsDisplay.png';

export const Wrapper = styled.div`
  width: 50%;
  height: 100%;
  padding: 40px;
  background-image: url(${Img});
  background-repeat: no-repeat;
  background-size: cover;
  background-position: center;

  display: flex;
  justify-content: flex-end;
  flex-direction: column;

  & div:first-child {
    max-width: 630px;
  }
`;

export const Round = styled.div<{ $rotate: string }>`
  ${({ theme, $rotate }) =>
    `border: 1px solid ${theme.colors.white};
    transform: rotate(${$rotate});
    -webkit-transform: rotate(${$rotate});`}

  width: 46px;
  height: 46px;
  border-radius: 50%;
  position: relative;
  margin-left: 16px;
  cursor: pointer;
`;

export const Arrow = styled.div`
  ${({ theme }) => `border: solid ${theme.colors.white};`}

  transform: rotate(135deg);
  -webkit-transform: rotate(135deg);
  top: 17px;
  border-width: 0 1px 1px 0;
  display: inline-block;
  padding: 5px;
  position: absolute;
  left: 12px;
  border-radius: 2px;
  cursor: pointer;
`;

export const Line = styled.div`
  ${({ theme }) => `background-color: ${theme.colors.white};`}

  position: absolute;
  top: 22px;
  left: 12px;
  width: 20px;
  height: 1px;
`;

export const ButtonWrapper = styled.div`
display: flex;
flex-direction: row:
align-items: center;
justify-content: center;
`;

export const StarsWrapper = styled.div`
  display: flex;
  flex-direction: row;

  & svg {
    margin-right: 8px;
  }
`;

export const ReviewWrapper = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
`;

export const Rating = styled.div`
  display: flex;
  flex-direction: column;
`;
