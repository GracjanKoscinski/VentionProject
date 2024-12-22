import { dataFetch } from './dataFetch';

export const GetDoctorsForService = async (token: string, serviceId: number) => {
  const response = await dataFetch(`${process.env.API_HOST}/api/doctors/service/${serviceId}`, {
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
