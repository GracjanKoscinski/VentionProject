import React from 'react';

import { TabletWithShadow, Label, Divider } from '@/components/common';
import { Wrapper, TopData, DataWrapper } from './styles';

type Test = {
  id: number;
  testName: string;
  doctorsName: string | null; 
  dateOfTest: string;
  hasPdfResult: boolean;
};

type Props = {
  data: Test;
  onClick?: () => void;
};

const TestRow = ({ data, onClick }: Props) => {
  return (
    <TabletWithShadow $padding="27px">
      <TopData>
        <DataWrapper>
          <Label
            label={data.testName}
            fontWeight={700}
            fontSize="s"
            lineHeight="20px"
            color="darkGrey"
            margin="0 0 8px 0"
          />
          <Label
            label={data.doctorsName ? `Dr. ${data.doctorsName}` : 'No assigned doctor'}
            fontWeight={400}
            fontSize="s"
            lineHeight="20px"
            color="darkGrey"
            margin="0"
          />
        </DataWrapper> 
        {onClick && data.hasPdfResult && (
          <Label
            label="Download PDF"
            fontWeight={500}
            fontSize="xs"
            lineHeight="20px"
            color="purple"
            margin="0"
            onClick={onClick}
          />
        )}
      </TopData>
      <Divider $height="1px" $color="lightGrey" />
      <Wrapper>
        <Label
          label="Date:"
          fontWeight={600}
          fontSize="s"
          lineHeight="22px"
          color="darkGrey"
          margin="12px 8px 0 0"
        />
        <Label
          label={data.dateOfTest}
          fontWeight={400}
          fontSize="s"
          lineHeight="22px"
          color="darkGrey"
          margin="12px 0 0"
        />
      </Wrapper>
    </TabletWithShadow>
  );
};

export default TestRow;

