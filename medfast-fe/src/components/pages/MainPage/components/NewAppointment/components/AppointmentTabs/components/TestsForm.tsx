import React, { useState, useEffect } from 'react';

import {
  Divider,
  RoundImageBackground,
  Label,
  Input,
  BackToWithArrow,
  Button,
  Loader,
} from '@/components/common';
import { TitleWrapper, FormWrapper, InputWrapper, ServicesWrapper } from './styles';
import { WrapperWithShadow } from '../../styles';
import NoResults from './NoResults';

import Icon from '@/components/Icons';

import { useUser } from '@/utils/UserContext';

import { TestAppointment } from '../TestAppointment';
import { SearchTests } from '@/api/SearchTests';
import Test from './Test';
import { useDebounce } from 'use-debounce';

type Props = {
  visitInfo: TestAppointment;
  setVisitInfo: (visitInfo: TestAppointment) => void;
  handleError: (serverResponse: 'somethingWrong') => void;
  handleBack: () => void;
};

type TestType = {
  id: number;
  test: string;
};

type ChosenTestType = {
  id: number | null;
  test: string | null;
};

const TestsForm = ({ visitInfo, setVisitInfo, handleError, handleBack }: Props) => {
  const userAuth = useUser();
  const [inputValue, setInputValue] = useState('');
  const [testsToDisplay, setTestsToDisplay] = useState<TestType[]>([]);
  const [chosenTest, setChosenTest] = useState<ChosenTestType | null>({
    test: null,
    id: null,
  });
  const [activeTest, setActiveTest] = useState<number | null>(null);
  const [isNoTests, setIsNoTests] = useState(false);
  const [isLoadingTests, setIsLoadingTests] = useState<boolean>(false);
  const [debouncedInputValue] = useDebounce(inputValue, 300);

  const handleClick = (testName: string, testId: number) => {
    setChosenTest({ test: testName, id: testId });
    setActiveTest(testId);
  };

  const handleConfirm = () => {
    setVisitInfo({
      ...visitInfo,
      test: {
        name: chosenTest?.test || '',
        id: chosenTest?.id || 0,
      },
    });
    handleBack();
  };

  const handleTests = async (keyword = '') => {
    const token = userAuth.userData?.accessToken || '';

    try {
      setIsLoadingTests(true);
      const response = await SearchTests(token, keyword);
      const testsData = response.data || [];

      if (testsData.length === 0) {
        setIsNoTests(true);
        setTestsToDisplay([]);
      } else {
        setIsNoTests(false);
        setTestsToDisplay(testsData);
      }
    } catch (error: any) {
      handleError('somethingWrong');
      setTestsToDisplay([]);
    } finally {
      setIsLoadingTests(false);
    }
  };

  useEffect(() => {
    handleTests();
  }, []);

  useEffect(() => {
    handleTests(debouncedInputValue);
  }, [debouncedInputValue]);

  const handleSearch = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value;
    setInputValue(value);
  };

  return (
    <>
      <FormWrapper>
        <BackToWithArrow label="Back" onClick={handleBack} />
        <TitleWrapper>
          <RoundImageBackground $backgroundColor="lightBlue" $borderColor="lightBlue">
            <Icon type="testsIcon" />
          </RoundImageBackground>
          <Label
            label="Test"
            fontWeight={700}
            fontSize="l"
            lineHeight="24px"
            color="darkGrey"
            margin="0 0 0 24px"
          />
        </TitleWrapper>
        <Divider $height="2px" $color="purple" />
        <Label
          label="Please select a test from the available options"
          fontWeight={400}
          fontSize="s"
          lineHeight="22px"
          color="darkGrey"
          margin="24px 0 16px"
        />
        <WrapperWithShadow $flexDirection="column">
          <InputWrapper>
            <Input
              name="tests"
              type="text"
              value={inputValue}
              placeholder="Find a test"
              onChange={handleSearch}
            />
            <Icon type="search" />
          </InputWrapper>
          {isLoadingTests ? (
            <Loader />
          ) : isNoTests ? (
            <NoResults />
          ) : (
            <ServicesWrapper>
              {testsToDisplay.map((test) => (
                <Test
                  key={test.id}
                  test={test.test}
                  isActive={test.id === activeTest}
                  onClick={() => handleClick(test.test, test.id)}
                />
              ))}
            </ServicesWrapper>
          )}
        </WrapperWithShadow>
      </FormWrapper>
      <Button
        label="Confirm test"
        buttonSize="l"
        disabled={!chosenTest?.id}
        onClick={handleConfirm}
      />
    </>
  );
};

export default TestsForm;

