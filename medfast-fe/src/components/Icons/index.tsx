import React, { FC } from 'react';
import LogoIcon from './resources/LogoIcon.svg';
import Logo from './resources/Logo.svg';
import CheckMarkIcon from './resources/CheckMarkIcon.svg';
import CalendarIcon from './resources/CalendarIcon.svg';
import PasswordVisible from './resources/PasswordVisible.svg';
import PasswordInvisible from './resources/PasswordInvisible.svg';
import ArrowBack from './resources/ArrowBack.svg';
import ArrowNext from './resources/ArrowNext.svg';
import CapsLockIcon from './resources/CapsLockIcon.svg';
import StarIcon from './resources/StarIcon.svg';
import SuccessIcon from './resources/SuccessIcon.svg';
import ErrorIcon from './resources/ErrorIcon.svg';
import WarningIcon from './resources/WarningIcon.svg';
import KeyIcon from './resources/KeyIcon.svg';
import UnreadMessageIcon from './resources/UnreadMessageIcon.svg';
import RedoIcon from './resources/RedoIcon.svg';
import Messenger from './resources/Messenger.svg';
import NotificationBell from './resources/NotificationBell.svg';
import Search from './resources/Search.svg';
import CalendarGrey from './resources/CalendarGrey.svg';
import Card from './resources/Card.svg';
import CareTeam from './resources/CareTeam.svg';
import Home from './resources/HomeIcon.svg';
import LogOut from './resources/LogOutIcon.svg';
import Settings from './resources/Settings.svg';
import SideBarClose from './resources/SideBarClose.svg';
import SideBarOpen from './resources/SideBarOpen.svg';
import PrescriptionsIcon from './resources/PrescriptionsIcon.svg';
import TestsIcon from './resources/TestsIcon.svg';
import VisitsIcon from './resources/VisitsIcon.svg';
import OpenArrow from './resources/OpenArrow.svg';
import CloseArrow from './resources/CloseArrow.svg';
import DoctorImage from './resources/DoctorImage.svg';
import Clock from './resources/ClockIcon.svg';
import Location from './resources/LocationIcon.svg';
import Needle from './resources/Needle.svg';
import Tests from './resources/Tests.svg';
import Cross from './resources/Cross.svg';
import OnSiteVisit from './resources/OnSiteVisit.svg';
import OnlineVisit from './resources/OnlineVisit.svg';
import TestVisit from './resources/TestVisit.svg';
import TimeAndDate from './resources/TimeAndDate.svg';
import Stethoscope from './resources/Stethoscope.svg';
import Person from './resources/Person.svg';
import NewAppointmentError from './resources/NewAppointmentError.svg';
import NewAppointmentCreated from './resources/NewAppointmentCreated.svg';

const ICONS = {
  newAppointmentError: <NewAppointmentError />,
  newAppointmentCreated: <NewAppointmentCreated />,
  logoIcon: <LogoIcon />,
  logo: <Logo />,
  checkMarkIcon: <CheckMarkIcon />,
  calendarIcon: <CalendarIcon />,
  passwordInvisible: <PasswordInvisible />,
  passwordVisible: <PasswordVisible />,
  arrowBack: <ArrowBack />,
  arrowNext: <ArrowNext />,
  capsLockIcon: <CapsLockIcon />,
  starIcon: <StarIcon />,
  success: <SuccessIcon />,
  warning: <WarningIcon />,
  error: <ErrorIcon />,
  keyIcon: <KeyIcon />,
  unreadMessageIcon: <UnreadMessageIcon />,
  redoIcon: <RedoIcon />,
  notificationBell: <NotificationBell />,
  messenger: <Messenger />,
  search: <Search />,
  calendarGrey: <CalendarGrey />,
  card: <Card />,
  careTeam: <CareTeam />,
  home: <Home />,
  logOut: <LogOut />,
  settings: <Settings />,
  sideBarClose: <SideBarClose />,
  sideBarOpen: <SideBarOpen />,
  visitsIcon: <VisitsIcon />,
  testsIcon: <TestsIcon />,
  prescriptionsIcon: <PrescriptionsIcon />,
  closeArrow: <CloseArrow />,
  openArrow: <OpenArrow />,
  doctorImage: <DoctorImage />,
  clock: <Clock />,
  location: <Location />,
  needle: <Needle />,
  tests: <Tests />,
  cross: <Cross />,
  onSiteVisit: <OnSiteVisit />,
  onlineVisit: <OnlineVisit />,
  testVisit: <TestVisit />,
  person: <Person />,
  stethoscope: <Stethoscope />,
  timeAndDate: <TimeAndDate />,
};

export type IconType = keyof typeof ICONS;

type Props = {
  type: IconType;
};

const Icon: FC<Props> = (props) => {
  const { type } = props;
  return ICONS[type];
};

export default Icon;

