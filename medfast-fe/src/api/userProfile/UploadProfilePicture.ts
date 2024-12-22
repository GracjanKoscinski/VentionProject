import { HttpError } from '../HttpError';

export const UploadProfilePicture = async (token: string, file: File) => {
  const url = `${process.env.API_HOST}/api/user/profile/photo/upload`;
  const formData = new FormData();
  formData.append('photo', file, file.name);

  const response = await fetch(url, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`,
    },
    body: formData,
  });

  if (!response.ok) {
    const errorData = await response.json();
    const errorMessage = errorData.errorMessage || 'An unknown error occurred.';
    throw new HttpError(response.status, errorMessage);
  }
  return await response.json();
};
