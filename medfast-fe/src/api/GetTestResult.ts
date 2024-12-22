import { HttpError } from './HttpError';

export const GetTestResult = async (token: string, testId: number) => {
  const url = `${process.env.API_HOST}/api/patient/test-appointments/result?testId=${testId}`;

  const response = await fetch(url, {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) {
    const errorData = await response.json();
    const errorMessage = errorData.errorMessage || 'An unknown error occurred.';
    throw new HttpError(errorData.status, errorMessage);
  }

  const blob = await response.blob();
  const downloadUrl = window.URL.createObjectURL(blob);
  const contentDisposition = response.headers.get('Content-Disposition');
  let filename = 'test-result.pdf';

  if (contentDisposition) {
    const match = contentDisposition.match(/filename="(.+?)"$/);
    if (match && match[1]) {
      filename = match[1];
    }
    filename = filename.trim().replace(/[_\s]+$/, '');
  }
  const link = document.createElement('a');
  link.href = downloadUrl;
  link.setAttribute('download', filename);
  document.body.appendChild(link);
  link.click();
  window.URL.revokeObjectURL(downloadUrl);
  document.body.removeChild(link);
};
