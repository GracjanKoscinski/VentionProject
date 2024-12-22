import { dataFetch } from './dataFetch';

export const SetTemporaryPassword = async (newData: Props) => {
  const response = await dataFetch(
    `${process.env.API_HOST}/api/doctor/settings/password/setPermanentPassword`,
    {
      headers: {
        'Content-Type': 'application/json',
      },
      method: 'PUT',
      body: JSON.stringify(newData),
    },
  );

  if (response.status === 200) {
    return response;
  } else {
    throw new Error(response.status);
  }
};

type Props = {
  code: string;
  newPassword: string;
  email: string;
  checkboxTermsAndConditions: boolean;
};
