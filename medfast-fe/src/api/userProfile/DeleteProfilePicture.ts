import { dataFetch } from '../dataFetch';

export const DeleteProfilePicture = async (token: string) => {
  const response = await dataFetch(`${process.env.API_HOST}/api/user/profile/photo/delete`, {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    method: 'DELETE',
  });

  if (response.status === 200) {
    return response;
  } else {
    throw new Error(response.status);
  }
};
