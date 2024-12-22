import React, { useState } from 'react';

import { Label, Tabs } from '@/components/common';
import { Wrapper, BottomData } from './styles';
import { Visits, Tests, Referrals } from './DashboardTabs';
import { CareTeam, Recommendations } from './components';

import { IconType } from '@/components/Icons/index';

export type LabelTypeDashBoard = 'visits' | 'tests' | 'referrals';

const DashBoard = () => {
  const tabs: { icon: IconType; label: LabelTypeDashBoard }[] = [
    { label: 'visits', icon: 'visitsIcon' },
    { label: 'tests', icon: 'testsIcon' },
    { label: 'referrals', icon: 'prescriptionsIcon' },
  ];

  const tabsToDisplay = {
    visits: { component: Visits },
    tests: { component: Tests },
    referrals: { component: Referrals },
  };
  const [currentTab, setCurrentTab] = useState(tabs[0].label);

  const handleDashboardView = (tabLabel: LabelTypeDashBoard) => {
    setCurrentTab(tabLabel);
  };

  const CurrentBody = tabsToDisplay[currentTab].component;

  return (
    <Wrapper>
      <Label
        label="Welcome to Medfast Hospital!"
        fontWeight={700}
        fontSize="l"
        lineHeight="30px"
        color="darkGrey"
        margin="0 0 32px 0"
      />
      <Tabs currentTab={currentTab} tabs={tabs} onClick={handleDashboardView} size="max" />
      <CurrentBody />
      <BottomData>
        <CareTeam />
        <Recommendations />
      </BottomData>
    </Wrapper>
  );
};

export default DashBoard;

