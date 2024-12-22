import React, { useEffect, useState } from 'react';

import { TabletWithShadow, RoundImageBackground, Label, DetailsDots } from '@/components/common';
import {
  VisitDataWrapper,
  ContentWrapper,
  Dot,
  ResizeWrapper,
  Wrapper,
  DataSectionWrapper,
  TopWrapper,
  LocationWrapper,
  StatusWrapper,
} from './styles';

import { STATUS_COLORS } from './Statuses';

import type { Visit } from '../DashboardTabs/Visits';

import Icon from '@/components/Icons';

import Image from '@/mocks/DoctorAvatarTest.png';

type Props = {
  data: Visit;
};

const VisitRow = ({ data }: Props) => {
  const nameArray = data.doctorsName.split(' ');
  const doctorInitials = nameArray[0][0] + nameArray[nameArray.length - 1][0];

  const [isResize, setIsResize] = useState(window.innerWidth <= 1120);

  const handleResize = () => {
    if (window.innerWidth <= 1120) {
      setIsResize(true);
    } else {
      setIsResize(false);
    }
  };

  useEffect(() => {
    addEventListener('resize', handleResize);

    return () => removeEventListener('resize', handleResize);
  }, []);

  return (
    <TabletWithShadow $padding="27px">
      {!isResize ? (
        <ContentWrapper>
          <VisitDataWrapper>
            <RoundImageBackground
              $backgroundImage={Image}
              $backgroundColor="white"
              $borderColor={Image ? 'white' : 'purple'}
            >
              {!Image && doctorInitials}
            </RoundImageBackground>
            <Label
              label={data.doctorsSpecialization}
              fontWeight={600}
              fontSize="s"
              lineHeight="20px"
              color="darkGrey"
              margin="0 0 0 16px"
            />
          </VisitDataWrapper>
          <Label
            label={`Dr. ${data.doctorsName}`}
            fontWeight={400}
            fontSize="s"
            lineHeight="22px"
            color="darkGrey"
            margin="0"
          />
          <VisitDataWrapper>
            <Icon type="clock" />
            <Label
              label={data.dateFrom}
              fontWeight={400}
              fontSize="s"
              lineHeight="20px"
              color="darkGrey"
              margin="0 0 0 16px"
            />
          </VisitDataWrapper>
          <VisitDataWrapper>
            <Icon type="location" />
            <Label
              label={data.location}
              fontWeight={400}
              fontSize="s"
              lineHeight="20px"
              color="darkGrey"
              margin="0 0 0 16px"
            />
          </VisitDataWrapper>
          <Wrapper>
            <Dot $size="10px" $backgroundColor={STATUS_COLORS[data.status].color} />
            <Label
              label={STATUS_COLORS[data.status].text}
              fontWeight={400}
              fontSize="s"
              lineHeight="20px"
              color="darkGrey"
              margin="0 0 0 8px"
            />
          </Wrapper>
          <DetailsDots />
        </ContentWrapper>
      ) : (
        <>
          <DataSectionWrapper>
            <TopWrapper>
              <ResizeWrapper>
                <RoundImageBackground
                  $backgroundImage={Image}
                  $backgroundColor="white"
                  $borderColor={Image ? 'white' : 'purple'}
                >
                  {!Image && doctorInitials}
                </RoundImageBackground>
                <DataSectionWrapper>
                  <Label
                    label={data.doctorsSpecialization}
                    fontWeight={600}
                    fontSize="s"
                    lineHeight="20px"
                    color="darkGrey"
                    margin="0"
                  />
                  <Label
                    label={`Dr. ${data.doctorsName}`}
                    fontWeight={400}
                    fontSize="s"
                    lineHeight="22px"
                    color="darkGrey"
                    margin="0"
                  />
                </DataSectionWrapper>
              </ResizeWrapper>
              <StatusWrapper>
                <Wrapper>
                  <Dot $size="10px" $backgroundColor={STATUS_COLORS[data.status].color} />
                  <Label
                    label={STATUS_COLORS[data.status].text}
                    fontWeight={400}
                    fontSize="s"
                    lineHeight="20px"
                    color="darkGrey"
                    margin="0 0 0 8px"
                  />
                </Wrapper>
                <DetailsDots />
              </StatusWrapper>
            </TopWrapper>
            <LocationWrapper>
              <VisitDataWrapper>
                <Icon type="clock" />
                <Label
                  label={data.dateFrom}
                  fontWeight={400}
                  fontSize="s"
                  lineHeight="20px"
                  color="darkGrey"
                  margin="0 0 0 16px"
                />
              </VisitDataWrapper>
              <VisitDataWrapper>
                <Icon type="location" />
                <Label
                  label={data.location}
                  fontWeight={400}
                  fontSize="s"
                  lineHeight="20px"
                  color="darkGrey"
                  margin="0 0 0 16px"
                />
              </VisitDataWrapper>
            </LocationWrapper>
          </DataSectionWrapper>
        </>
      )}
    </TabletWithShadow>
  );
};

export default VisitRow;

