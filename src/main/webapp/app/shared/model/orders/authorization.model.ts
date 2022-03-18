import dayjs from 'dayjs';
import { IOrder } from 'app/shared/model/orders/order.model';
import { PaymentProvider } from 'app/shared/model/enumerations/payment-provider.model';

export interface IAuthorization {
  id?: number;
  status?: string;
  authId?: string;
  currencyCode?: string;
  amount?: number;
  expirationTime?: string;
  paymentProvider?: PaymentProvider;
  order?: IOrder;
}

export const defaultValue: Readonly<IAuthorization> = {};
