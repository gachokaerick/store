import dayjs from 'dayjs';
import { IAddress } from 'app/shared/model/address.model';
import { IOrderItem } from 'app/shared/model/order-item.model';
import { IPayment } from 'app/shared/model/payment.model';
import { IBuyer } from 'app/shared/model/buyer.model';
import { OrderStatus } from 'app/shared/model/enumerations/order-status.model';

export interface IOrder {
  id?: number;
  orderDate?: string;
  orderStatus?: OrderStatus;
  description?: string | null;
  address?: IAddress;
  orderItems?: IOrderItem[] | null;
  payments?: IPayment[] | null;
  buyer?: IBuyer;
}

export const defaultValue: Readonly<IOrder> = {};
