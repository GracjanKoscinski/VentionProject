import { dataFetch } from './dataFetch';

export const CreateOnlineAppointment = async (visitData: VisitData, token: string) => {
  const response = await dataFetch(`${process.env.API_HOST}/api/appointments`, {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    method: 'POST',
    body: JSON.stringify(visitData),
  });

  if (response.status === 200) {
    return response;
  } else {
    throw new Error(response.status);
  }
};

type VisitData = {
  doctorId: number;
  serviceId: number;
  dateFrom: string;
};
