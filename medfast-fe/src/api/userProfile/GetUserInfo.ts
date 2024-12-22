import { dataFetch } from '../dataFetch';

export const GetUserInfo = async (token: string) => {
  const response = await dataFetch(`${process.env.API_HOST}/api/user/profile/info`, {
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
