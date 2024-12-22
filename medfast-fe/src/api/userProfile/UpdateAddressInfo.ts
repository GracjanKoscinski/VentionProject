import { AddressInfoType } from '../../components/pages/MainPage/components/Profile/components/Interface/AddressInfoType';
import { dataFetch } from '../dataFetch';

export const UpdateAddressInfo = async (token: string, address: AddressInfoType) => {
  const response = await dataFetch(`${process.env.API_HOST}/api/user/profile/address-info`, {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    method: 'PUT',
    body: JSON.stringify(address),
  });

  if (response.status === 200) {
    return response;
  } else {
    throw new Error(response.status);
  }
};
