import React, { useState, useEffect } from 'react';

import {
  Button,
  RoundImageBackground,
  TabletWithShadow,
  Label,
  NotificationNumber,
} from '@/components/common';
import { Wrapper, UserWrapper, ContentWrapper, NotificationWrapper, Side } from './styles';

import Search from './components/Search';

import { useUser } from '@/utils/UserContext';

import Icon from '@/components/Icons';

//TODO: replace with API data
import Avatar from '@/mocks/AvatarTest.png';
import specializationServices from '@/mocks/specializationServices.json';

import { GetServices } from '@/api/GetServices';

type Props = {
  isSettings: boolean;
  createNewAppointment: () => void;
};

const Header = ({ isSettings, createNewAppointment }: Props) => {
  const userAuth = useUser();
  const isDoctor = userAuth.userData?.role === 'DOCTOR';
  const [isResize, setIsResize] = useState(window.innerWidth <= 840);
  const [services, setServices] = useState([]);

  const handleResize = () => {
    if (window.innerWidth <= 840) {
      setIsResize(true);
    } else {
      setIsResize(false);
    }
  };

  useEffect(() => {
    addEventListener('resize', handleResize);
    return () => removeEventListener('resize', handleResize);
  }, []);

  //TODO: replace with API data
  const notifications = 2;
  const messages = 1;
  const userData = { name: 'Melissa', surname: 'Nicholson', imageUrl: Avatar || null };

  const handleSearch = (option: string) => {};

  const handleServices = () => {};


  return (
    <Wrapper>
      <TabletWithShadow
        $shape="0 0 56px 56px"
        $padding="24px"
        $width={isSettings ? '100%' : '2000px'}
      >
        <ContentWrapper>
          <Side>
            <Search
              name="search"
              placeholder="Find a doctor or service"
              handleChange={(option: string) => handleSearch(option)}
            />
            {!isDoctor && (
              <>
                {!isResize && (
                  <Button
                    label="Our services"
                    buttonSize="xs"
                    primary
                    borderRadius="oval"
                    onClick={handleServices}
                  />
                )}
                <Button
                  label={isResize ? '+' : '+ Make an appointment'}
                  buttonSize="s"
                  borderRadius="oval"
                  onClick={createNewAppointment}
                />
              </>
            )}
          </Side>
          <Side>
            <NotificationWrapper>
              <RoundImageBackground $backgroundColor="lightBlue" $borderColor="white">
                <Icon type="messenger" />
                {messages > 0 && <NotificationNumber>{messages}</NotificationNumber>}
              </RoundImageBackground>
              <RoundImageBackground $backgroundColor="lightBlue" $borderColor="white">
                <Icon type="notificationBell" />
                {notifications > 0 && <NotificationNumber>{notifications}</NotificationNumber>}
              </RoundImageBackground>
            </NotificationWrapper>
            <UserWrapper>
              <RoundImageBackground
                $backgroundImage={userData.imageUrl}
                $backgroundColor="white"
                $borderColor={userData.imageUrl ? 'white' : 'purple'}
              >
                {!userData.imageUrl && userData.name[0] + userData.surname[0]}
              </RoundImageBackground>
              <Label
                label={`${userData.name} ${userData.surname}`}
                fontWeight={600}
                fontSize="s"
                lineHeight="20px"
                color="darkGrey"
                margin="0 0 0 16px"
              />
            </UserWrapper>
          </Side>
        </ContentWrapper>
      </TabletWithShadow>
    </Wrapper>
  );
};

export default Header;

