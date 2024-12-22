// import { render, screen, waitFor } from '@testing-library/react';
// import userEvent from '@testing-library/user-event';
// import '@testing-library/jest-dom';

// import RegistrationPage from './RegistrationPage';
// import Theme from '../../Theme/Theme';

// const mockUsedNavigate = jest.fn();
// jest.mock('react-router-dom', () => ({
//   ...jest.requireActual('react-router-dom'),
//   useNavigate: () => mockUsedNavigate,
// }));

// jest.mock('./Forms/PersonalDataForm');
// jest.mock('./Forms/AddressForm/AddressForm');

// test('Registration page workflow', async () => {
//   const PageToTest = () => (
//     <Theme>
//       <RegistrationPage />
//     </Theme>
//   );

//   render(<PageToTest />);

//   const nameInput = screen.getByPlaceholderText('Your name');
//   const surnameInput = screen.getByPlaceholderText('Your surname');
//   const ageInput = screen.getByPlaceholderText('MM/DD/YYYY');
//   const nextButton = screen.getByText('Next');

//   expect(nextButton).toBeDisabled();

//   await userEvent.type(nameInput, 'John');
//   await userEvent.type(surnameInput, 'Doe');
//   await userEvent.type(ageInput, '04/12/1998');

//   expect(nameInput).toHaveValue('John');
//   expect(surnameInput).toHaveValue('Doe');
//   expect(ageInput).toHaveValue('04/12/1998');

//   expect(nextButton).toHaveAttribute('disabled={false}', true);
//   userEvent.click(nextButton);

//   await waitFor(() => screen.getByText('Address')), { timeout: 3000 };
// });
