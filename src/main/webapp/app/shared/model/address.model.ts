import { IBuyer } from 'app/shared/model/buyer.model';

export interface IAddress {
  id?: number;
  street?: string | null;
  city?: string;
  town?: string;
  country?: string;
  zipcode?: string | null;
  buyer?: IBuyer;
}

export const defaultValue: Readonly<IAddress> = {};
