import { dataFetch } from './dataFetch';

export const ReverifyUserEmail = async (email: string) => {
  const response = await dataFetch(`${process.env.API_HOST}/auth/reverify?email=${email}`, {
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
