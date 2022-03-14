import React from 'react';
import { PayPalButtons } from '@paypal/react-paypal-js';
import { ICatalogItem } from 'app/shared/model/catalog/catalog-item.model';
import { OrderResponseBody, PurchaseItem } from '@paypal/paypal-js/types/apis/orders';
import { IPayment } from 'app/shared/model/orders/payment.model';
import { OrderStatus } from 'app/shared/model/enumerations/order-status.model';
import { IOrder } from 'app/shared/model/orders/order.model';

export const PaypalCheckout = props => {
  const catalogItems: ReadonlyArray<ICatalogItem> = props.catalogItems;
  const currencyCode = 'USD';

  const checkoutItems: PurchaseItem[] = catalogItems.map(item => ({
    name: `${item.name}`,
    quantity: `${props.getItemFromCartById(item.id).quantity}`,
    category: 'DIGITAL_GOODS',
    unit_amount: {
      currency_code: currencyCode,
      value: `${item.price}`,
    },
  }));

  const makePayment = (paymentDetails: OrderResponseBody) => {};

  const createOrderEntity = (paymentDetails: OrderResponseBody): IOrder => {
    // return {
    //   createTime: paymentDetails.create_time,
    //   updateTime: paymentDetails.update_time,
    //   paymentStatus: paymentDetails.status,
    //   payerCountryCode: paymentDetails.payer.address.country_code,
    //   payerEmail: paymentDetails.payer.email_address,
    //   payerName: paymentDetails.payer.name.given_name,
    //   payerSurname: paymentDetails.payer.name.surname,
    //   payerId: paymentDetails.payer.payer_id,
    //   currency: currencyCode,
    //   amount: calculatePaymentAmount(paymentDetails),
    //   paymentId: paymentDetails.id,
    //   order: {
    //     orderDate: paymentDetails.create_time,
    //     orderStatus: OrderStatus.DRAFT,
    //     description:
    //   }
    // };
    return {
      orderDate: paymentDetails.create_time,
      orderStatus: OrderStatus.DRAFT,
      description: paymentDetails.id + ': ' + paymentDetails.status,
      address: {
        // set address b4 paying for order
      },
      orderItems: [],
      payments: [],
      buyer: {},
    };
  };

  const calculatePaymentAmount = (paymentDetails: OrderResponseBody): number => {
    let total = 0;
    paymentDetails.purchase_units.forEach(
      unit =>
        (total += unit.payments.captures.reduce(
          (amount, capture) => (capture.status === 'COMPLETED' ? amount + Number((capture.amount as any).value) : 0),
          0
        ))
    );
    return total;
  };

  return (
    <PayPalButtons
      createOrder={(data, actions) => {
        return actions.order.create({
          purchase_units: [
            {
              amount: {
                currency_code: currencyCode,
                value: `${props.checkoutTotal}`,
                breakdown: {
                  item_total: {
                    currency_code: currencyCode,
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
          makePayment(details);
        });
      }}
    />
  );
};

export default PaypalCheckout;
