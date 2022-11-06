import { IOrder } from 'app/shared/model/order.model';

export interface IOrderItem {
  id?: number;
  productName?: string;
  pictureUrl?: string;
  unitPrice?: number;
  discount?: number;
  units?: number;
  productId?: number;
  order?: IOrder;
}

export const defaultValue: Readonly<IOrderItem> = {};
