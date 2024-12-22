import { AddressInfoType } from './AddressInfoType';
import { ContactInfoType } from './ContatctInfoType';
import { PersonalInfoType } from './PersonalInfoType';

export type UserInfoType = {
  personalInfo: PersonalInfoType;
  addressInfo: AddressInfoType;
  contactInfo: ContactInfoType;
};
