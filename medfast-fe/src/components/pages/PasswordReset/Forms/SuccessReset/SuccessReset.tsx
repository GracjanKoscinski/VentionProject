import React from 'react';
import { useNavigate } from 'react-router-dom';

import {
  Label,
  Button,
  Header,
  Form,
  ScreenWrapperCentred,
  RoundImageBackground,
} from '@/components/common';
import { Wrapper } from '../styles';

import Icon from '@/components/Icons';

const SuccessReset = () => {
  const navigate = useNavigate();

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();

    navigate('/logIn');
  };

  return (
    <Wrapper>
      <Header />
      <ScreenWrapperCentred>
        <Form isBordered={false} onSubmit={(event: React.FormEvent) => handleSubmit(event)}>
          <RoundImageBackground $backgroundColor="success" $borderColor="white">
            <Icon type="success" />
          </RoundImageBackground>
          <Label
            label="Password reset"
            fontWeight={700}
            fontSize="l"
            lineHeight="30px"
            color="darkGrey"
            margin="12px 0 12px"
          />
          <Label
            label="Your password has been successfully reset. Click below to log in"
            fontWeight={400}
            fontSize="s"
            lineHeight="22px"
            color="darkGrey"
            margin="0 0 24px"
          />
          <Button label="Continue" buttonSize="m" />
        </Form>
      </ScreenWrapperCentred>
    </Wrapper>
  );
};

export default SuccessReset;
