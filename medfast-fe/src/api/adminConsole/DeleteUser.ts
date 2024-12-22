import { dataFetch } from '../dataFetch';

export const DeleteUser = async (token: string, email: string) => {
  const response = await dataFetch(
    `${process.env.API_HOST}/api/admin-console/delete?user-email=${email}`,
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
