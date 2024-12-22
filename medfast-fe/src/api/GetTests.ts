import { dataFetch } from './dataFetch';

export const GetTests = async (token: string, type: 'UPCOMING' | 'PAST', amount: number) => {
  const response = await dataFetch(`${process.env.API_HOST}/api/patient/test-appointments?type=${type}&amount=${amount}`, {
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
