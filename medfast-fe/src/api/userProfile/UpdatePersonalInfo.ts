import { dataFetch } from '../dataFetch';
import { PersonalInfoType } from '../../components/pages/MainPage/components/Profile/components/Interface/PersonalInfoType';

export const UpdatePersonalInfo = async (token: string, personal: PersonalInfoType) => {
  const response = await dataFetch(`${process.env.API_HOST}/api/user/profile/personal-info`, {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    method: 'PUT',
    body: JSON.stringify(personal),
  });

  if (response.status === 200) {
    return response;
  } else {
    throw new Error(response.status);
  }
};
