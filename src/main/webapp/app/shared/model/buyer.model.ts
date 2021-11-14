import { IUser } from 'app/shared/model/user.model';
import { IAddress } from 'app/shared/model/address.model';
import { IOrder } from 'app/shared/model/order.model';
import { Gender } from 'app/shared/model/enumerations/gender.model';

export interface IBuyer {
  id?: number;
  firstName?: string;
  lastName?: string;
  gender?: Gender;
  email?: string;
  phone?: string;
  user?: IUser;
  addresses?: IAddress[] | null;
  orders?: IOrder[] | null;
}

export const defaultValue: Readonly<IBuyer> = {};
