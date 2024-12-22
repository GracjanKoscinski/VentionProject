import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

import { Header, Button } from '@/components/common';
import {
  PersonalDataForm,
  PasswordForm,
  DoctorDetailsForm,
  ContactsForm,
  SuccessSignUp,
} from './Forms';
import { default as TermsAndConditions } from './Forms/TermsAndConditions/TermsAndConditions';
import { FullScreenWrapper } from './FullScreenWrapper/styles';

import { format } from 'date-fns';

import { useUser } from '@/utils/UserContext';

import { DoctorSignUp } from '@/api/DoctorSignUp';
import { SetTemporaryPassword } from '@/api/SetTemporaryPassword';

type Props = {
  stage?: number;
};

const RegistrationPageDoctor = ({ stage }: Props) => {
  const location = useLocation();
  const userAuth = useUser();
  const navigate = useNavigate();
  const [doctorData, setDoctorData] = useState<DoctorData>({
    role: 'doctor',
    name: null,
    surname: null,
    birthday: null,
    email: null,
    phone: null,
    specialization: null,
    licenseNumber: null,
    password: null,
    repeatedPassword: null,
    isChecked: false,
  });
  const [serverError, setServerError] = useState<
    'alreadyExist' | 'somethingWrong' | 'specializationNotFound' | null
  >(null);
  const [isTermsShown, setIsTermsShown] = useState(false);
  const [isSuccessSignUp, setSuccessSignUp] = useState(false);
  const [isPermanentPassword, setIsPermanentPassword] = useState(false);
  const [stageNumber, setStageNumber] = useState(stage ? stage : 0);
  const [isLoading, setIsLoading] = useState(false);
  const emailConfirmationMessage =
    'Doctor should check the email for temporary password and change it using the link within 24 hours.';
  const permanentPasswordMessage = 'Your permanent password has been successfully created.';
  const stages = [
    { id: 1, component: PersonalDataForm },
    { id: 2, component: DoctorDetailsForm },
    { id: 3, component: ContactsForm },
    { id: 4, component: PasswordForm },
  ];
  const CurrentBody = stages[stageNumber].component;

  const handleSignUp = async () => {
    try {
      setIsLoading(true);

      await DoctorSignUp({
        adminToken: userAuth.userData?.accessToken || 'null',
        doctorData: {
          email: doctorData.email || '',
          name: doctorData.name || '',
          surname: doctorData.surname || '',
          birthDate: doctorData.birthday ? format(doctorData.birthday, 'yyyy-MM-dd') : '',
          phone:
            doctorData.phone
              ?.split('')
              .filter((char) => !isNaN(+char))
              .filter((char) => char !== ' ')
              .join('') || '',
          licenseNumber: doctorData.licenseNumber || '',
          //TODO: replace hardcoded data
          specializationIds: [1, 2, 3, 4],
          locationId: 1,
        },
      });

      setSuccessSignUp(true);
    } catch (error: any) {
      if (error.message === '201') {
        setSuccessSignUp(true);
      } else if (error.message === '409') {
        setServerError('alreadyExist');
      } else if (error.message === '404') {
        setServerError('specializationNotFound');
      } else {
        setServerError('somethingWrong');
      }
    }
    setIsLoading(false);
  };

  const handleTemporaryPassword = async () => {
    const data = location.search;
    const email = data.substring(data.indexOf('=') + 1, data.indexOf('&'));
    const code = data.substring(data.lastIndexOf('=') + 1);

    const newData = {
      code,
      newPassword: doctorData.password || '',
      email,
      checkboxTermsAndConditions: doctorData.isChecked || false,
    };

    try {
      setIsLoading(true);

      await SetTemporaryPassword(newData);

      setIsPermanentPassword(true);
    } catch (error: any) {
      setServerError('somethingWrong');
    }
    setIsLoading(false);
  };

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();

    stageNumber <= stages.length - 3
      ? setStageNumber(stageNumber + 1)
      : stageNumber === stages.length - 1
        ? handleTemporaryPassword()
        : handleSignUp();
  };

  const handleClickBack = (event: React.MouseEvent) => {
    event.preventDefault();
    setStageNumber(stageNumber - 1);
  };

  const handleRedirect = () => {
    navigate('/doctor-logIn');
  };

  const handleLogout = () => {
    userAuth.logOut(userAuth.userData?.accessToken || '');

    navigate('/doctor-logIn', { replace: true });
  };

  useEffect(() => {
    setServerError(null);
  }, [doctorData]);

  return (
    <FullScreenWrapper>
      {!isTermsShown && !isSuccessSignUp && !isPermanentPassword && (
        <>
          <Header currentStage={stageNumber} stages={stages} />
          <CurrentBody
            userData={doctorData}
            setUserData={setDoctorData}
            onSubmit={handleSubmit}
            handleClickBack={handleClickBack}
            setIsTermsShown={setIsTermsShown}
            serverError={serverError}
            isLoading={isLoading}
          />
        </>
      )}
      {(isSuccessSignUp || isPermanentPassword) && (
        <SuccessSignUp
          title={isSuccessSignUp ? 'The doctor has been successfully added' : 'New password'}
          message={isSuccessSignUp ? emailConfirmationMessage : permanentPasswordMessage}
        >
          <Button
            label={isPermanentPassword ? 'Go to log in' : 'Log out'}
            buttonSize="m"
            onClick={isPermanentPassword ? handleRedirect : handleLogout}
          />
        </SuccessSignUp>
      )}
      {isTermsShown && <TermsAndConditions setIsTermsShown={setIsTermsShown} />}
    </FullScreenWrapper>
  );
};

export default RegistrationPageDoctor;

export type DoctorData = {
  role: string;
  name: string | null;
  surname: string | null;
  birthday: Date | null;
  email: string | null;
  phone: string | null;
  specialization: string | null;
  licenseNumber: string | null;
  password: string | null;
  repeatedPassword: string | null;
  isChecked: boolean;
};

