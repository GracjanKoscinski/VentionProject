import styled from 'styled-components';

export const Table = styled.table`
  width: 100%;
  border-collapse: collapse;
  margin: 20px 0;
  box-shadow: 0px 4px 12px rgba(0, 0, 0, 0.2); 
`;

export const TableRow = styled.tr`
  border-bottom: 1px solid #ddd;
`;

export const TableHeader = styled.th`
  padding: 12px;
  text-align: left;
  background-color: white;
  color: black;
  border-right: 1px solid #ddd;
  font-weight: bold;
  font-size: ${({ theme }) => theme.fontSizes.s};
  cursor: pointer;

  &:last-child {
    border-right: none;
  }
`;

export const TableCell = styled.td`
  padding: 12px;
  text-align: left;
  font-size: ${({ theme }) => theme.fontSizes.s};
`;
export const FlexTableCell = styled(TableCell)`
 display: flex;
 justify-content: space-between;
 align-items: center;
 font-size: ${({ theme }) => theme.fontSizes.s};
`;

export const Icon = styled.img`
 width: 7px;
  height: 7px;
  margin-right: 6px;
  margin-bottom: 3px; 
`;

export const LimitChange = styled.div`
 display: flex;
 justify-content: space-between;
 align-items: center;
`;

export const StatusCell = styled.div`
display: flex; 
align-items: center; 
 font-size: ${({ theme }) => theme.fontSizes.s};
`;

export const DorpDownMenu = styled.div`
 position: relative;
  display: inline-block;

  & ul {
    position: absolute;
    right: 0;
    top: 25px;
    background-color: #fff;
    border: 1px solid #ddd;
    box-shadow: 0px 4px 12px rgba(0, 0, 0, 0.1);
    z-index: 1;
    list-style-type: none;
    margin: 0;
    padding: 10px;
    width: 150px;
    text-align: right;
    font-size: ${({ theme }) => theme.fontSizes.xs};
    color: ${({ theme }) => theme.colors.grey};

  & li {
    margin-right: 10px;
    font-size: ${({ theme }) => theme.fontSizes.xs};
    color: #333;
    cursor: pointer;
  }

`;

export const PaginationWrapper = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding: 10px;
  box-shadow: 0px 4px 12px rgba(0, 0, 0, 0.2); 

  & p {
    text-align: left;
    font-size: ${({ theme }) => theme.fontSizes.xs};
    margin-bottom: 5px;
    margin-right: 20px;
    margin-left: 20px;
  }

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
    cursor: pointer;
  }

  select, & button {
    padding: 10px;
    font-size: ${({ theme }) => theme.fontSizes.xs};
    border: 1px solid #ccc;
    border-radius: 4px;
    background-color: ${({ theme }) => theme.colors.white};
    &:hover {
      border-color: ${({ theme }) => theme.colors.lightGrey};
    }
    &:focus {
      outline: none;
      border-color: ${({ theme }) => theme.colors.lightGrey};
    }
  }
`;
