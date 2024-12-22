import React from 'react';
import { Oval } from 'react-loader-spinner';

import { LoaderWrapper } from './styles';

type Props = {
  size?: string;
  color?: string;
};

const Loader = ({ size, color }: Props) => {
  return (
    <LoaderWrapper>
      <Oval
        visible={true}
        height={size ? size : '80'}
        width={size ? size : '80'}
        color={color ? color : '#8E68F3'}
        secondaryColor="#E4F3FF"
        ariaLabel="oval-loading"
      />
    </LoaderWrapper>
  );
};

export default Loader;
