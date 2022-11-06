export interface IBasketItem {
  id?: number;
  productId?: number;
  productName?: string;
  unitPrice?: number;
  oldUnitPrice?: number;
  quantity?: number;
  pictureUrl?: string;
  userLogin?: string;
}

export const defaultValue: Readonly<IBasketItem> = {};
