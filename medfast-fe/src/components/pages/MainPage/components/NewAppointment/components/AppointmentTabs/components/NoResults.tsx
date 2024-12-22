import React from 'react';

import { Label } from '@/components/common';
import { NoResultsWrapper, NoResultsTitleWrapper } from './styles';
import Icon from '@/components/Icons';

const NoResults = () => {
  return (
    <NoResultsWrapper>
      <NoResultsTitleWrapper>
        <Label
          label="The result has not been found"
          fontWeight={600}
          fontSize="m"
          lineHeight="22px"
          color="darkGrey"
          margin="0 0 16px"
        />
        <Label
          label={`Check if there is an error or look for something similar`}
          fontWeight={400}
          fontSize="s"
          lineHeight="22px"
          color="darkGrey"
          margin="0"
        />
      </NoResultsTitleWrapper>
      <Icon type="tests" />
    </NoResultsWrapper>
  );
};

export default NoResults;

