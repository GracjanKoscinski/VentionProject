import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { useUser } from '@/utils/UserContext';

import { PasswordChangeForm, Header } from './components';
import { PopUpWithContent } from '@/components/common';
import { Wrapper } from './components/styles';

export type LabelTypeSettings = 'password' | 'data' | 'option';

type Props = {
  setIsSettings: (isSettings: boolean) => void;
  setServerResponse: (serverResponse: 'somethingWrong' | 'hasBeenChanged') => void;
};

const SettingsPage = ({ setIsSettings, setServerResponse }: Props) => {
  const userAuth = useUser();
  const [isPortal, setIsPortal] = useState(false);
  const navigate = useNavigate();
  const tabs: { label: LabelTypeSettings }[] = [
    { label: 'password' },
    { label: 'data' },
    { label: 'option' },
  ];

  const tabsToDisplay = {
    password: { component: PasswordChangeForm },
    data: { component: () => <></> },
    option: { component: () => <></> },
  };

  const [currentTab, setCurrentTab] = useState<LabelTypeSettings>(tabs[0].label);

  const handleSettingsView = (tabLabel: LabelTypeSettings) => {
    setCurrentTab(tabLabel);
  };

  const CurrentBody = tabsToDisplay[currentTab].component;

  const handleConfirm = async () => {
    try {
      await userAuth.logOut(userAuth.userData?.accessToken || 'null');

      navigate('/password-reset', { replace: true });
    } catch (error: any) {
      setServerResponse('somethingWrong');
    }
  };

  return (
    <Wrapper>
      <Header
        currentTab={currentTab}
        tabs={tabs}
        handleSettingsView={handleSettingsView}
        setIsSettings={setIsSettings}
      />
      <CurrentBody
        setIsPortal={setIsPortal}
        setServerResponse={setServerResponse}
        setIsSettings={setIsSettings}
      />
      {isPortal && (
        <PopUpWithContent
          title="Forgot password?"
          message="You will be logged out and navigated to reset password page."
          confirmButton="I agree"
          cancelButton="Cancel"
          cancelMethod={() => setIsPortal(false)}
          confirmMethod={handleConfirm}
        />
      )}
    </Wrapper>
  );
};

export default SettingsPage;

