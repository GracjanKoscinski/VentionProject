import { dataFetch } from './dataFetch';

export const GetVisits = async (token: string, type: 'UPCOMING' | 'PAST') => {
  const response = await dataFetch(
    `${process.env.API_HOST}/api/patient/appointments?type=${type}`,
    {
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      method: 'GET',
    },
  );

  if (response.status === 200) {
    return response;
  } else {
    throw new Error(response.status);
  }
};
