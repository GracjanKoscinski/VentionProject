import { dataFetch } from '../dataFetch';
export const SearchDoctors = async (token: string, page: number, amount: number, sortBy:string, filterBy: string,  keyWord: string)  => {
  const response = await dataFetch(
    `${process.env.API_HOST}/api/admin-console/search-doctors?page=${page}&amount=${amount}&sort-by=${sortBy}&filter-by=${filterBy}&keyword=${keyWord}&page=${page}&amount=${amount}`,
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
