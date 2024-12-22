import React, { useState } from 'react';

import { Label } from '@/components/common';
import { DataWrapper, InfoWrapper } from './styles';
import Recommendation from './Recommendation';
import TabletWithoutData from './TabletWithoutData';

import recommendationsData from '@/mocks/recommendations.json';

const Recommendations = () => {
  //replace with API data
  const recommendations: RecommendationsType = recommendationsData;
  const [recommendationsToDisplay, setRecommendationsToDisplay] = useState({
    data: recommendations.recommendations?.slice(0, 2) || null,
    button: recommendations.recommendations && recommendations.recommendations.length > 2,
  });

  const handleMoreData = () => {
    setRecommendationsToDisplay({ data: recommendationsData.recommendations, button: false });
  };

  return (
    <DataWrapper>
      <Label
        label="Recommendations"
        fontWeight={700}
        fontSize="m"
        lineHeight="22px"
        color="darkGrey"
        margin="0 0 16px 0"
      />
      {recommendationsToDisplay.data ? (
        <InfoWrapper>
          {recommendationsToDisplay.data.map((recommendation) => (
            <Recommendation
              title={recommendation.title}
              text={recommendation.text}
              key={recommendation.title}
            />
          ))}
          {recommendationsToDisplay.button && (
            <Label
              label="See more"
              fontWeight={500}
              fontSize="s"
              lineHeight="22px"
              color="purple"
              margin="0"
              onClick={handleMoreData}
            />
          )}
        </InfoWrapper>
      ) : (
        <TabletWithoutData label="There will be recommends for you" icon="tests" />
      )}
    </DataWrapper>
  );
};

export default Recommendations;

type RecommendationType = {
  title: string;
  text: string;
};

type RecommendationsType = { recommendations: RecommendationType[] | null };

