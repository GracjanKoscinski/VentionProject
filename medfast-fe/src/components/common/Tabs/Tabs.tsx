import React from 'react';

import Label from '@/components/common/Label/Label';
import { TabsContainer, TabWrapper } from './styles';

import Icon from '@/components//Icons';
import { IconType } from '@/components/Icons';
import { LabelTypeDashBoard } from '@/components/pages/MainPage/components/Dashboard/DashBoard';
import { LabelTypeSettings } from '@/components/pages/MainPage/components/Settings/SettingsPage';
import { LabelTypeProfile } from '@/components/pages/MainPage/components/Profile/ProfilePage';
import { TabsContainerSize } from './styles';


type LabelSupportedTypes = LabelTypeDashBoard | LabelTypeSettings | LabelTypeProfile;

type Props<LabelType> = {
  currentTab: LabelType;
  tabs: { label: LabelType; icon?: IconType }[];
  onClick: (tabLabel: LabelType) => void;
  size?: TabsContainerSize;
};

const Tabs = <T extends LabelSupportedTypes>({ currentTab, tabs, onClick, size }: Props<T>) => {
  return (
    <TabsContainer $size={size}>
      {tabs.map((tab) => (
        <TabWrapper
          key={tab.label}
          $isCurrentTab={currentTab === tab.label}
          onClick={() => onClick(tab.label)}
        >
          {tab.icon && <Icon type={tab.icon} />}
          <Label
            label={`${tab.label[0].toUpperCase()}${tab.label.slice(1)}`}
            fontWeight={600}
            fontSize="s"
            lineHeight="22px"
            color={currentTab === tab.label ? 'white' : 'darkGrey'}
            margin={tab.icon ? '0 0 0 8px' : '0px'}
          />
        </TabWrapper>
      ))}
    </TabsContainer>
  );
};

export default Tabs;

