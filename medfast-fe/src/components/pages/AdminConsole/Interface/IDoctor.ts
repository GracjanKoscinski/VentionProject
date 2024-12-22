import { UserStatus } from "./UserStatus";
export interface IDoctor {
    name: string;
    email: string;
    specializations: string[];
    status: UserStatus;
  }