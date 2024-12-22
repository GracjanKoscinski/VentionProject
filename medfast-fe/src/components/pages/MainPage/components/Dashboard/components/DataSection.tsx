import React, { useEffect, useState } from 'react';

import { Label } from '@/components/common';
import { SectionTitle, DataSectionWrapper, Line, Dot, TabletWrapper, InfoWrapper } from './styles';

import Icon from '@/components/Icons';

type Props = {
  title: string;
  children: JSX.Element | JSX.Element[];
  onOpen?: () => void;
  onClose?: () => void;
};

const DataSection = ({ title, children, onOpen, onClose }: Props) => {
  const [isOpen, setIsOpen] = useState(false);

  const handleDropdown = () => {
    if (isOpen && onClose) onClose();
    if (!isOpen && onOpen) onOpen();
    setIsOpen(!isOpen);
  };

  return (
    <DataSectionWrapper>
      {title && (
        <SectionTitle onClick={handleDropdown}>
          <Dot $size="8px" $backgroundColor="purple" />
          <Label
            label={title}
            fontWeight={600}
            fontSize="m"
            lineHeight="22px"
            color="darkGrey"
            margin="0 14px 0 16px"
          />
          <Icon type={isOpen ? 'closeArrow' : 'openArrow'} />
        </SectionTitle>
      )}
      <TabletWrapper>
        <Line />
        {isOpen && <InfoWrapper>{children}</InfoWrapper>}
      </TabletWrapper>
    </DataSectionWrapper>
  );
};

export default DataSection;

