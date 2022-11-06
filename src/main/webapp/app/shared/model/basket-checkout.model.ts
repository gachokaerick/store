import dayjs from 'dayjs';

export interface IBasketCheckout {
  id?: number;
  street?: string | null;
  city?: string;
  town?: string;
  country?: string;
  zipcode?: string | null;
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
  userLogin?: string;
  description?: string | null;
}

export const defaultValue: Readonly<IBasketCheckout> = {};
