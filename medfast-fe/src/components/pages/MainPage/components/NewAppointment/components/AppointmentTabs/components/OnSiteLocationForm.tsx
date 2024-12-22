import React, { useState, useEffect } from 'react';

import {
  Divider,
  RoundImageBackground,
  Label,
  Input,
  BackToWithArrow,
  Button,
} from '@/components/common';
import { TitleWrapper, FormWrapper, InputWrapper, ServicesWrapper } from './styles';
import { WrapperWithShadow } from '../../styles';
import NoResults from './NoResults';

import Icon from '@/components/Icons';

import { useUser } from '@/utils/UserContext';

import locationInfo from '@/mocks/locations.json';
import Location from './Location';
import { OnSiteVisit } from '../OnSiteAppointment';
type Props = {
  visitInfo: OnSiteVisit;
  setVisitInfo: (visitInfo: OnSiteVisit) => void;
  handleError: (serverResponse: 'somethingWrong') => void;
  handleBack: () => void;
};

const OnSiteLocationForm = ({ visitInfo, setVisitInfo, handleError, handleBack }: Props) => {
  const userAuth = useUser();
  const [inputValue, setInputValue] = useState('');
  const [locations, setLocations] = useState<LocationType[] | []>(locationInfo.locations);
  const [locationsToDisplay, setLocationsToDisplay] = useState<LocationType[] | []>(locations);
  const [chosenLocation, setChosenLocation] = useState<ChosenLocationType | null>({
    id: null, 
    hospital_name: null, 
    street_address: null, 
    house: null
  });
  const [activeLocation, setActiveLocation] = useState<number | null>(null);
  const [isNoLocations, setIsNoLocations] = useState(false);

  const handleClick = (locationName: string, locationId: number) => {
    const selectedLocation = locations.find(loc => loc.id === locationId);
    if (selectedLocation) {
      setChosenLocation({
        id: selectedLocation.id,
        hospital_name: selectedLocation.hospital_name,
        street_address: selectedLocation.street_address,
        house: selectedLocation.house,
      });
      setActiveLocation(locationId);
    }
  };

  const handleConfirm = () => {
    setVisitInfo({
      ...visitInfo,
      location: {
        hospital_name: chosenLocation?.hospital_name || '',
        id: chosenLocation?.id || 0,
        street_address: chosenLocation?.street_address || '',
        house: chosenLocation?.house || '',
      },
    });
    handleBack();
  };

  const handleSearch = (event: React.ChangeEvent<HTMLInputElement>) => {
    isNoLocations && setIsNoLocations(false);
    const value = event.target.value;
    const regex = new RegExp(value, 'i');
    const filteredLocations = locations.filter((location) =>
      location.hospital_name.match(regex)
    );

    if (filteredLocations.length === 0) {
      setIsNoLocations(true);
    } else {
      setLocationsToDisplay(filteredLocations);
    }

    setInputValue(value);
  };

  const handleLocations = async () => {
    const token = userAuth.userData?.accessToken || '';

    try {
      const response = locationInfo.locations;
      setLocations(response);
    } catch (error: any) {
      handleError('somethingWrong');
    }
  };

  useEffect(() => {
    handleLocations();
  }, []);

  return (
    <>
      <FormWrapper>
        <BackToWithArrow label="Back" onClick={handleBack} />
        <TitleWrapper>
          <RoundImageBackground $backgroundColor="lightBlue" $borderColor="lightBlue">
            <Icon type="location" />
          </RoundImageBackground>
          <Label
            label="Location"
            fontWeight={700}
            fontSize="l"
            lineHeight="24px"
            color="darkGrey"
            margin="0 0 0 24px"
          />
        </TitleWrapper>
        <Divider $height="2px" $color="purple" />
        <Label
          label="Please select a location from the available options"
          fontWeight={400}
          fontSize="s"
          lineHeight="22px"
          color="darkGrey"
          margin="24px 0 16px"
        />
        <WrapperWithShadow $flexDirection="column">
          <InputWrapper>
            <Input
              name="locations"
              type="text"
              value={inputValue}
              placeholder="Find a location"
              onChange={(event) => handleSearch(event)}
            />
            <Icon type="search" />
          </InputWrapper>
          {isNoLocations ? (
            <NoResults />
          ) : (
            <ServicesWrapper>
              {locationsToDisplay.map((location) => (
                <Location
                  key={location.id}
                  name={location.hospital_name}
                  address={`${location.street_address}, ${location.house}`}
                  isActive={location.id === activeLocation}
                  onClick={() => handleClick(location.hospital_name, location.id)}
                />
              ))}
            </ServicesWrapper>
          )}
        </WrapperWithShadow>
      </FormWrapper>
      <Button
        label="Confirm location"
        buttonSize="l"
        disabled={!chosenLocation}
        onClick={handleConfirm}
      />
    </>
  );
};

export default OnSiteLocationForm;

type LocationType = {
  id: number;
  hospital_name: string;
  street_address: string;
  house: string;
};

type ChosenLocationType = {
  id: number | null;
  hospital_name: string | null;
  street_address: string | null;
  house: string | null;
};
