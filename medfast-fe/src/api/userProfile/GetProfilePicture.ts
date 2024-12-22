import { HttpError } from '../HttpError';

export const GetProfilePicture = async (token: string) => {
  const url = `${process.env.API_HOST}/api/user/profile/photo/get`;

  const response = await fetch(url, {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) {
    const errorData = await response.json();
    const errorMessage = errorData.errorMessage || 'An unknown error occurred.';
    throw new HttpError(errorData.status, errorMessage);
  }
  const blob = await response.blob();
  return blob;
};
