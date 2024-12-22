import { dataFetch } from './dataFetch';

export const SearchTests = async (token: string, keyword: string) => {
  const response = await dataFetch(
    `${process.env.API_HOST}/api/patient/tests?keyword=${keyword}`,
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
