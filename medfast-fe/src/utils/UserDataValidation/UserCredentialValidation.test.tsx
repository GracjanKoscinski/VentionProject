import UserDataValidation from './UserCredentialValidation';
import '@testing-library/jest-dom';

describe('validation tests', () => {
  //required
  it('required field function with not valid data', () => {
    const answer = UserDataValidation().requiredField('');

    expect(answer).toEqual('required');
  });

  it('required field function with valid data', () => {
    const answer = UserDataValidation().requiredField('Hello, World!');

    expect(answer).toEqual(false);
  });

  //length
  it('length function with not valid data', () => {
    const answer = UserDataValidation().length('Hello, World', 5, 10);

    expect(answer).toEqual('length');
  });

  it('length function with not valid data', () => {
    const answer = UserDataValidation().length('Hey', 5, 10);

    expect(answer).toEqual('length');
  });

  it('length function with valid data', () => {
    const answer = UserDataValidation().length('Hello, World', 10, 20);

    expect(answer).toEqual(false);
  });

  //date format
  it('allowed chars "dateFormat" function with not valid data', () => {
    const answer = UserDataValidation().dateFormat('23.12.20');

    expect(answer).toEqual('dateFormat');
  });

  it('allowed chars "dateFormat" function with not valid data', () => {
    const answer = UserDataValidation().dateFormat('23 Dec 2020');

    expect(answer).toEqual(false);
  });

  it('allowed chars "dateFormat" function with valid data', () => {
    const answer = UserDataValidation().dateFormat('12/23/2020');

    expect(answer).toEqual(false);
  });

  //age limit
  it('allowed chars "ageLimit" function with not valid data', () => {
    const answer = UserDataValidation().ageLimit('12/23/2020');

    expect(answer).toEqual('ageLimit');
  });

  it('allowed chars "ageLimit" function with not valid data', () => {
    const answer = UserDataValidation().ageLimit('12/23/1900');

    expect(answer).toEqual('ageLimit');
  });

  it('allowed chars "ageLimit" function with valid data', () => {
    const answer = UserDataValidation().ageLimit('12/23/2000');

    expect(answer).toEqual(false);
  });

  //allowed chars
  it('allowed chars "noSpecOrNum" function with not valid data', () => {
    const answer = UserDataValidation().allowedChar('Hello, World', 'noSpecOrNum');

    expect(answer).toEqual('allowedChars');
  });

  it('allowed chars "noSpecOrNum" function with not valid data', () => {
    const answer = UserDataValidation().allowedChar('Hello1234', 'noSpecOrNum');

    expect(answer).toEqual('allowedChars');
  });

  it('allowed chars "noSpecOrNum" function with valid data', () => {
    const answer = UserDataValidation().allowedChar('Hello', 'noSpecOrNum');

    expect(answer).toEqual(false);
  });

  it('allowed chars "latin" function with not valid data', () => {
    const answer = UserDataValidation().allowedChar('Привет', 'latin');

    expect(answer).toEqual('allowedChars');
  });

  it('allowed chars "latin" function with valid data', () => {
    const answer = UserDataValidation().allowedChar('Hello, World', 'latin');

    expect(answer).toEqual(false);
  });

  it('allowed chars "alphanumeric" function with not valid data', () => {
    const answer = UserDataValidation().allowedChar('Hello, World!', 'alphanumeric');

    expect(answer).toEqual('allowedChars');
  });

  it('allowed chars "alphanumeric" function with valid data', () => {
    const answer = UserDataValidation().allowedChar('Hello123', 'alphanumeric');

    expect(answer).toEqual(false);
  });

  it('allowed chars "alphanumeric" function with valid data', () => {
    const answer = UserDataValidation().allowedChar('Hello', 'alphanumeric');

    expect(answer).toEqual(false);
  });

  it('allowed chars "noNum" function with not valid data', () => {
    const answer = UserDataValidation().allowedChar('Hello1234', 'noNum');

    expect(answer).toEqual('allowedChars');
  });

  it('allowed chars "noNum" function with valid data', () => {
    const answer = UserDataValidation().allowedChar('Hello', 'noNum');

    expect(answer).toEqual(false);
  });

  it('allowed chars "onlyNum" function with not valid data', () => {
    const answer = UserDataValidation().allowedChar('Hello!', 'onlyNum');

    expect(answer).toEqual('allowedChars');
  });

  it('allowed chars "onlyNum" function with valid data', () => {
    const answer = UserDataValidation().allowedChar('1234', 'onlyNum');

    expect(answer).toEqual(false);
  });

  it('allowed chars "email" function with not valid data', () => {
    const answer = UserDataValidation().allowedChar('Hello', 'email');

    expect(answer).toEqual('allowedChars');
  });

  it('allowed chars "email" function with not valid data', () => {
    const answer = UserDataValidation().allowedChar('@mail.com', 'email');

    expect(answer).toEqual('allowedChars');
  });

  it('allowed chars "email" function with valid data', () => {
    const answer = UserDataValidation().allowedChar('test@gmail.com', 'email');

    expect(answer).toEqual(false);
  });

  it('allowed chars "phone" function with not valid data', () => {
    const answer = UserDataValidation().allowedChar('+123456789', 'phone');

    expect(answer).toEqual('allowedChars');
  });

  it('allowed chars "phone" function with valid data', () => {
    const answer = UserDataValidation().allowedChar('+1 (234) 567 8901', 'phone');

    expect(answer).toEqual(false);
  });

  it('allowed chars "password" function with not valid data', () => {
    const answer = UserDataValidation().allowedChar('qwerty', 'password');

    expect(answer).toEqual('allowedChars');
  });

  it('allowed chars "password" function with valid data', () => {
    const answer = UserDataValidation().allowedChar('Qw!2', 'password');

    expect(answer).toEqual(false);
  });
});
