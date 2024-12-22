import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

import { useUser } from '@/utils/UserContext';

import { SideBar, PopUpWithContent, ServerResponsePopUp } from '@/components/common';
import { FullMainScreenWrapper, Wrapper, Content } from './styles';
import Header from './components/Header/Header';
import DashBoard from './components/Dashboard/DashBoard';
import SettingsPage from './components/Settings/SettingsPage';
import NewAppointment from './components/NewAppointment/NewAppointment';
import ProfilePage from './components/Profile/ProfilePage';

const MainPage = () => {
  const userAuth = useUser();
  const isDoctor = userAuth.userData?.role === 'DOCTOR';
  const isAdmin = userAuth.userData?.role === 'ADMIN';

  const [isSettings, setIsSettings] = useState(false);
  const [isProfile, setIsProfile] = useState(false);
  const [isLogOut, setIsLogOut] = useState(false);
  const [isNewAppointmentOpen, setIsNewAppointmentOpen] = useState(false);
  const [serverResponse, setServerResponse] = useState<
    'somethingWrong' | 'hasBeenChanged' | 'hasBeenUpdated' | null
  >(null);
  const isMainPage = !isProfile && !isSettings;

  const navigate = useNavigate();

  const handleLogOut = async () => {
    try {
      await userAuth.logOut(userAuth.userData?.accessToken || 'null');

      navigate('/logIn', { replace: true });
    } catch (error: any) {
      setServerResponse('somethingWrong');
      setIsLogOut(false);
    }
  };

  const handleNewAppointment = () => {
    setIsNewAppointmentOpen(true);
  };

  useEffect(() => {
    const timeOutId = setTimeout(() => setServerResponse(null), 5000);

    return () => clearTimeout(timeOutId);
  }, [serverResponse]);

  useEffect(() => {
    isAdmin && navigate('/admin-console', { replace: true });
  }, []);

  return (
    !isAdmin && (
      <FullMainScreenWrapper>
        <ServerResponsePopUp
          serverResponse={serverResponse}
          onClick={() => setServerResponse(null)}
        />
        {isLogOut && (
          <PopUpWithContent
            title="Log out?"
            message="Are you sure you want to log out from your Medfast account?"
            confirmButton="Log out"
            cancelButton="Cancel"
            cancelMethod={() => setIsLogOut(false)}
            confirmMethod={handleLogOut}
          />
        )}
        <SideBar
          setIsSettings={setIsSettings}
          setIsLogOut={setIsLogOut}
          setIsProfile={setIsProfile}
        />
        <Wrapper>
          <Header isSettings={isSettings} createNewAppointment={handleNewAppointment} />
          <Content>
            {isMainPage && !isDoctor && <DashBoard />}
            {isSettings && (
              <SettingsPage setIsSettings={setIsSettings} setServerResponse={setServerResponse} />
            )}
            {isProfile && (
              <ProfilePage setIsProfile={setIsProfile} setServerResponse={setServerResponse} />
            )}
          </Content>
        </Wrapper>
        <NewAppointment
          isNewAppointmentOpen={isNewAppointmentOpen}
          setIsNewAppointmentOpen={setIsNewAppointmentOpen}
          setServerResponse={setServerResponse}
        />
      </FullMainScreenWrapper>
    )
  );
};

export default MainPage;
