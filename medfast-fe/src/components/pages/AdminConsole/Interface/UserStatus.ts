export type UserStatus = 'ACTIVE' | 'WAITING_FOR_CONFIRMATION' | 'DEACTIVATED' | 'DELETED';

export const mapUserStatusToLabel = (status: UserStatus): string => {
    switch (status) {
        case 'WAITING_FOR_CONFIRMATION':
        return 'Waiting for Confirmation';
        case 'DEACTIVATED':
        return 'Deactivated';
        case 'DELETED':
        return 'Deleted';
        default:
        return 'Active'; 
    }
};