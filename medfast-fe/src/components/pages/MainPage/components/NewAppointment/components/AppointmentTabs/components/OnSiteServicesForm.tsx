import React, { useState, useEffect } from 'react';

import {
  Divider,
  RoundImageBackground,
  Label,
  Input,
  BackToWithArrow,
  Button,
  Loader,
} from '@/components/common';
import { TitleWrapper, ServicesWrapper, FormWrapper, InputWrapper } from './styles';
import { WrapperWithShadow } from '../../styles';
import Service from './Service';
import NoResults from './NoResults';

import Icon from '@/components/Icons';

import { GetServices } from '@/api/GetServices';

import { useUser } from '@/utils/UserContext';

import { OnSiteVisit } from '../OnSiteAppointment';

type Props = {
  visitInfo: OnSiteVisit;
  setVisitInfo: (visitInfo: OnSiteVisit) => void;
  handleError: (serverResponse: 'somethingWrong') => void;
  handleBack: () => void;
};

const OnSiteServicesForm = ({ visitInfo, setVisitInfo, handleError, handleBack }: Props) => {
  const userAuth = useUser();
  const [inputValue, setInputValue] = useState('');
  const [services, setServices] = useState<ServiceType[]>([]);
  const [servicesToDisplay, setServicesToDisplay] = useState<ServiceType[]>([]);
  const [chosenService, setChosenService] = useState<ChosenServiceType | null>({
    service: null,
    id: null,
    duration: null,
  });
  const [activeService, setActiveService] = useState<number | null>(null);
  const [isNoServices, setIsNoServices] = useState(false);
  const [isLoadingServices, setIsLoadingServices] = useState(true);

  const handleClick = (serviceName: string, serviceId: number, duration: number) => {
    setChosenService({ service: serviceName, id: serviceId, duration });
    setActiveService(serviceId);
  };

  const handleConfirm = () => {
    setVisitInfo({
      ...visitInfo,
      service: {
        service: chosenService?.service || '',
        id: chosenService?.id || 0,
        duration: handleDuration(chosenService?.duration || 0),
      },
    });
    handleBack();
  };

  const handleDuration = (duration: number) => {
    if (duration < 60) {
      return `${duration} min`;
    } else {
      const minutes = duration % 60;
      const hours = (duration - minutes) / 60;

      return `${hours}h${minutes !== 0 ? ` ${minutes}min` : ``}`;
    }
  };

  const handleSearch = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (isNoServices) setIsNoServices(false);
    const value = event.target.value;
    const regex = new RegExp(value, 'i');
    const sortedServices = services.filter((service) => service.service.match(regex));

    if (sortedServices.length === 0) {
      setIsNoServices(true);
      setServicesToDisplay([]);
    } else {
      setServicesToDisplay(sortedServices);
    }

    setInputValue(value);
  };

  const handleServices = async () => {
    const token = userAuth.userData?.accessToken || '';

    try {
      const response = await GetServices(token);

      const servicesData = response.data || response.services || response;
      if (Array.isArray(servicesData)) {
        setServices(servicesData);
        setServicesToDisplay(servicesData);
      } else {
        throw new Error('Invalid data format received from GetServices');
      }
    } catch (error: any) {
      handleError('somethingWrong');
      setServices([]);
      setServicesToDisplay([]);
    } finally {
      setIsLoadingServices(false);
    }
  };

  useEffect(() => {
    handleServices();
  }, []);

  return (
    <>
      <FormWrapper>
        <BackToWithArrow label="Back" onClick={handleBack} />
        <TitleWrapper>
          <RoundImageBackground $backgroundColor="lightBlue" $borderColor="lightBlue">
            <Icon type="stethoscope" />
          </RoundImageBackground>
          <Label
            label="Service"
            fontWeight={700}
            fontSize="l"
            lineHeight="24px"
            color="darkGrey"
            margin="0 0 0 24px"
          />
        </TitleWrapper>
        <Divider $height="2px" $color="purple" />
        <Label
          label="Please select a service from the available options"
          fontWeight={400}
          fontSize="s"
          lineHeight="22px"
          color="darkGrey"
          margin="24px 0 16px"
        />
        <WrapperWithShadow $flexDirection="column">
          <InputWrapper>
            <Input
              name="services"
              type="text"
              value={inputValue}
              placeholder="Find a service"
              onChange={handleSearch}
            />
            <Icon type="search" />
          </InputWrapper>
          {isLoadingServices ? (
            <Loader />
          ) : isNoServices ? (
            <NoResults />
          ) : (
            <ServicesWrapper>
              {servicesToDisplay.map((service) => (
                <Service
                  key={service.id}
                  service={service.service}
                  duration={`(${handleDuration(service.duration)})`}
                  isActive={service.id === activeService}
                  onClick={() => handleClick(service.service, service.id, service.duration)}
                />
              ))}
            </ServicesWrapper>
          )}
        </WrapperWithShadow>
      </FormWrapper>
      <Button
        label="Confirm service"
        buttonSize="l"
        disabled={!chosenService}
        onClick={handleConfirm}
      />
    </>
  );
};

export default OnSiteServicesForm;

type ServiceType = {
  id: number;
  service: string;
  duration: number;
};

type ChosenServiceType = {
  id: number | null;
  service: string | null;
  duration: number | null;
};
