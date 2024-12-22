import { dataFetch } from './dataFetch';

export const GetLoctionsForTest = async (token: string, testId: number) => {
  const response = await dataFetch(
    `${process.env.API_HOST}/api/patient/test-appointments/available-locations?testId=${testId}`,
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
