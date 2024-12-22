import styled from 'styled-components';

export const HeaderWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;

  width: 100%;

  margin-bottom: 40px;
`;

export const Wrapper = styled.div`
  display: flex;
  justify-content: flex-start;
  align-items: center;
  flex-direction: column;

  max-width: 1500px;
  width: 100%;
  height: 100vh;
  padding: 0 25px;
  margin-top: 32px;

  background-color: ${({ theme }) => theme.colors.white};
`;
export const ButtonWrapper = styled.div`
  display: flex;
  gap: 10px; /* Adjust the gap value as needed */
  width: auto;
  padding: 20px;
  margin-bottom: 40px;
`;

export const PointerLabel = styled.div`
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
`;

export const FileInput = styled.label`
  width: auto;
  padding: 5px 10px;
  border: 2px solid ${({ theme }) => theme.colors.purple};
  border-radius: 8px;
  font-size: 16px;
  color: ${({ theme }) => theme.colors.white};
  background-color: ${({ theme }) => theme.colors.purple};
  cursor: pointer;
  transition:
    border-color 0.3s,
    box-shadow 0.3s;
  font-weight: 500;
  line-height: 22px;
  margin: 20px 0px;
  appearance: none;

  &::-webkit-file-upload-button {
    visibility: hidden;
  }

  &:focus {
    border-color: ${({ theme }) => theme.colors.primary};
    box-shadow: 0 0 5px ${({ theme }) => theme.colors.primary};
    outline: none;
  }
`;
