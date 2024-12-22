import { dataFetch } from './dataFetch';

export const UserLogIn = async ({ userData }: UserDataType) => {
  const response = await dataFetch(`${process.env.API_HOST}/auth/signin`, {
    headers: {
      'Content-Type': 'application/json',
    },
    method: 'POST',
    body: JSON.stringify(userData),
  });

  if (response.status === 200) {
    return response;
  } else {
    throw new Error(response.status.toString());
  }
};

export type UserDataType = {
  userData: {
    email: string;
    password: string;
  };
};
