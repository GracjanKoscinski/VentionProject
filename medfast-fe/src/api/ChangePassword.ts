import { dataFetch } from './dataFetch';

export const ChangePassword = async (token: string, userData: Props) => {
  const response = await dataFetch(`${process.env.API_HOST}/api/patient/settings/password/change`, {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    method: 'POST',
    body: JSON.stringify(userData),
  });

  if (response.status === 200) {
    return response;
  } else {
    throw new Error(response.status);
  }
};

type Props = {
  currentPassword: string;
  newPassword: string;
};
