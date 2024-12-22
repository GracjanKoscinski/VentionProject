import React, { useEffect, useState } from 'react';
import {
  Label,
  Input,
  Button,
  Form,
  ServerResponse,
  PopUpWithContent,
} from '@/components/common';
import { UserInfoType } from './Interface/UserInfoType';
import { ContactInfoType } from './Interface/ContatctInfoType';
import { Wrapper, ButtonWrapper } from './styles';
import UserDataValidation, {
  REG_EXP,
} from '@/utils/UserDataValidation/UserCredentialValidation';
import { UpdateContactInfo } from '@/api/userProfile/UpdateContactInfo';
import { useUser } from '@/utils/UserContext';
import { useNavigate } from 'react-router-dom';
import { set } from 'date-fns';

const ERROR_TEXT = {
  email: 'Please enter a valid email address.',
  phone: 'Please enter a valid phone number in format xxx xxx xxxx.',
  required: 'This field is required.',
};

type Errors = {
  email: string | null;
  phone: string | null;
};

type Props = {
  userInfo: UserInfoType;
  setUserInfo: (info: UserInfoType) => void;
  setServerResponse: (serverResponse: 'somethingWrong' | 'hasBeenUpdated') => void;
};

const ContactInfoForm = ({ userInfo, setUserInfo, setServerResponse }: Props) => {
  const [contactInfo, setContactInfo] = useState<ContactInfoType | undefined>(
    userInfo?.contactInfo,
  );
  const [formErrors, setFormErrors] = useState<Errors>({
    email: null,
    phone: null,
  });
  const [isEdit, setIsEdit] = useState<boolean>(false);
  const [isEmailChanged, setIsEmailChanged] = useState<boolean>(false);
  const userAuth = useUser();
  const navigate = useNavigate();

  const [isFormValid, setIsFormValid] = useState<boolean>(false);

  const validation = UserDataValidation();

  useEffect(() => {
    setContactInfo(userInfo?.contactInfo);
  }, [userInfo]);

  const validateEmail = (email: string): string | null => {
    const validationResp = [
      validation.requiredField(email),
      REG_EXP.email.test(email) ? '' : 'email',
    ].filter(Boolean);

    const errorKey = validationResp.find((error) => typeof error === 'string');
    return errorKey ? ERROR_TEXT[errorKey as keyof typeof ERROR_TEXT] : null;
  };

  const validatePhone = (phone: string): string | null => {
    const validationResp = [
      validation.requiredField(phone),
      /^\d{11}$/.test(phone) ? '' : 'phone',
    ].filter(Boolean);

    const errorKey = validationResp.find((error) => typeof error === 'string');
    return errorKey ? ERROR_TEXT[errorKey as keyof typeof ERROR_TEXT] : null;
  };

  const validateForm = () => {
    const errors = {
      email: validateEmail(contactInfo?.email || ''),
      phone: validatePhone(contactInfo?.phone || ''),
    };
    setFormErrors(errors);
    return !Object.values(errors).some((error) => error !== null);
  };

  useEffect(() => {
    setIsFormValid(validateForm());
  }, [contactInfo]);

  const handleSaveEmail = async () => {
    try {
      if (contactInfo) {
        await UpdateContactInfo(userAuth.userData?.accessToken || '', contactInfo);
        setServerResponse('hasBeenUpdated');
        setIsEdit(false);
        setUserInfo({ ...userInfo, contactInfo } as UserInfoType);
        userAuth.userData = null;
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('role');
        localStorage.removeItem('terms');
        navigate('/logIn', { replace: true });
      }
    } catch (error: any) {
      setIsEmailChanged(false);
      setServerResponse('somethingWrong');
    }
  };

  const handleSave = async () => {
    try {
      if (contactInfo) {
        if (contactInfo.email !== userInfo.contactInfo?.email) {
          setIsEmailChanged(true);
        } else {
          await UpdateContactInfo(userAuth.userData?.accessToken || '', contactInfo);
          setServerResponse('hasBeenUpdated');
          setIsEdit(false);
          setUserInfo({ ...userInfo, contactInfo } as UserInfoType);
        }
      }
    } catch (error: any) {
      setServerResponse('somethingWrong');
    }
  };
  const handleLogOut = async () => {
    try {
      await userAuth.logOut(userAuth.userData?.accessToken || 'null');
      navigate('/logIn', { replace: true });
    } catch (error: any) {
      setServerResponse('somethingWrong');
    }
  };

  const handleCancel = () => {
    setIsEdit(false);
    setContactInfo(userInfo.contactInfo);
  };

  return (
    <Wrapper>
      <Form onSubmit={() => {}}>
        <div style={isEdit ? {} : { pointerEvents: 'none', opacity: 0.7 }}>
          <Input
            name="phone"
            label="Phone"
            type="tel"
            value={contactInfo?.phone || ''}
            isInvalid={formErrors.phone || ''}
            placeholder="Enter your phone number"
            onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
              const updatedPhone = event.target.value;
              setContactInfo({ ...contactInfo, phone: updatedPhone } as ContactInfoType);
              setFormErrors({ ...formErrors, phone: validatePhone(updatedPhone) });
            }}
          />
          <Input
            name="email"
            label="Email"
            type="email"
            value={contactInfo?.email || ''}
            isInvalid={formErrors.email || ''}
            placeholder="Enter your email"
            onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
              const updatedEmail = event.target.value;
              setContactInfo({ ...contactInfo, email: updatedEmail } as ContactInfoType);
              setFormErrors({ ...formErrors, email: validateEmail(updatedEmail) });
            }}
          />
          {isEmailChanged && (
            <PopUpWithContent
              title="Change email"
              message="Are you sure you want to change your email? You will be logged out and asked to confirm your new email."
              confirmButton="Change"
              cancelButton="Cancel"
              cancelMethod={() => {
                setIsEmailChanged(false);
                setIsEdit(false);
              }}
              confirmMethod={() => {
                handleSaveEmail();
              }}
            />
          )}
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

export default ContactInfoForm;
