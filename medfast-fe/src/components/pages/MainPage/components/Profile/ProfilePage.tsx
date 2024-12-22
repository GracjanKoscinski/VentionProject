import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

import { useUser } from '@/utils/UserContext';

import { PopUpWithContent } from '@/components/common';
import PersonalInfoForm from './components/PersonalInfoForm';
import ContactInfoForm from './components/ContactInfoForm';
import AddressInfoForm from './components/AddressInfoForm';
import Header from './components/Header';
import { Wrapper } from './components/styles';
import { GetUserInfo } from '@/api/userProfile/GetUserInfo';
import { UserInfoType } from './components/Interface/UserInfoType';

export type LabelTypeProfile = 'personal' | 'contact' | 'address';

type Props = {
  setIsProfile: (isProfile: boolean) => void;
  setServerResponse: (serverResponse: 'somethingWrong' | 'hasBeenUpdated') => void;
};

const ProfilePage = ({ setIsProfile, setServerResponse }: Props) => {
  const userAuth = useUser();
  const navigate = useNavigate();
  const [userInfo, setUserInfo] = useState<UserInfoType>({} as UserInfoType);
  const tabs: { label: LabelTypeProfile }[] = [
    { label: 'personal' },
    { label: 'contact' },
    { label: 'address' },
  ];

  const tabsToDisplay = {
    personal: { component: PersonalInfoForm },
    contact: { component: ContactInfoForm },
    address: { component: AddressInfoForm },
  };

  const [currentTab, setCurrentTab] = useState<LabelTypeProfile>(tabs[0].label);

  const handleProfileView = (tabLabel: LabelTypeProfile) => {
    setCurrentTab(tabLabel);
  };

  const handleGetUserInfo = async () => {
    try {
      const response = await GetUserInfo(userAuth.userData?.accessToken || '');
      setUserInfo(response.data || null);
    } catch (error: any) {
      if (error.message === 401) {
        navigate('/login');
      } else {
        console.error('Error fetching user info:', error);
        setServerResponse('somethingWrong');
      }
    }
  };

  useEffect(() => {
    handleGetUserInfo();
  }, []);

  const CurrentBody = tabsToDisplay[currentTab].component;

  return (
    <Wrapper
      style={{
        overflowY: 'scroll',
        msOverflowStyle: 'none',
        scrollbarWidth: 'none',
      }}
    >
      <Header
        currentTab={currentTab}
        tabs={tabs}
        handleProfileView={handleProfileView}
        setIsProfile={setIsProfile}
      />
      <CurrentBody
        userInfo={userInfo}
        setServerResponse={setServerResponse}
        setUserInfo={setUserInfo}
      />
    </Wrapper>
  );
};

export default ProfilePage;
