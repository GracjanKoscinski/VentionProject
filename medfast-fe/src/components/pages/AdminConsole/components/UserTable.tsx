import React, { useEffect, useState } from 'react';
import {
  Table,
  TableRow,
  TableHeader,
  TableCell,
  PaginationWrapper,
  FlexTableCell,
  Icon,
  StatusCell,
  LimitChange,
} from './tableStyles';
import { IDoctor } from '../Interface/IDoctor';
import { mapUserStatusToLabel } from '../Interface/UserStatus';
import activeIcon from './StatusIcons/green_dot.png';
import waitingIcon from './StatusIcons/yellow_dot.png';
import deactivatedIcon from './StatusIcons/gray_dot.png';
import deletedIcon from './StatusIcons/red_dot.png';
import ArrowBack from '@/components/Icons';
import ArrowNext from '@/components/Icons';
import DropdownMenu from './DropdownMenu';
import { useUser } from '@/utils/UserContext';
import {
  EMAIL_SORT_KEY,
  SPECIALIZATION_SORT_KEY,
  STATUS_ACTIVE,
  STATUS_DEACTIVATED,
  STATUS_SORT_KEY,
  STATUS_WAITING_FOR_CONFIRMATION,
  SURNAME_SORT_KEY,
} from '../Interface/constants';
import DropDownMenu from './DropdownMenu';

type Props = {
  doctors: IDoctor[];
  onSortBy: (sortField: string, page: number, amountRows: number) => void;
  page: number;
  setPage: React.Dispatch<React.SetStateAction<number>>;
  amountRows: number;
  setAmountRows: React.Dispatch<React.SetStateAction<number>>;
  totalAmountOfDoctors: number;
};

const UserTable = ({
  doctors,
  onSortBy,
  page,
  setPage,
  amountRows,
  setAmountRows,
  totalAmountOfDoctors,
}: Props) => {
  const userAuth = useUser();
  const indexOfLastDoctor = page * amountRows;
  const indexOfFirstDoctor = indexOfLastDoctor - amountRows;
  const options = Array.from({ length: 10 }, (_, i) => (i + 1) * 10);

  const handleLimitChange = (limit: string) => {
    setAmountRows(parseInt(limit));
    setPage(1);
  };

  const handleNextPage = () => {
    if (page < Math.ceil(totalAmountOfDoctors / amountRows)) {
      setPage(page + 1);
    }
  };

  const handlePreviousPage = () => {
    if (page > 1) {
      setPage(page - 1);
    }
  };

  return (
    <>
      <Table>
        <thead>
          <TableRow>
            <TableHeader onClick={() => onSortBy(SURNAME_SORT_KEY, page, amountRows)}>
              Name Surname
            </TableHeader>
            <TableHeader onClick={() => onSortBy(EMAIL_SORT_KEY, page, amountRows)}>
              Email
            </TableHeader>
            <TableHeader onClick={() => onSortBy(SPECIALIZATION_SORT_KEY, page, amountRows)}>
              Specializations
            </TableHeader>
            <TableHeader onClick={() => onSortBy(STATUS_SORT_KEY, page, amountRows)}>
              Status
            </TableHeader>
          </TableRow>
        </thead>
        <tbody>
          {doctors.map((doctor) => (
            <TableRow key={doctor.email}>
              <TableCell style={{ fontWeight: 'bold' }}>{doctor.name}</TableCell>
              <TableCell>{doctor.email}</TableCell>
              <TableCell>
                {doctor.specializations.map((specialization, index) => (
                  <span key={index} style={{ fontSize: '16px' }}>
                    {specialization}
                    <br />
                  </span>
                ))}
              </TableCell>
              <FlexTableCell>
                <StatusCell>
                  <Icon
                    src={
                      doctor.status === STATUS_ACTIVE
                        ? activeIcon
                        : doctor.status === STATUS_WAITING_FOR_CONFIRMATION
                          ? waitingIcon
                          : doctor.status === STATUS_DEACTIVATED
                            ? deactivatedIcon
                            : deletedIcon
                    }
                  />
                  {mapUserStatusToLabel(doctor.status)}
                </StatusCell>
                <DropDownMenu
                  userStatus={doctor.status}
                  doctorEmail={doctor.email}
                  userAuthToken={userAuth.userData?.accessToken || ''}
                />
              </FlexTableCell>
            </TableRow>
          ))}
        </tbody>
      </Table>

      <PaginationWrapper>
        <p>
          Showing {doctors.length} results out of {totalAmountOfDoctors}
        </p>
        <LimitChange>
          <div>
            <label htmlFor="limit">Rows per page:</label>
            <select
              id="limit"
              onChange={(e) => handleLimitChange(e.target.value)}
              value={amountRows}
            >
              {options.map((value) => (
                <option key={value} value={value}>
                  {value}
                </option>
              ))}
            </select>
          </div>
          <p>
            {indexOfFirstDoctor + 1} - {Math.min(indexOfLastDoctor, totalAmountOfDoctors)} of{' '}
            {totalAmountOfDoctors}
          </p>
          <button onClick={handlePreviousPage} disabled={page === 1}>
            <ArrowBack type="arrowBack" />
          </button>
          <button
            onClick={handleNextPage}
            disabled={page >= Math.ceil(totalAmountOfDoctors / amountRows)}
          >
            <ArrowNext type="arrowNext" />
          </button>
        </LimitChange>
      </PaginationWrapper>
    </>
  );
};

export default UserTable;

