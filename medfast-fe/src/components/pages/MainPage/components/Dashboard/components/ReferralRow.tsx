import React from 'react';

import { TabletWithShadow, Label, Button, DetailsDots, Divider } from '@/components/common';
import { Wrapper, TopData, DataWrapper, DatesWrapper, BottomData, Dot } from './styles';

import { STATUS_COLORS } from './Statuses';
import { Referral } from '../DashboardTabs/Referrals';

type Props = {
  data: Referral;
};

const ReferralRow = ({ data }: Props) => {
  return (
    <TabletWithShadow $padding="27px">
      <TopData>
        <DataWrapper>
          <Label
            label={data.specialization}
            fontWeight={700}
            fontSize="s"
            lineHeight="20px"
            color="darkGrey"
            margin="0 0 8px 0"
          />
          <Wrapper>
            <Label
              label="Issued by:"
              fontWeight={600}
              fontSize="s"
              lineHeight="20px"
              color="darkGrey"
              margin="0 8px 0 0"
            />
            <Label
              label={data.issuedBy}
              fontWeight={400}
              fontSize="s"
              lineHeight="20px"
              color="darkGrey"
              margin="0"
            />
          </Wrapper>
        </DataWrapper>
        {data.appointmentStatus === 'Scheduled' ? (
          <Button label="Schedule" buttonSize="xs" primary />
        ) : (
          <Wrapper>
            <Wrapper>
              <Dot $size="10px" $backgroundColor={'red'} />
              <Label
                label={STATUS_COLORS[data.appointmentStatus].text}
                fontWeight={400}
                fontSize="s"
                lineHeight="20px"
                color="darkGrey"
                margin="0 30px 0 8px"
              />
            </Wrapper>
            <DetailsDots />
          </Wrapper>
        )}
      </TopData>
      <Divider $height="1px" $color="lightGrey" />
      <BottomData>
        <Label
          label="Check how to prepare"
          fontWeight={500}
          fontSize="xs"
          lineHeight="22px"
          color="purple"
          margin="0"
        />
        <DatesWrapper>
          <Wrapper>
            <Label
              label="Date of issue:"
              fontWeight={500}
              fontSize="xs"
              lineHeight="22px"
              color="darkGrey"
              margin="0 8px 0 32px"
            />
            <Label
              label={data.dateOfIssue}
              fontWeight={400}
              fontSize="s"
              lineHeight="22px"
              color="darkGrey"
              margin="0"
            />
          </Wrapper>
          <Wrapper>
            <Label
              label="Expires:"
              fontWeight={500}
              fontSize="xs"
              lineHeight="22px"
              color="darkGrey"
              margin="0 8px 0 32px"
            />
            <Label
              label={data.expirationDate}
              fontWeight={400}
              fontSize="s"
              lineHeight="22px"
              color="darkGrey"
              margin="0"
            />
          </Wrapper>
        </DatesWrapper>
      </BottomData>
    </TabletWithShadow>
  );
};

export default ReferralRow;

