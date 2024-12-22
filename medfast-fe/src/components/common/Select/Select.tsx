import React, { useRef, useState, useEffect } from 'react';
import { Arrow, Wrapper, Option, OptionsWrapper, HighlightedOptionsWrapper } from './styles';
import Input from '@/components/common/Input/Input';

type Props = {
  name: string;
  options: string[];
  highlightedOptions?: string[];
  placeholder: string;
  handleChange: (option: string, event: React.MouseEvent) => void;
};

const Select = ({ name, options, highlightedOptions, placeholder, handleChange }: Props) => {
  const [optionsToDisplay, setOptionsToDisplay] = useState(options);
  const [highlightedOptionsToDisplay, setHighlightedOptionsToDisplay] =
    useState(highlightedOptions);
  const [isFocused, setIsFocused] = useState(false);
  const [value, setValue] = useState('');
  const ref = useRef<HTMLDivElement>(null);

  const handleClick = () => {
    setIsFocused(!isFocused);
  };

  const handleSelect = (option: string, event: React.MouseEvent) => {
    event.stopPropagation();

    setValue(option);
    setIsFocused(false);
    handleChange(option, event);
  };

  const handleSort = (valueToFind: string) => {
    const regex = new RegExp(valueToFind, 'i');

    const sortedOptions = options.filter((option) => option.match(regex));
    const sortedHighlightedOptions = highlightedOptions?.filter((option) => option.match(regex));

    setOptionsToDisplay(sortedOptions);
    setHighlightedOptionsToDisplay(sortedHighlightedOptions);
  };

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (ref.current && !ref.current.contains(event.target as Node)) {
        setIsFocused(false);
      }
    };
    document.addEventListener('click', handleClickOutside);

    return () => document.removeEventListener('click', handleClickOutside);
  }, []);

  return (
    <>
      <Wrapper onClick={handleClick} ref={ref}>
        <Input
          onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
            handleSort(event.target.value);
            setValue(event.target.value);
          }}
          type="text"
          label=""
          placeholder={placeholder}
          name={name}
          value={value}
        />
        <Arrow $isFocused={isFocused} />
        {isFocused && (
          <OptionsWrapper>
            <HighlightedOptionsWrapper>
              {highlightedOptionsToDisplay?.map((option) => (
                <Option
                  key={option}
                  onClick={(event: React.MouseEvent) => handleSelect(option, event)}
                >
                  {option}
                </Option>
              ))}
            </HighlightedOptionsWrapper>
            {optionsToDisplay.map((option) => (
              <Option
                key={option}
                onClick={(event: React.MouseEvent) => handleSelect(option, event)}
              >
                {option}
              </Option>
            ))}
          </OptionsWrapper>
        )}
      </Wrapper>
    </>
  );
};

export default Select;

