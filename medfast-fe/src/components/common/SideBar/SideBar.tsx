import React, { useEffect, useState, useRef } from 'react';

import { TabletWithShadow } from '@/components/common/TabletWithShadow/styles';
import {
  ContentWrapper,
  OtherWrapper,
  TabsWrapper,
  Wrapper,
  TabWrapper,
  IconWrapper,
  Wrap,
} from './styles';
import { FullScreenShadow } from '@/components/common/FullScreenShadow/styles';
import { NotificationNumber } from '@/components/common/NotificationNumber/styles';
import Label from '@/components/common/Label/Label';

import Icon from '@/components/Icons';
import { IconType } from '@/components/Icons';

type Props = {
  setIsSettings: (isSettings: boolean) => void;
  setIsLogOut: (isLogOut: boolean) => void;
  setIsProfile: (isProfile: boolean) => void;
};

const SideBar = ({ setIsSettings, setIsLogOut, setIsProfile }: Props) => {
  const [isSideBarOpen, setIsSideBarOpen] = useState(false);
  const [currentTab, setCurrentTab] = useState('Dashboard');
  const [isShadow, setIsShadow] = useState(false);
  const [isResize, setIsResize] = useState(window.innerWidth <= 960);
  const ref = useRef<HTMLDivElement>(null);

  const generalTabs: { icon: IconType; label: string }[] = [
    { icon: 'home', label: 'Dashboard' },
    { icon: 'calendarGrey', label: 'Calendar' },
    { icon: 'card', label: 'Payments' },
    { icon: 'careTeam', label: 'Care team' },
    { icon: 'person', label: 'Profile' },
  ];

  const otherTabs: { icon: IconType; label: string }[] = [
    { icon: 'settings', label: 'Settings' },
    { icon: 'logOut', label: 'Logout' },
  ];

  //TODO: replace quantity with API data
  const resizeTabs: { icon: IconType; label: string; quantity: number }[] = [
    { icon: 'messenger', label: 'Messages', quantity: 1 },
    { icon: 'notificationBell', label: 'Notifications', quantity: 2 },
  ];

  const handleResize = () => {
    if (window.innerWidth <= 960) {
      setIsResize(true);
    } else {
      setIsResize(false);
    }
  };

  useEffect(() => {
    addEventListener('resize', handleResize);

    return () => removeEventListener('resize', handleResize);
  }, []);

  const handleSideBarClose = (tabLabel: string) => {
    if (tabLabel === 'Settings') {
      setIsSettings(true);
      setIsProfile(false);
    }
    if (tabLabel === 'Profile') {
      setIsProfile(true);
      setIsSettings(false);

    }

    setIsShadow(false);
    setIsSideBarOpen(false);
    setCurrentTab(tabLabel);
  };

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (ref.current && ref.current.contains(event.target as Node)) {
        setIsSideBarOpen(false);
        setIsShadow(false);
      }
    };
    document.addEventListener('click', handleClickOutside, { capture: true });

    return () => document.removeEventListener('click', handleClickOutside, { capture: true });
  }, []);

  return (
    <>
      <Wrapper>
        <TabletWithShadow
          $shape={isSideBarOpen ? '0  24px 24px 0' : '0  56px 56px 0'}
          $padding="40px 0"
          $flex={isSideBarOpen ? '1 0 220px' : '1 0 110px'}
          $height="100%"
        >
          <ContentWrapper>
            <TabsWrapper>
              <IconWrapper>
                {isSideBarOpen ? <Icon type="logo" /> : <Icon type="logoIcon" />}
              </IconWrapper>
              <Label
                label="General"
                fontWeight={400}
                fontSize="xs"
                lineHeight="20px"
                color="grey"
                margin={isSideBarOpen ? '0 0 0 40px' : '0 25px '}
              />
              {generalTabs.map((tab) => (
                <TabWrapper
                  $isSideBarOpen={isSideBarOpen}
                  $isCurrentTab={currentTab === tab.label}
                  key={tab.label}
                  onClick={() => handleSideBarClose(tab.label)}
                >
                  <Icon type={tab.icon} />
                  <Label
                    label={tab.label}
                    fontWeight={500}
                    fontSize="s"
                    lineHeight="20px"
                    color="darkGrey"
                    margin="0 0 0 10px"
                  />
                </TabWrapper>
              ))}
              {isResize && (
                <>
                  {resizeTabs.map((tab) => (
                    <TabWrapper
                      $isSideBarOpen={isSideBarOpen}
                      $isCurrentTab={currentTab === tab.label}
                      key={tab.label}
                      onClick={() => handleSideBarClose(tab.label)}
                    >
                      <Wrap>
                        <Icon type={tab.icon} />
                        <NotificationNumber>{tab.quantity}</NotificationNumber>
                      </Wrap>
                      <Label
                        label={tab.label}
                        fontWeight={500}
                        fontSize="s"
                        lineHeight="20px"
                        color="darkGrey"
                        margin="0 0 0 10px"
                      />
                    </TabWrapper>
                  ))}
                </>
              )}
            </TabsWrapper>
            <OtherWrapper>
              <Label
                label="Other"
                fontWeight={400}
                fontSize="xs"
                lineHeight="20px"
                color="grey"
                margin={isSideBarOpen ? '0 0 0 40px' : '0 35px '}
              />
              {otherTabs.map((tab) => (
                <TabWrapper
                  $isSideBarOpen={isSideBarOpen}
                  $isCurrentTab={currentTab === tab.label && tab.label !== 'Logout'}
                  key={tab.label}
                  onClick={() => {
                    tab.label === 'Logout' && setIsLogOut(true);
                    handleSideBarClose(tab.label);
                  }}
                >
                  <Icon type={tab.icon} />
                  <Label
                    label={tab.label}
                    fontWeight={500}
                    fontSize="s"
                    lineHeight="20px"
                    color="darkGrey"
                    margin="0 0 0 10px"
                  />
                </TabWrapper>
              ))}
            </OtherWrapper>
            <TabWrapper
              $isSideBarOpen={isSideBarOpen}
              onClick={() => {
                setIsShadow(!isShadow);
                setIsSideBarOpen(!isSideBarOpen);
              }}
            >
              <Icon type={isSideBarOpen ? 'sideBarOpen' : 'sideBarClose'} />
              <Label
                label="Collapse"
                fontWeight={500}
                fontSize="s"
                lineHeight="20px"
                color="darkGrey"
                margin="0 0 0 10px"
              />
            </TabWrapper>
          </ContentWrapper>
        </TabletWithShadow>
      </Wrapper>
      {isShadow && <FullScreenShadow ref={ref} />}
    </>
  );
};

export default SideBar;

