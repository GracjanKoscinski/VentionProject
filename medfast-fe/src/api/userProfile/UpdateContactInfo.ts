import { ContactInfoType } from '../../components/pages/MainPage/components/Profile/components/Interface/ContatctInfoType';
import { dataFetch } from '../dataFetch';

export const UpdateContactInfo = async (token: string, contact: ContactInfoType) => {
  const response = await dataFetch(`${process.env.API_HOST}/api/user/profile/contact-info`, {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    method: 'PUT',
    body: JSON.stringify(contact),
  });

  if (response.status === 200) {
    return response;
  } else {
    throw new Error(response.status);
  }
};
