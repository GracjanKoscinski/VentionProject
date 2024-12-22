export const UserLogOut = async (token: string) => {
  const response = await fetch(`${process.env.API_HOST}/auth/logout`, {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    method: 'POST',
  });

  if (response.status === 200) {
    return response;
  } else {
    throw new Error(response.status.toString());
  }
};
