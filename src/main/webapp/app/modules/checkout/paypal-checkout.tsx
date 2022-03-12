import React from 'react';
import { PayPalButtons } from '@paypal/react-paypal-js';
import { ICatalogItem } from 'app/shared/model/catalog/catalog-item.model';
import { PurchaseItem } from '@paypal/paypal-js/types/apis/orders';

export const PaypalCheckout = props => {
  const catalogItems: ReadonlyArray<ICatalogItem> = props.catalogItems;

  const checkoutItems: PurchaseItem[] = catalogItems.map(item => ({
    name: `${item.name}`,
    quantity: `${props.getItemFromCartById(item.id).quantity}`,
    category: 'DIGITAL_GOODS',
    unit_amount: {
      currency_code: 'USD',
      value: `${item.price}`,
    },
  }));

  return (
    <PayPalButtons
      createOrder={(data, actions) => {
        return actions.order.create({
          purchase_units: [
            {
              amount: {
                currency_code: 'USD',
                value: `${props.checkoutTotal}`,
                breakdown: {
                  item_total: {
                    currency_code: 'USD',
                    value: `${props.checkoutTotal}`,
                  },
                },
              },
              items: checkoutItems,
            },
          ],
        });
      }}
      onApprove={(data, actions) => {
        return actions.order.capture().then(details => {
          // eslint-disable-next-line no-console
          console.log('details: ', details);
        });
      }}
    />
  );
};

export default PaypalCheckout;
