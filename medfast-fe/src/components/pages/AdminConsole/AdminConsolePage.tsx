import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ContentWrapper, TitleWrapper } from './styles';
import Header from './Header';
import UserTable from './components/UserTable';
import { useUser } from '@/utils/UserContext';
import { GetDoctorsSortBy } from '@/api/adminConsole/GetDoctorsSortBy';
import { SearchDoctors } from '@/api/adminConsole/SearchDoctors';
import { IDoctor } from './Interface/IDoctor';
import ServerResponse, { ERROR_MESSAGES } from '@/components/common/ServerResponse/ServerResponse';
import { Loader, PopUpWithContent } from '@/components/common';
import { SURNAME_SORT_KEY } from './Interface/constants';


type Doctors = IDoctor[];

const AdminConsole = () => {
  const userAuth = useUser();
  const navigate = useNavigate();

  const [doctors, setDoctors] = useState<Doctors>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<keyof typeof ERROR_MESSAGES | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [searchBy, setSearchBy] = useState(SURNAME_SORT_KEY);
  const [sortBy, setSortBy] = useState(SURNAME_SORT_KEY);
  const [isLogOut, setIsLogOut] = useState(false);
  const [serverResponse, setServerResponse] = useState<'somethingWrong' | null>(null);
  const [page, setPage] = useState(1);
  const [amountRows, setAmountRows] = useState(10);
  const [totalAmountOfDoctors, setTotalAmountOfDoctors] = useState(0);

  const handleLogOut = async () => {
    try {
      await userAuth.logOut(userAuth.userData?.accessToken || 'null');
      navigate('/logIn', { replace: true });
    } catch (error: any) {
      setServerResponse('somethingWrong');
      setIsLogOut(false);
    }
  };

  useEffect(() => {
    const timeOutId = setTimeout(() => setServerResponse(null), 5000);
    return () => clearTimeout(timeOutId);
  }, [serverResponse]);

  const handleSortBy = async (sortField: string, page: number, amountRows: number) => {
    setLoading(true);
    setError(null);
    setSortBy(sortField);
    try {
      let response;
      if (searchTerm.trim() !== '') {
        response = await SearchDoctors(
          userAuth.userData?.accessToken || '',
          page - 1,
          amountRows,
          sortField,
          searchBy,
          searchTerm,
        );
      } else {
        response = await GetDoctorsSortBy(
          userAuth.userData?.accessToken || '',
          page - 1,
          amountRows,
          sortField,
        );
      }
      setDoctors(response.data.doctors);
      setTotalAmountOfDoctors(response.data.totalAmount);
    } catch (err: any) {
      setError('somethingWrong');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (page: number, amountRows: number) => {
    setLoading(true);
    setError(null);
    try {
      let response = await SearchDoctors(
        userAuth.userData?.accessToken || '',
        page - 1,
        amountRows,
        sortBy,
        searchBy,
        searchTerm,
      );
      setDoctors(response.data.doctors);
      setTotalAmountOfDoctors(response.data.totalAmount);
    } catch (err: any) {
      setError('somethingWrong');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (searchTerm.trim() === '') {
      handleSortBy(SURNAME_SORT_KEY, page, amountRows);
    } else {
      handleSearch(page, amountRows);
    }
  }, [searchTerm, searchBy, page, amountRows]);

  return (
    <ContentWrapper>
      <Header
        searchTerm={searchTerm}
        setSearchTerm={setSearchTerm}
        setIsLogOut={setIsLogOut}
        searchBy={searchBy}
        setSearchBy={setSearchBy}
      />
      <TitleWrapper>
        <h1>Admin Console</h1>
      </TitleWrapper>
      {error && (
        <ServerResponse serverError={error} hasCross={true} onClick={() => setError(null)} />
      )}
      {isLogOut && (
        <PopUpWithContent
          title="Log out?"
          message="Are you sure you want to log out from your Medfast account?"
          confirmButton="Log out"
          cancelButton="Cancel"
          cancelMethod={() => setIsLogOut(false)}
          confirmMethod={handleLogOut}
        />
      )}
      {loading && <Loader size="80" color="#8E68F3" />}

      {!loading && !error && doctors.length > 0 && (
        <UserTable
          doctors={doctors}
          onSortBy={handleSortBy}
          page={page}
          setPage={setPage}
          amountRows={amountRows}
          setAmountRows={setAmountRows}
          totalAmountOfDoctors={totalAmountOfDoctors}
        />
      )}
    </ContentWrapper>
  );
};

export default AdminConsole;
