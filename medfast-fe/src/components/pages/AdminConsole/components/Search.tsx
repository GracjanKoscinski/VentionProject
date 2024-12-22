import React from 'react';
import { Wrapper } from '../headerStyles';
import { Input } from '@/components/common';
import Icon from '@/components/Icons';

type Props = {
  searchTerm: string;
  setSearchTerm: (value: string) => void;
};

const Search = ({ searchTerm, setSearchTerm }: Props) => {
  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value;
    setSearchTerm(value);
  };

  return (
    <Wrapper>
      <Input
        name="search"
        type="text"
        placeholder="Find a doctor"
        value={searchTerm}
        onChange={handleInputChange}
      />
      <Icon type="search" />
    </Wrapper>
  );
};

export default Search;

