import { dataFetch } from './dataFetch';

export const GetServices = async (token: string) => {
  const response = await dataFetch(`${process.env.API_HOST}/api/services`, {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    method: 'GET',
  });

  if (response.status === 200) {
    return response;
  } else {
    throw new Error(response.status);
  }
};
