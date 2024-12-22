import React from 'react';
import { Button, RoundImageBackground, TabletWithShadow, Label } from '../../common';
import {
  HeaderWrapper,
  UserWrapper,
  HeaderContentWrapper,
  Side,
  ListOfOptions,
} from './headerStyles';

import Avatar from '@/mocks/AvatarTest.png';
import Search from './components/Search';
import { useNavigate } from 'react-router-dom';
import Icon from '@/components/Icons';

type Props = {
  searchTerm: string;
  setSearchTerm: (value: string) => void;
  setIsLogOut: (logout: boolean) => void;
  searchBy: string;
  setSearchBy: (value: string) => void;
};

const Header = ({ searchTerm, setSearchTerm, setIsLogOut, searchBy, setSearchBy }: Props) => {
  const userData = { name: 'Melissa', surname: 'Nicholson', imageUrl: Avatar || null };
  const navigate = useNavigate();

  const handleLogOutClick = () => {
    setIsLogOut(true);
  };

  const handleAddDoctorClick = () => {
    navigate('/admin-console/doctor-registration');
  };

  const handleSearchByChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setSearchBy(e.target.value);
  };

  return (
    <HeaderWrapper>
      <TabletWithShadow $padding="24px" $width="100%">
        <HeaderContentWrapper>
          <Side>
            <Search searchTerm={searchTerm} setSearchTerm={setSearchTerm} />
            <ListOfOptions>
              <label htmlFor="searchOption"> Search by:</label>
              <select id="searchOption" value={searchBy} onChange={handleSearchByChange}>
                <option value="SURNAME">Surname, name, email</option>
                <option value="STATUS">User's status</option>
                <option value="SPECIALIZATION">Specialization</option>
              </select>
            </ListOfOptions>
          </Side>
          <Side>
            <Button
              label={'+ Add New Doctor'}
              buttonSize="s"
              borderRadius="oval"
              onClick={handleAddDoctorClick}
            />
          </Side>
          <Side style={{ cursor: 'pointer' }} onClick={handleLogOutClick}>
            <Icon type="logOut" />
            <Label
              label="Log out"
              fontWeight={500}
              fontSize="s"
              lineHeight="20px"
              color="darkGrey"
              margin="0 0 0 10px"
            />
          </Side>
          <Side>
            <UserWrapper>
              <RoundImageBackground
                $backgroundImage={Avatar}
                $backgroundColor="white"
                $borderColor={Avatar ? 'white' : 'purple'}
              />
              <Label
                label={'Melissa Nicholson'}
                fontWeight={600}
                fontSize="s"
                lineHeight="20px"
                color="darkGrey"
                margin="0 0 0 16px"
              />
            </UserWrapper>
          </Side>
        </HeaderContentWrapper>
      </TabletWithShadow>
    </HeaderWrapper>
  );
};

export default Header;

