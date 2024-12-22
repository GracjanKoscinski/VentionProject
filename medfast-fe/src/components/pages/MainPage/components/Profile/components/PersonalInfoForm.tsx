import React, { useEffect, useState } from 'react';
import { Label, Input, Button, Form, Select, RadioButton } from '@/components/common';
import { UserInfoType } from './Interface/UserInfoType';
import { PersonalInfoType } from './Interface/PersonalInfoType';
import { ButtonWrapper, Wrapper } from './styles';
import { countries } from 'countries-list';
import ImageDisplay from './ProfilePicture';
import UserDataValidation from '@/utils/UserDataValidation/UserCredentialValidation';
import BirthDayCalendar from '@/components/common/Calendar/BirthDayCalendar';
import { useUser } from '@/utils/UserContext';
import { UpdatePersonalInfo } from '@/api/userProfile/UpdatePersonalInfo';

const ERROR_TEXT = {
  required: 'This field is required.',
  length: 'The length must be from 2 to 50 characters.',
  allowedChars: 'Only Latin letters allowed, no digits or special symbols.',
  dateFormat: 'Invalid date',
  ageLimit: 'Age limit from 18 to 110 years.',
  citizenship: 'Citizenship is required.',
};

type Errors = {
  name: string | null;
  surname: string | null;
  birthday: string | null;
  citizenship: string | null;
};

type Props = {
  userInfo: UserInfoType;
  setUserInfo: (info: UserInfoType) => void;
  setServerResponse: (serverResponse: 'somethingWrong' | 'hasBeenUpdated') => void;
};

const frequentlyUsedCountries = ['Canada', 'Mexico', 'United States'];
const allCountries = Object.values(countries).map((country) => country.name);
const otherCountries = allCountries.filter((country) => !frequentlyUsedCountries.includes(country));

const PersonalInfoForm = ({ userInfo, setUserInfo, setServerResponse }: Props) => {
  const [personalInfo, setPersonalInfo] = useState<PersonalInfoType | undefined>(
    userInfo?.personalInfo,
  );

  const userAuth = useUser();
  const [isEdit, setIsEdit] = useState<boolean>(false);
  const [formErrors, setFormErrors] = useState<Errors>({
    name: null,
    surname: null,
    birthday: null,
    citizenship: null,
  });

  const [isFormValid, setIsFormValid] = useState<boolean>(false);

  const validation = UserDataValidation();
  const sexOptions = ['Male', 'Female'];

  useEffect(() => {
    setPersonalInfo(userInfo?.personalInfo);
  }, [userInfo]);

  const validateField = (field: string, fieldName: keyof Errors): string | null => {
    if (fieldName === 'name' || fieldName === 'surname') {
      const validationResp = [
        validation.allowedChar(field, 'noSpecOrNum'),
        validation.length(field, 2, 50),
        validation.requiredField(field),
      ].filter(Boolean);

      const errorKey = validationResp.find((error) => typeof error === 'string');
      return errorKey ? ERROR_TEXT[errorKey as keyof typeof ERROR_TEXT] : null;
    }
    return null;
  };

  const validateBirthday = (value: string): string | null => {
    const validationResp = [
      validation.dateFormat(value),
      validation.ageLimit(value),
      validation.requiredField(value),
    ].filter(Boolean);

    const errorKey = validationResp.find((error) => typeof error === 'string');
    return errorKey ? ERROR_TEXT[errorKey as keyof typeof ERROR_TEXT] : null;
  };

  const validateForm = () => {
    const errors = {
      name: validateField(personalInfo?.name || '', 'name'),
      surname: validateField(personalInfo?.surname || '', 'surname'),
      birthday: personalInfo?.birthDate
        ? validateBirthday(personalInfo.birthDate.toString())
        : 'Birthday is required.',
      citizenship: personalInfo?.citizenship ? null : ERROR_TEXT.citizenship,
    };
    setFormErrors(errors);
    return !Object.values(errors).some((error) => error !== null);
  };

  useEffect(() => {
    setIsFormValid(validateForm());
  }, [personalInfo]);

  const handleSave = async () => {
    try {
      if (personalInfo) {
        await UpdatePersonalInfo(userAuth.userData?.accessToken || '', personalInfo);
        setUserInfo({ ...userInfo, personalInfo } as UserInfoType);
        setServerResponse('hasBeenUpdated');
        setIsEdit(false);
      } else {
        setServerResponse('somethingWrong');
      }
    } catch (error: any) {
      setServerResponse('somethingWrong');
    }
  };

  const handleCancel = () => {
    setIsEdit(false);
    setPersonalInfo(userInfo.personalInfo);
  };

  const handleSelectOption = (option: string) => {
    setPersonalInfo({ ...personalInfo, citizenship: option } as PersonalInfoType);
    setFormErrors({
      ...formErrors,
      citizenship: option ? option : ERROR_TEXT.citizenship,
    });
  };

  return (
    <Wrapper>
      <Form onSubmit={() => {}}>
        <ImageDisplay />
        <div style={isEdit ? {} : { pointerEvents: 'none', opacity: 0.7 }}>
          <Label
            label="Name"
            fontWeight={700}
            fontSize="s"
            lineHeight="30px"
            color="darkGrey"
            margin="0 0 0px"
          />
          <Input
            name="firstName"
            type="text"
            value={personalInfo?.name || ''}
            placeholder="Enter your first name"
            isInvalid={formErrors.name || ''}
            onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
              setPersonalInfo({ ...personalInfo, name: event.target.value } as PersonalInfoType);
              setFormErrors({ ...formErrors, name: validateField(event.target.value, 'name') });
            }}
          />
          <Label
            label="Surname"
            fontWeight={700}
            fontSize="s"
            lineHeight="30px"
            color="darkGrey"
            margin="0 0 0px"
          />
          <Input
            name="surName"
            type="text"
            value={personalInfo?.surname || ''}
            placeholder="Enter your surname"
            isInvalid={formErrors.surname || ''}
            onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
              setPersonalInfo({ ...personalInfo, surname: event.target.value } as PersonalInfoType);
              setFormErrors({
                ...formErrors,
                surname: validateField(event.target.value, 'surname'),
              });
            }}
          />
          <BirthDayCalendar
            error={formErrors.birthday}
            userInfo={userInfo}
            setPersonalInfo={setPersonalInfo}
          />
          <RadioButton
            label="Legal Sex"
            options={sexOptions}
            handleSubmit={(option: string) =>
              setPersonalInfo({
                ...personalInfo,
                sex: option,
              } as PersonalInfoType)
            }
          />
          <Label
            label="Citizenship"
            fontWeight={700}
            fontSize="s"
            lineHeight="30px"
            color="darkGrey"
            margin="0 0 0px"
          />
          <Select
            name="citizenship"
            highlightedOptions={frequentlyUsedCountries}
            options={otherCountries}
            placeholder={personalInfo?.citizenship || 'Choose from options'}
            handleChange={handleSelectOption}
          />
        </div>
      </Form>
      {isEdit ? (
        <ButtonWrapper>
          <Button onClick={handleSave} label="Save" buttonSize="s" disabled={!isFormValid} />
          <Button
            onClick={() => {
              handleCancel();
            }}
            label="Cancel"
            buttonSize="s"
          />
        </ButtonWrapper>
      ) : (
        <ButtonWrapper>
          <Button onClick={() => setIsEdit(true)} label="Edit" buttonSize="m" />
        </ButtonWrapper>
      )}
    </Wrapper>
  );
};

export default PersonalInfoForm;
