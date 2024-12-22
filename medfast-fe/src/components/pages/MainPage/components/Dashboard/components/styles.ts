import styled from 'styled-components';

export const DataSectionWrapper = styled.div`
  margin: 5px 0 5px;
  min-height: 50px;

  display: flex;
  flex-direction: column;
`;

export const SectionTitle = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: flex-start;

  margin-bottom: 5px;

  cursor: pointer;
`;

export const InfoWrapper = styled.div`
  & > div {
    margin: 0 0 16px 0;
  }

  width: 100%;

  & > #highlightText {
    text-align: end;
    cursor: pointer;
    margin: 0;
  }
`;

export const WrapperRow = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;

  & div {
    max-width: 300px;
    width: 100%;
    text-align: center;
  }

  & button {
    padding: 18px 22px;
  }
`;

export const WrapperColumn = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  & div {
    max-width: 430px;
    width: 100%;
    text-align: center;
  }
`;

export const Line = styled.div<{
  $height?: string;
}>`
  width: 2px;
  border-radius: 2px;
  margin: 0 21px 0 3px;
  ${({ theme }) => `background-color: ${theme.colors.purple};`}
  ${({ $height }) => ($height ? `height: ${$height}` : 'min-height: 20px;')}
`;

export const Dot = styled.div<{
  $size: string;
  $backgroundColor: string;
}>`
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
  ${({ theme, $backgroundColor }) =>
    $backgroundColor === 'success' || $backgroundColor === 'error' || $backgroundColor === 'warning'
      ? `background-color: ${theme.colors[$backgroundColor].color};`
      : `background-color: ${theme.colors[$backgroundColor]};`}
`;

export const TabletWrapper = styled.div`
  display: flex;
  flex-direction: row;
  align-items: stretch;
  justify-content: flex-start;

  & > div:nth-child(2) {
    margin: 12px 0;
    width: 100%;
  }
`;

export const VisitDataWrapper = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  align-items: center;

  & > div:nth-child(2) {
    max-width: 180px;
    width: 100%;
  }

  & > #highlightText {
    flex: 1;

    max-width: 500px;
    width: 100%;

    margin-right: 10px;
  }

  & > div:first-child {
    margin: 0;
    flex-shrink: 0;
  }

  @media (max-width: 1000px) {
    & > div:nth-child(2) {
      min-width: 130px;
    }

    & > div:nth-child(3) {
      min-width: 70px;
    }
  }
`;

export const ContentWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;

  & > div {
    flex: 3;
    min-width: 170px;
  }

  & > div:last-child {
    flex: 0;
    min-width: 5px;
  }

  & > div:nth-child(5),
  > div:nth-child(2) {
    flex: 2;
    min-width: 140px;
  }

  & > div:first-child {
    min-width: 210px;
  }

  @media (max-width: 1315px) {
    gap: 10px;
  }

  & > div:nth-child(3),
  > div:nth-child(4) {
    min-width: 150px;
  }
`;

export const Wrapper = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  align-items: center;
`;

export const TopData = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;

  margin-bottom: 12px;

  & > #highlightText {
    cursor: pointer;
  }
`;

export const DataWrapper = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: start;
`;

export const DatesWrapper = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: flex-end;
  align-items: start;

  @media (max-width: 860px) {
    & div {
      margin: 0;
      }

    & > div {
      flex-direction: column;
      align-items: flex-start;
      margin-left: 20px;
    }
`;

export const BottomData = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: start;

  margin: 12px 24px 0 0;
  }
`;

export const ImageWrapper = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
`;

export const RecommendationWrapper = styled.div`
  width: 100%;
  padding: 20px 24px;
  border-radius: 10px;

  ${({ theme }) => `background: ${theme.button.default.background};`}

  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;

  & > div {
    max-width: 440px;
    width: 100%;
  }

  @media (max-width: 1150px) {
    justify-content: space-between;
  }
`;

export const CareTeamWrapper = styled.div`
  margin: 10px 0;

  & > button {
    flex-shrink: 0;
  }

  & button {
    width: 56px;
    padding: 18px 23px;
    transform: rotate(180deg);
    margin: 0 10px;

    ${({ theme }) => `color: ${theme.colors.white}`}
  }
`;

export const MemberContainer = styled.div``;

export const ResizeWrapper = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  align-items: flex-start;

  & div {
    margin: 0;
  }

  & > div:first-child {
    margin: 0 15px 0 0;
  }
`;

export const TopWrapper = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: flex-start;
  gap: 15px;
`;

export const LocationWrapper = styled.div`
  display: flex;
  flex-direction: column;
  margin-top: 15px;

  & > div:nth-child(2) {
    margin-top: 10px;
  }
`;

export const StatusWrapper = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  align-items: center;
  gap: 10px;

  max-width: 160px;
  width: 100%;
`;
