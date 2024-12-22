import { dataFetch } from './dataFetch';

export const PasswordResetConfirmationCode = async (email: string, token: string) => {
  const response = await dataFetch(
    `${process.env.API_HOST}/auth/otp/verify?email=${email}&token=${token}`,
    {
      headers: {
        'Content-Type': 'application/json',
      },
      method: 'POST',
    },
  );

  if (response.status === 200) {
    return response;
  } else {
    throw new Error(response.status);
  }
};
