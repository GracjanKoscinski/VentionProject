import { dataFetch } from './dataFetch';

export const UserEmailVerification = async (code: string) => {
  const response = await dataFetch(`${process.env.API_HOST}/auth/verify${code}`, {
    headers: {
      'Content-Type': 'application/json',
    },
    method: 'POST',
  });

  if (response.status === 200) {
    return response;
  } else {
    throw new Error(response.status);
  }
};
