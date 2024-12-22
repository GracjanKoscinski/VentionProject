import React, { useEffect, useState } from 'react';
import { Label, Input, Button, Form } from '@/components/common';
import { UserInfoType } from './Interface/UserInfoType';
import { AddressInfoType } from './Interface/AddressInfoType';
import { Wrapper, ButtonWrapper } from './styles';
import UserDataValidation, {
  REG_EXP,
} from '@/utils/UserDataValidation/UserCredentialValidation';
import { UpdateAddressInfo } from '@/api/userProfile/UpdateAddressInfo';
import { useUser } from '@/utils/UserContext';

const ERROR_TEXT = {
  street: 'Please enter a valid street name',
  house: 'Please enter a valid house number',
  apartment: 'Please enter a valid apartment number',
  city: 'Please enter a valid city name',
  state: 'Please enter a valid state name',
  ZIP: 'Please enter a valid ZIP',
};

type Errors = {
  street: null | string;
  house: null | string;
  apartment: null | string;
  city: null | string;
  state: null | string;
  ZIP: null | string;
};

type Props = {
  userInfo: UserInfoType;
  setUserInfo: (info: UserInfoType) => void;
  setServerResponse: (serverResponse: 'somethingWrong' | 'hasBeenUpdated') => void;
};

const AddressInfoForm = ({ userInfo, setUserInfo, setServerResponse }: Props) => {
  const initialAddressInfo: AddressInfoType = {
    streetAddress: '',
    city: '',
    house: '',
    state: '',
    apartment: '',
    zip: '',
  };
  const userAuth = useUser();

  const [addressInfo, setAddressInfo] = useState<AddressInfoType | undefined>(
    userInfo?.addressInfo || initialAddressInfo,
  );
  const [isEdit, setIsEdit] = useState<boolean>(false);

  const [formErrors, setFormErrors] = useState<Errors>({
    street: null,
    house: null,
    apartment: null,
    city: null,
    state: null,
    ZIP: null,
  });

  const isValid =
    !Object.values(formErrors).some((error) => error !== '') &&
    Object.values(addressInfo || initialAddressInfo).every((value) => value !== '');

  const validation = (
    data: string,
    inputName: keyof typeof formErrors,
    start: number,
    end: number,
    validChars: keyof typeof REG_EXP,
  ) => {
    const validationResp = [
      UserDataValidation().requiredField(data),
      UserDataValidation().allowedChar(data, validChars),
      UserDataValidation().length(data, start, end),
    ].filter(Boolean);

    const errors = validationResp.filter((error) => typeof error === 'string');
    return errors.length === 0 ? '' : ERROR_TEXT[inputName];
  };

  useEffect(() => {
    const validationErrors = {
      street: addressInfo?.streetAddress
        ? validation(addressInfo.streetAddress, 'street', 2, 50, 'latin')
        : '',
      house: addressInfo?.house ? validation(addressInfo.house, 'house', 1, 20, 'latin') : '',
      apartment: addressInfo?.apartment
        ? validation(addressInfo.apartment, 'apartment', 1, 20, 'alphanumeric')
        : '',
      city: addressInfo?.city ? validation(addressInfo.city, 'city', 1, 20, 'noNum') : '',
      state: addressInfo?.state ? validation(addressInfo.state, 'state', 2, 50, 'noNum') : '',
      ZIP: addressInfo?.zip ? validation(addressInfo.zip, 'ZIP', 5, 5, 'onlyNum') : '',
    };
    setFormErrors(validationErrors);
  }, [addressInfo]);

  const handleSave = async () => {
    setServerResponse('hasBeenUpdated');
    try {
      if (addressInfo) {
        await UpdateAddressInfo(userAuth.userData?.accessToken || '', addressInfo);
        setUserInfo({ ...userInfo, addressInfo } as UserInfoType);
        setServerResponse('hasBeenUpdated');
        setIsEdit(false);
      }
    } catch (err: any) {
      setServerResponse('somethingWrong');
    }
  };
  const handleCancel = () => {
    setIsEdit(false);
    setAddressInfo(userInfo.addressInfo);
  };

  return (
    <Wrapper>
      <Form onSubmit={() => {}}>
        <div style={isEdit ? {} : { pointerEvents: 'none', opacity: 0.7 }}>
          <Input
            name="street"
            type="text"
            placeholder="Your street address"
            isInvalid={formErrors.street || ''}
            value={addressInfo?.streetAddress || ''}
            label="Street Address"
            onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
              setAddressInfo({
                ...addressInfo,
                streetAddress: event.target.value,
              } as AddressInfoType);
            }}
          />
          <Input
            name="city"
            type="text"
            placeholder="Your city"
            isInvalid={formErrors.city || ''}
            value={addressInfo?.city || ''}
            label="City"
            onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
              setAddressInfo({ ...addressInfo, city: event.target.value } as AddressInfoType);
            }}
          />
          <Input
            name="house"
            type="text"
            placeholder="Your house"
            value={addressInfo?.house || ''}
            isInvalid={formErrors.house || ''}
            label="House"
            onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
              setAddressInfo({ ...addressInfo, house: event.target.value } as AddressInfoType);
            }}
          />
          <Input
            name="state"
            type="text"
            placeholder="Your state"
            value={addressInfo?.state || ''}
            isInvalid={formErrors.state || ''}
            label="State"
            onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
              setAddressInfo({ ...addressInfo, state: event.target.value } as AddressInfoType);
            }}
          />
          <Input
            name="apartment"
            type="text"
            placeholder="Your apartment"
            value={addressInfo?.apartment || ''}
            isInvalid={formErrors.apartment || ''}
            label="Apartment"
            onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
              setAddressInfo({ ...addressInfo, apartment: event.target.value } as AddressInfoType);
            }}
          />
          <Input
            name="ZIP"
            type="text"
            placeholder="******"
            value={addressInfo?.zip || ''}
            isInvalid={formErrors.ZIP || ''}
            label="ZIP"
            onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
              setAddressInfo({ ...addressInfo, zip: event.target.value } as AddressInfoType);
            }}
          />
        </div>
      </Form>
      {isEdit ? (
        <ButtonWrapper>
          <Button onClick={handleSave} label="Save" buttonSize="s" disabled={!isValid} />
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

export default AddressInfoForm;
