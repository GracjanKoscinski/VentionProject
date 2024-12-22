import { dataFetch } from './dataFetch';

export const UserSignUp = async ({ userData }: Props) => {
  const response = await dataFetch(`${process.env.API_HOST}/auth/signup`, {
    headers: {
      'Content-Type': 'application/json',
    },
    method: 'POST',
    body: JSON.stringify(userData),
  });

  if (response.status === 200) {
    return response;
  } else {
    throw new Error(response.status);
  }
};

export type Props = {
  userData: {
    email: string;
    password: string;
    name: string;
    surname: string;
    birthDate: string;
    streetAddress: string;
    house: string;
    apartment: string;
    city: string;
    state: string;
    zip: string;
    phone: string;
    sex: string;
    citizenship: string;
    checkboxTermsAndConditions: boolean;
  };
};
