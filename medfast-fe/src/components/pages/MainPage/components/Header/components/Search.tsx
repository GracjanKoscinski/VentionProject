import React, { useRef, useState, useEffect } from 'react';

import { Wrapper, OptionWrapper, OptionsWrapper, LabelWrapper, OptionGroupWrapper } from './styles';
import Input from '@/components/common/Input/Input';
import { RoundImageBackground } from '@/components/common/RoundImageBackground/styles';
import Label from '@/components/common/Label/Label';

import Icon from '@/components/Icons';

import DoctorImg from '@/components/pages/MainPage/Header/DoctorAvatarTest.png';
import { GetServices } from '@/api/GetServices';
import { useUser } from '@/utils/UserContext';

import doctorsData from '@/mocks/doctors.json';

type Props = {
  name: string;
  placeholder: string;
  handleChange: (option: string) => void;
};

type ServiceData = {
  specialization: string;
  name: string;
};

type DoctorData = {
  name: string;
  surname: string;
  speciality: string;
  imageUrl: string | null;
};

const Search = ({ name, placeholder, handleChange }: Props) => {
  const userAuth = useUser();
  const [options, setOptions] = useState({
    doctors: [] as DoctorData[],
    services: [] as ServiceData[],
  });

  const [optionsToDisplay, setOptionsToDisplay] = useState(options);
  const [isFocused, setIsFocused] = useState(false);
  const [value, setValue] = useState('');
  const ref = useRef<HTMLDivElement>(null);

  const handleClick = () => {
    setIsFocused(!isFocused);
  };

  const handleSelect = (option: string, event: React.MouseEvent) => {
    event.stopPropagation();

    setValue(option);
    setIsFocused(false);
    handleChange(option);
  };

  const handleSort = (valueToFind: string) => {
    const regex = new RegExp(valueToFind.trim(), 'i');

    const sortedDoctors = options.doctors
      .filter(
        (option) =>
          option.name.match(regex) ||
          option.speciality.match(regex) ||
          [option.name, option.surname].join(' ').match(regex) ||
          option.surname.match(regex),
      )
      .sort((a, b) => a.surname.localeCompare(b.surname));

    const sortedServices = options.services
      .filter((option) => option.name.match(regex) || option.specialization.match(regex))
      .sort((a, b) => a.name.localeCompare(b.name));

    setOptionsToDisplay({ doctors: sortedDoctors, services: sortedServices });
  };

  useEffect(() => {
    setOptionsToDisplay(options);
  }, [options]);

  useEffect(() => {
    GetServices(userAuth.userData?.accessToken || '').then((res) => {
      const mappedServices = res.data.map((service: any) => {
        return { name: service.service, specialization: '' };
      }, [] as any[]);

      setOptions({ doctors: doctorsData.doctors as any, services: mappedServices });
    });

    const handleClickOutside = (event: MouseEvent) => {
      if (ref.current && !ref.current.contains(event.target as Node)) {
        setIsFocused(false);
      }
    };
    document.addEventListener('click', handleClickOutside);

    return () => document.removeEventListener('click', handleClickOutside);
  }, []);

  return (
    <Wrapper onClick={handleClick} ref={ref}>
      <Icon type="search" />
      <Input
        onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
          handleSort(event.target.value);
          setValue(event.target.value);
        }}
        type="text"
        label=""
        placeholder={placeholder}
        name={name}
        value={value}
      />
      {isFocused && (
        <OptionsWrapper>
          {Object.values(optionsToDisplay).every((optionSet) => optionSet.length === 0) && (
            <Label
              label="Your search returned no results"
              fontWeight={400}
              fontSize="s"
              lineHeight="30px"
              color="red"
              margin="0 0 0 0rem"
            />
          )}
          <OptionGroupWrapper>
            {optionsToDisplay.doctors.length > 0 && (
              <Label
                label="Doctors"
                fontWeight={400}
                fontSize="m"
                lineHeight="30px"
                color="grey"
                margin="0 0 0 0.5rem"
              />
            )}
            {optionsToDisplay.doctors.map((option, index) => (
              <OptionWrapper
                key={[option.name, option.surname, option.speciality, index].join('')}
                onClick={(event: React.MouseEvent) =>
                  handleSelect(`${option.name} ${option.surname}`, event)
                }
              >
                <RoundImageBackground
                  $backgroundImage={option.imageUrl || ''}
                  $backgroundColor="white"
                  $borderColor={option.imageUrl ? 'white' : 'purple'}
                >
                  {!option.imageUrl && option.name[0] + option.surname[0]}
                </RoundImageBackground>
                <LabelWrapper>
                  <Label
                    label={`${option.name} ${option.surname}`}
                    fontWeight={600}
                    fontSize="s"
                    lineHeight="22px"
                    color="darkGrey"
                    margin="0"
                  />
                  <Label
                    label={option.speciality}
                    fontWeight={400}
                    fontSize="s"
                    lineHeight="22px"
                    color="darkGrey"
                    margin="0"
                  />
                </LabelWrapper>
              </OptionWrapper>
            ))}
          </OptionGroupWrapper>
          <OptionGroupWrapper>
            {optionsToDisplay.services.length > 0 && (
              <Label
                label="Services"
                fontWeight={400}
                fontSize="m"
                lineHeight="30px"
                color="grey"
                margin="0 0 0 0.5rem"
              />
            )}
            {optionsToDisplay.services.map((serviceData, index) => (
              <OptionWrapper
                key={serviceData.name + serviceData.specialization + index}
                onClick={(event: React.MouseEvent) => handleSelect(serviceData.name, event)}
              >
                <LabelWrapper>
                  <Label
                    label={serviceData.name}
                    fontWeight={600}
                    fontSize="s"
                    lineHeight="22px"
                    color="darkGrey"
                    margin="0"
                  />
                  <Label
                    label={serviceData.specialization}
                    fontWeight={400}
                    fontSize="s"
                    lineHeight="22px"
                    color="darkGrey"
                    margin="0"
                  />
                </LabelWrapper>
              </OptionWrapper>
            ))}
          </OptionGroupWrapper>
        </OptionsWrapper>
      )}
    </Wrapper>
  );
};

export default Search;

