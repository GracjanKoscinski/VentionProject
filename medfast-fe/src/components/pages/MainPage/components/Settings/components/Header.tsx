import React from 'react';

import { Label, Divider, BackToWithArrow, Tabs } from '@/components/common';
import { HeaderWrapper } from './styles';

import { LabelTypeSettings } from '../SettingsPage';

type Props = {
  currentTab: LabelTypeSettings;
  tabs: { label: LabelTypeSettings }[];
  handleSettingsView: (tabLabel: LabelTypeSettings) => void;
  setIsSettings: (isSettings: boolean) => void;
};

const Header = ({ currentTab, tabs, handleSettingsView, setIsSettings }: Props) => {
  return (
    <HeaderWrapper>
      <BackToWithArrow label="Back home" onClick={() => setIsSettings(false)} />
      <Tabs currentTab={currentTab} tabs={tabs} onClick={handleSettingsView} />
      <Label
        label="Settings"
        fontWeight={700}
        fontSize="l"
        lineHeight="24px"
        color="darkGrey"
        margin="13px 0 32px 0"
      />
      <Label
        label="Your password information and settings are stored here"
        fontWeight={400}
        fontSize="s"
        lineHeight="24px"
        color="darkGrey"
        margin="0 0 8px 0"
      />
      <Divider $height="1px" $color="lightGrey" />
    </HeaderWrapper>
  );
};

export default Header;
