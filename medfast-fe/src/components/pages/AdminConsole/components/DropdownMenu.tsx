import React, { useEffect, useRef, useState } from 'react';
import Settings from '@/components/Icons';
import { DorpDownMenu } from './tableStyles';
import { UserStatus } from '@/components/pages/AdminConsole/Interface/UserStatus';
import { ActivateUser } from '@/api/adminConsole/ActivateUser';
import { DeactivateUser } from '@/api/adminConsole/DeactivateUser';
import ServerResponsePopUp from '@/components/common/ServerResponsePopUp/ServerResponsePopUp';
import { PopUpWithContent } from '@/components/common';
import { DeleteUser } from '@/api/adminConsole/DeleteUser';
import { STATUS_ACTIVE, STATUS_DEACTIVATED, STATUS_DELETED } from '../Interface/constants';

type Props = {
  userStatus: UserStatus;
  doctorEmail: string;
  userAuthToken: string;
};

const DropDownMenu = ({ userStatus, doctorEmail, userAuthToken }: Props) => {
  const [menuVisible, setMenuVisible] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showConfirm, setShowConfirm] = useState(false);
  const [actionToConfirm, setActionToConfirm] = useState<string | null>(null);
  const wrapperRef = useRef<HTMLDivElement>(null);

  const handleClickOutside = (event: MouseEvent) => {
    if (wrapperRef.current && !wrapperRef.current.contains(event.target as Node)) {
      setMenuVisible(false);
    }
  };

  useEffect(() => {
    document.addEventListener('click', handleClickOutside);
    return () => {
      document.removeEventListener('click', handleClickOutside);
    };
  }, []);

  const toggleMenu = () => {
    setMenuVisible(!menuVisible);
  };
  const handleOptionClick = (action: string) => {
    setActionToConfirm(action);
    if (action !== 'view' && action !== 'edit') {
      setShowConfirm(true);
    }
    setMenuVisible(false);
  };

  const confirmAction = async () => {
    try {
      switch (actionToConfirm) {
        case 'deactivate':
          await DeactivateUser(userAuthToken, doctorEmail);
          break;
        case 'delete':
          await DeleteUser(userAuthToken, doctorEmail);
          break;
        case 'activate':
          await ActivateUser(userAuthToken, doctorEmail);
          break;
        default:
          break;
      }
      setShowConfirm(false);
      window.location.reload();
    } catch (err: any) {
      setError('somethingWrong');
    }
  };

  const cancelAction = () => {
    setShowConfirm(false);
  };

  return (
    <div ref={wrapperRef}>
      <DorpDownMenu>
        <div onClick={toggleMenu} style={{ cursor: 'pointer' }}>
          <Settings type="settings" />
        </div>

        {menuVisible && (
          <ul>
            <li onClick={() => handleOptionClick('view')}>View Details</li>
            {userStatus !== STATUS_DELETED && (
              <li onClick={() => handleOptionClick('edit')}>Edit</li>
            )}
            {userStatus === STATUS_DEACTIVATED && (
              <li onClick={() => handleOptionClick('activate')}>Reactivate</li>
            )}
            {(userStatus === STATUS_ACTIVE || userStatus === 'WAITING_FOR_CONFIRMATION') && (
              <li onClick={() => handleOptionClick('deactivate')}>Deactivate</li>
            )}
            {userStatus !== STATUS_DELETED && (
              <li onClick={() => handleOptionClick('delete')}>Delete</li>
            )}
          </ul>
        )}
        {showConfirm && (
          <PopUpWithContent
            title="Confirm Action"
            message={`Are you sure you want to ${actionToConfirm} this user: ${doctorEmail}?`}
            confirmButton="Confirm"
            cancelButton="Cancel"
            confirmMethod={confirmAction}
            cancelMethod={cancelAction}
          />
        )}
        <ServerResponsePopUp serverResponse={error} onClick={() => setError(null)} />
      </DorpDownMenu>
    </div>
  );
};

export default DropDownMenu;
