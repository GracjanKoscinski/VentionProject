import { dataFetch } from './dataFetch';

type ElementSelection = 'FIRST' | 'REMAINING';
type ReferralType = 'PAST' | 'UPCOMING';

export const GetReferrals = async (
  token: string,
  elementSelection: ElementSelection,
  referralType: ReferralType,
) => {
  const response = await dataFetch(
    `${process.env.API_HOST}/api/patient/referrals?elementSelection=${elementSelection}&referralType=${referralType}`,
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

