import { dataFetch } from './dataFetch';

export const PasswordResetNewPassword = async (newData: Props) => {
  const response = await dataFetch(`${process.env.API_HOST}/auth/password/reset`, {
    headers: {
      'Content-Type': 'application/json',
    },
    method: 'POST',
    body: JSON.stringify(newData),
  });

  if (response.status === 200) {
    return response;
  } else {
    throw new Error(response.status);
  }
};

export type Props = {
  otp: string;
  newPassword: string;
  email: string;
};
