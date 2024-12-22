import React, { useState, useEffect } from 'react';

import {
  RoundImageBackground,
  BackToWithArrow,
  Label,
  Divider,
  Button,
  Input,
  Loader
} from '@/components/common';
import { FormWrapper, TitleWrapper, DoctorsWrapper, InputWrapper } from './styles';
import { WrapperWithShadow } from '../../styles';
import Doctor from './Doctor';
import NoResults from './NoResults';

import { OnlineVisit } from '../OnlineAppointment';

import Icon from '@/components/Icons';

import { GetDoctorsForService } from '@/api/GetDoctorsForService';

import Image from '@/mocks/DoctorAvatarTest.png';
import doctorsInfo from '@/mocks/doctorsForVisits.json';
import { useUser } from '@/utils/UserContext';

type Props = {
  visitInfo: OnlineVisit;
  setVisitInfo: (visitInfo: OnlineVisit) => void;
  handleError: (serverResponse: 'somethingWrong') => void;
  handleBack: () => void;
};

const DoctorsForm = ({ visitInfo, setVisitInfo, handleError, handleBack }: Props) => {
  const userAuth = useUser();
  const [chosenDoctor, setChosenDoctor] = useState<ChosenDoctorType>({
    name: null,
    speciality: null,
    imageUrl: null,
    id: null,
  });
  const [doctors, setDoctors] = useState<DoctorType[]>([]);
  const [doctorsToDisplay, setDoctorsToDisplay] = useState<DoctorType[]>([]);
  const [activeDoctor, setActiveDoctor] = useState<number | null>(null);
  const [isNoDoctors, setIsNoDoctors] = useState(false);
  const [inputValue, setInputValue] = useState('');
  const [isLoadingDoctors, setIsLoadingDoctors] = useState(true);

  const isFilled = chosenDoctor.name !== null && chosenDoctor.speciality !== null;

  const handleClick = (
    doctorName: string,
    speciality: string[],
    imageUrl: string,
    doctorId: number,
  ) => {
    setChosenDoctor({ name: doctorName, speciality, imageUrl, id: doctorId });
    setActiveDoctor(doctorId);
  };

  const handleConfirm = () => {
    setVisitInfo({ ...visitInfo, doctor: chosenDoctor });
    handleBack();
  };

  const handleSearch = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (isNoDoctors) setIsNoDoctors(false);
    const value = event.target.value;
    const regex = new RegExp(value, 'i');
    const filteredDoctors = doctors.filter((doctor) => doctor.fullName.match(regex));

    if (filteredDoctors.length === 0) {
      setIsNoDoctors(true);
      setDoctorsToDisplay([]);
    } else {
      setDoctorsToDisplay(filteredDoctors);
      setIsNoDoctors(false);
    }

    setInputValue(value);
  };

  const handleDoctors = async () => {
    try {
      const token = userAuth.userData?.accessToken || '';

      const response = await GetDoctorsForService(token, visitInfo.service.id || 0);
      const doctorsData = response.data || [];
      const doctorsWithIds = doctorsData.map((doctor: DoctorType, index: number) => ({
        ...doctor,
        id: index,
      }));

      if (doctorsWithIds.length === 0) {
        setIsNoDoctors(true);
        setDoctors([]);
        setDoctorsToDisplay([]);
      } else {
        setIsNoDoctors(false);
        setDoctors(doctorsWithIds);
        setDoctorsToDisplay(doctorsWithIds);
      }
    } catch (error: any) {
      handleError('somethingWrong');
      setDoctors([]);
      setDoctorsToDisplay([]);
    } finally {
      setIsLoadingDoctors(false);
    }
  };

  useEffect(() => {
    handleDoctors();
  }, []);

  return (
    <>
      <FormWrapper>
        <BackToWithArrow label="Back" onClick={handleBack} />
        <TitleWrapper>
          <RoundImageBackground $backgroundColor="lightBlue" $borderColor="lightBlue">
            <Icon type="person" />
          </RoundImageBackground>
          <Label
            label="Doctor"
            fontWeight={700}
            fontSize="l"
            lineHeight="24px"
            color="darkGrey"
            margin="0 0 0 24px"
          />
        </TitleWrapper>
        <Divider $height="2px" $color="purple" />
        <Label
          label="Please select a doctor from the available options"
          fontWeight={400}
          fontSize="s"
          lineHeight="22px"
          color="darkGrey"
          margin="24px 0 16px"
        />
        <WrapperWithShadow $flexDirection="column">
          <InputWrapper>
            <Input
              name="doctors"
              type="text"
              value={inputValue}
              placeholder="Find a doctor"
              onChange={handleSearch}
            />
            <Icon type="search" />
          </InputWrapper>
          {isLoadingDoctors ? (
            <Loader />
          ) : (
            <>
              <Label
                label={`Available specialists (${doctorsToDisplay.length})`}
                fontWeight={600}
                fontSize="s"
                lineHeight="22px"
                color="darkGrey"
                margin="24px 0 16px 0"
              />
              {isNoDoctors ? (
                <NoResults />
              ) : (
                <DoctorsWrapper>
                  {doctorsToDisplay.map((doctor) => (
                    <Doctor
                      key={doctor.id}
                      name={doctor.fullName}
                      speciality={doctor.specializations}
                      location="Beth Moses Hospital 404 Hart Street"
                      slots={doctor.availableSlots}
                      imageUrl={doctor.userPhotoResponse || Image}
                      isActive={doctor.id === activeDoctor}
                      onClick={() =>
                        handleClick(
                          doctor.fullName,
                          doctor.specializations,
                          doctor.userPhotoResponse || Image,
                          doctor.id,
                        )
                      }
                    />
                  ))}
                </DoctorsWrapper>
              )}
            </>
          )}
        </WrapperWithShadow>
      </FormWrapper>
      <Button label="Confirm doctor" buttonSize="l" disabled={!isFilled} onClick={handleConfirm} />
    </>
  );
};

export default DoctorsForm;

type ChosenDoctorType = {
  name: string | null;
  speciality: string[] | null;
  imageUrl: string | null;
  id: number | null;
};

type DoctorType = {
  id: number;
  fullName: string;
  specializations: string[];
  userPhotoResponse: string | null;
  availableSlots: number;
};
