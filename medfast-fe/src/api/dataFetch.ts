export const dataFetch = async (url: string | URL | globalThis.Request, options?: RequestInit) => {
  try {
    const response = await fetch(url, options);

    const result = await response.json();

    if (result.status === 401) {
      const refreshToken = JSON.parse(localStorage.getItem('refreshToken') || 'null');

      try {
        const response = await fetch(`${process.env.API_HOST}/auth/refresh`, {
          headers: {
            'Content-Type': 'application/json',
          },
          method: 'POST',
          body: JSON.stringify(refreshToken),
        });

        const result = await response.json();

        localStorage.setItem('accessToken', JSON.stringify(result.data.accessToken));
        localStorage.setItem('refreshToken', JSON.stringify(result.data.refreshToken));
      } catch (error: any) {
        throw new Error(error);
      }
    }

    return result;
  } catch (error: any) {
    throw new Error(error);
  }
};
