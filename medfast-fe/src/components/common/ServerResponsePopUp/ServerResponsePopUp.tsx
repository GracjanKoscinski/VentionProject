import React, { useState, useEffect } from 'react';
import { createPortal } from 'react-dom';

import { Wrapper } from './styles';

import ServerResponse from '@/components/common/ServerResponse/ServerResponse';
import { ERROR_MESSAGES } from '@/components/common/ServerResponse/ServerResponse';

type Props = {
  serverResponse: keyof typeof ERROR_MESSAGES | null;
  onClick?: () => void;
};

const ServerResponsePopUp = ({ serverResponse, onClick }: Props) => {
  const [response, setResponse] = useState<keyof typeof ERROR_MESSAGES | null>(null);
  const [hasError, setHasError] = useState(false);

  useEffect(() => {
    let timeOutId = null;

    if (serverResponse !== null) {
      setResponse(serverResponse);
      setHasError(true);

      timeOutId = setTimeout(() => setHasError(false), 5000);
    }

    if (timeOutId) () => clearTimeout(timeOutId);
  }, [serverResponse]);

  return (
    hasError &&
    createPortal(
      <Wrapper $serverResponse={!!serverResponse}>
        {response && <ServerResponse serverError={response} hasCross={true} onClick={onClick} />}
      </Wrapper>,
      document.body,
    )
  );
};

export default ServerResponsePopUp;

