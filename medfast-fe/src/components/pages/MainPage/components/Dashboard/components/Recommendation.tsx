import React from 'react';

import { TabletWithShadow, Label } from '@/components/common';
import { RecommendationWrapper, DataWrapper } from './styles';

import Icon from '@/components/Icons';

type Props = {
  title: string;
  text: string;
};

const Recommendation = ({ title, text }: Props) => {
  return (
    <TabletWithShadow $padding="0" $margin="0 0 16px 0">
      <RecommendationWrapper>
        <DataWrapper>
          <Label
            label={title}
            fontWeight={600}
            fontSize="m"
            lineHeight="22px"
            color="white"
            margin="0 0 8px 0"
          />
          <Label
            label={text}
            fontWeight={400}
            fontSize="s"
            lineHeight="22px"
            color="white"
            margin="0 32px 0 0"
          />
        </DataWrapper>
        <Icon type="needle" />
      </RecommendationWrapper>
    </TabletWithShadow>
  );
};

export default Recommendation;

