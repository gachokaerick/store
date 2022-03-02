import { ICatalogBrand } from 'app/shared/model/catalog/catalog-brand.model';
import { ICatalogType } from 'app/shared/model/catalog/catalog-type.model';

export interface ICatalogItem {
  id?: number;
  name?: string;
  description?: string | null;
  price?: number;
  pictureFileName?: string | null;
  pictureUrl?: string | null;
  availableStock?: number;
  restockThreshold?: number;
  maxStockThreshold?: number;
  onReorder?: boolean | null;
  catalogBrand?: ICatalogBrand;
  catalogType?: ICatalogType;
  previousPrice?: number;
}

export const defaultValue: Readonly<ICatalogItem> = {
  onReorder: false,
};
