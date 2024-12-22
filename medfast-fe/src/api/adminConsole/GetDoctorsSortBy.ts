import { dataFetch } from '../dataFetch';

export const GetDoctorsSortBy = async (token: string, page: number, amount: number, sortBy: string) => {
  const response = await dataFetch(
    `${process.env.API_HOST}/api/admin-console/get-doctors?page=${page}&amount=${amount}&sort-by=${sortBy}`,
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
