import { dataFetch } from '../dataFetch';

export const DeactivateUser = async (token: string, email: string) => {
  const response = await dataFetch(
    `${process.env.API_HOST}/api/admin-console/deactivate?user-email=${email}`,
    {
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      method: 'PUT',
    },
  );

  if (response.status === 200) {
    return response;
  } else {
    throw new Error(response.status);
  }
};
