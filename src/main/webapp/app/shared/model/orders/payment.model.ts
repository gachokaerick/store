import dayjs from 'dayjs';
import { IOrder } from 'app/shared/model/orders/order.model';

export interface IPayment {
  id?: number;
  createTime?: string;
  updateTime?: string;
  paymentStatus?: string | null;
  payerCountryCode?: string | null;
  payerEmail?: string | null;
  payerName?: string;
  payerSurname?: string | null;
  payerId?: string;
  currency?: string;
  amount?: number;
  paymentId?: string | null;
  order?: IOrder;
}

export const defaultValue: Readonly<IPayment> = {};
