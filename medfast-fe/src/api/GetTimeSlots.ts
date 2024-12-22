import { dataFetch } from './dataFetch';

export const GetTimeSlots = async (doctorId: number, serviceId: number, chosenDate: Date) => {
  const response = await dataFetch(
    `${process.env.API_HOST}/api/doctors/${doctorId}/appointments/occupied?serviceId=${serviceId}&month=${chosenDate.month}&year=${chosenDate.year}`,
    {
      headers: {
        'Content-Type': 'application/json',
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

type Date = {
  day: number;
  month: number;
  year: number;
};
