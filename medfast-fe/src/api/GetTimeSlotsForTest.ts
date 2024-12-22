import { dataFetch } from './dataFetch';

export const GetTimeSlotsForTestForTest = async (
  token: string,
  testId: number,
  month: number,
  year: number,
) => {
  const response = await dataFetch(
    `${process.env.API_HOST}/api/patient/test-appointments/available-timeslots?testId=${testId}&month=${month}&year=${year}`,
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
