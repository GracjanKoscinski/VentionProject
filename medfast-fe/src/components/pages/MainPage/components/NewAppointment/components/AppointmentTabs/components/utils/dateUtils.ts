import { format } from 'date-fns';

interface DateObject {
  year: number | null;
  month: number | null;
  day: number | null;
  time: string | null;
}

export const formatDateToShow = (date: DateObject): string => {
  if (!date.year || !date.month || !date.day) return '';
  const formattedDate = format(new Date(date.year, date.month - 1, date.day), 'MMM-dd-yyyy');
  return `${formattedDate} ${date.time ? `| ${date.time}` : ''}`;
};
