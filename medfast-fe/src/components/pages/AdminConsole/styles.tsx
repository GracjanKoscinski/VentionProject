import styled from 'styled-components';


export const ContentWrapper = styled.div`
  display: flex;
  flex-direction: column;
  padding: 20px;
`;



export const TitleWrapper = styled.div`
 margin: 20px 0 100px; 
  text-align: left;

  h1 {
    font-size: ${({ theme }) => theme.fontSizes.l};
    font-weight: bold;
  }
`;

