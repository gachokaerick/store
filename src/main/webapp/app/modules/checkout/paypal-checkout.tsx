import React from 'react';
import { PayPalButtons } from '@paypal/react-paypal-js';
import { ICatalogItem } from 'app/shared/model/catalog/catalog-item.model';
import { OrderResponseBody, PurchaseItem } from '@paypal/paypal-js/types/apis/orders';
import { IPayment } from 'app/shared/model/orders/payment.model';
import { OrderStatus } from 'app/shared/model/enumerations/order-status.model';
import { IOrder } from 'app/shared/model/orders/order.model';
import { IAddress } from 'app/shared/model/orders/address.model';
import { IOrderItem } from 'app/shared/model/orders/order-item.model';

export const PaypalCheckout = props => {
  const catalogItems: ReadonlyArray<ICatalogItem> = props.catalogItems;
  const currencyCode = 'USD';
  const selectedAddress: Readonly<IAddress> = props.selectedAddress;

  const checkoutItems: PurchaseItem[] = catalogItems.map(item => ({
    name: `${item.name}`,
    quantity: `${props.getItemFromCartById(item.id).quantity}`,
    category: 'DIGITAL_GOODS',
    unit_amount: {
      currency_code: currencyCode,
      value: `${item.price}`,
    },
  }));

  const orderItems: IOrderItem[] = catalogItems?.map(item => ({
    productName: item.name,
    pictureUrl: item.pictureUrl,
    unitPrice: item.price,
    discount: 0,
    units: props.getItemFromCartById(item.id).quantity,
    productId: item.id,
  }));

  const makePayment = (paymentDetails: OrderResponseBody) => {
    props.submitOrder(createOrderEntity(paymentDetails));
  };

  const createOrderEntity = (paymentDetails: OrderResponseBody): IOrder => {
    return {
      orderDate: paymentDetails.create_time,
      orderStatus: OrderStatus.DRAFT,
      description: paymentDetails.id + ': ' + paymentDetails.status,
      address: {
        ...selectedAddress,
      },
      orderItems,
      payments: mapPayments(paymentDetails),
      buyer: selectedAddress.buyer,
    };
  };

  const mapPayments = (paymentDetails: OrderResponseBody): IPayment[] => {
    let payments: IPayment[] = [];
    paymentDetails.purchase_units.forEach(unit => {
      const mapped: IPayment[] = unit.payments.captures.map(capture => ({
        createTime: `${capture.create_time}`,
        updateTime: `${capture.update_time}`,
        paymentStatus: `${capture.status}`,
        payerCountryCode: paymentDetails.payer.address.country_code,
        payerEmail: paymentDetails.payer.email_address,
        payerName: paymentDetails.payer.name.given_name,
        payerSurname: paymentDetails.payer.name.surname,
        payerId: paymentDetails.payer.payer_id,
        currency: `${(capture.amount as any).currency_code}`,
        amount: Number((capture.amount as any).value),
        paymentId: `${capture.id}`,
      }));
      payments = [...payments, ...mapped];
    });
    return payments;
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
