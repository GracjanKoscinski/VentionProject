import { dataFetch } from './dataFetch';

export const DoctorSignUp = async ({ adminToken, doctorData }: Props) => {
  const response = await dataFetch(`${process.env.API_HOST}/api/admin-console/registerDoctor`, {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${adminToken}`,
    },
    method: 'POST',
    body: JSON.stringify(doctorData),
  });

  if (response.status === 200) {
    return response;
  } else {
    throw new Error(response.status);
  }
};

type Props = {
  adminToken: string;
  doctorData: {
    email: string;
    name: string;
    surname: string;
    birthDate: string;
    phone: string;
    specializationIds: number[];
    locationId: number;
    licenseNumber: string;
  };
};
