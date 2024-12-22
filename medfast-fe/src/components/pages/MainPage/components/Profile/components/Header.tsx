import React from 'react';

import { Label, Divider, BackToWithArrow, Tabs } from '@/components/common';

import { LabelTypeProfile } from '../ProfilePage';
import { HeaderWrapper } from './styles';

type Props = {
  currentTab: LabelTypeProfile;
  tabs: { label: LabelTypeProfile }[];
  handleProfileView: (tabLabel: LabelTypeProfile) => void;
  setIsProfile: (isProfile: boolean) => void;
};

const Header = ({ currentTab, tabs, handleProfileView, setIsProfile }: Props) => {
  return (
    <HeaderWrapper>
      <BackToWithArrow label="Back home" onClick={() => setIsProfile(false)} />
      <Label
        label="Profile"
        fontWeight={700}
        fontSize="l"
        lineHeight="24px"
        color="darkGrey"
        margin="13px 0 32px 0"
      />
      <Label
        label="Manage your profile informatioin here"
        fontWeight={400}
        fontSize="s"
        lineHeight="24px"
        color="darkGrey"
        margin="0 0 8px 0"
      />
      <Tabs currentTab={currentTab} tabs={tabs} onClick={handleProfileView} />
      <Divider $height="1px" $color="lightGrey" />
    </HeaderWrapper>
  );
};

export default Header;
