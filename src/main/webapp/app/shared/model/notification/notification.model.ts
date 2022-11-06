import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { NotificationType } from 'app/shared/model/enumerations/notification-type.model';

export interface INotification {
  id?: number;
  date?: string;
  details?: string;
  sentDate?: string | null;
  format?: NotificationType;
  user?: IUser;
}

export const defaultValue: Readonly<INotification> = {};
