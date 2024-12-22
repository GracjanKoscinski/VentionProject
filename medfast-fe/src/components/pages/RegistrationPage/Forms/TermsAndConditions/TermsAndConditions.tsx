import React from 'react';

import { Label, TabletWithShadow, BackToWithArrow } from '@/components/common';
import { Wrapper, LabelWrapper } from './styles';
import TermsText from './text.json';

import Icon from '@/components/Icons';

type Props = {
  setIsTermsShown: (isTermsShown: boolean) => void;
};

const TermsAndConditions = ({ setIsTermsShown }: Props) => {
  const text = TermsText;

  const handleClick = () => {
    setIsTermsShown(false);
  };

  return (
    <Wrapper>
      <Icon type="logo" />
      <BackToWithArrow label="Back to Log In" onClick={handleClick} />
      <TabletWithShadow>
        <Label
          label="Terms and Conditions"
          fontWeight={700}
          fontSize="l"
          lineHeight="30px"
          color="darkGrey"
          margin="0 0 16px"
        />
        <Label
          label={TermsText.firstTitle}
          fontWeight={400}
          fontSize="s"
          lineHeight="22px"
          color="darkGrey"
          margin="0 0 16px"
        />
        <Label
          label={TermsText.firstText}
          fontWeight={400}
          fontSize="s"
          lineHeight="22px"
          color="darkGrey"
          margin="0 0 16px"
        />
        <Label
          label={TermsText.secondTitle}
          fontWeight={400}
          fontSize="s"
          lineHeight="22px"
          color="darkGrey"
          margin="0 0 16px"
        />
        <Label
          label={TermsText.secondText}
          fontWeight={400}
          fontSize="s"
          lineHeight="22px"
          color="darkGrey"
          margin="0 0 16px"
        />
        <LabelWrapper>
          <Label
            label="Last updated: May 12, 2022"
            fontWeight={400}
            fontSize="s"
            lineHeight="22px"
            color="darkGrey"
            margin="0"
          />
        </LabelWrapper>
      </TabletWithShadow>
    </Wrapper>
  );
};

export default TermsAndConditions;

