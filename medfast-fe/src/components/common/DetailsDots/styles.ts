import styled from 'styled-components';

export const Dot = styled.div`
  ${({ theme }) => `background-color: ${theme.colors.grey};`}

  width: 3px;
  height: 3px;
  border-radius: 50%;
`;

export const DotsContainer = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;

  height: 15px;
  cursor: pointer;
`;
