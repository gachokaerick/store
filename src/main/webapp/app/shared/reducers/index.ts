import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';

import locale from './locale';
import authentication from './authentication';
import applicationProfile from './application-profile';

import administration from 'app/modules/administration/administration.reducer';
import userManagement from './user-management';
// prettier-ignore
import address from 'app/entities/orders/address/address.reducer';
// prettier-ignore
import payment from 'app/entities/orders/payment/payment.reducer';
// prettier-ignore
import orderItem from 'app/entities/orders/order-item/order-item.reducer';
// prettier-ignore
import buyer from 'app/entities/orders/buyer/buyer.reducer';
// prettier-ignore
import order from 'app/entities/orders/order/order.reducer';
// prettier-ignore
import cart from 'app/modules/cart/cart.reducer';
// prettier-ignore
import authorization from 'app/entities/orders/authorization/authorization.reducer';
// prettier-ignore
import notification from 'app/entities/orders/notification/notification.reducer';
// prettier-ignore
import catalogType from 'app/entities/orders/catalog-type/catalog-type.reducer';
// prettier-ignore
import catalogItem from 'app/entities/orders/catalog-item/catalog-item.reducer';
// prettier-ignore
import catalogBrand from 'app/entities/orders/catalog-brand/catalog-brand.reducer';
// prettier-ignore
import basketItem from 'app/entities/orders/basket-item/basket-item.reducer';
// prettier-ignore
import basketCheckout from 'app/entities/orders/basket-checkout/basket-checkout.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const rootReducer = {
  authentication,
  locale,
  applicationProfile,
  administration,
  userManagement,
  address,
  payment,
  orderItem,
  buyer,
  notification,
  order,
  catalogType,
  catalogItem,
  catalogBrand,
  basketItem,
  basketCheckout,
  authorization,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
  loadingBar,
  cart,
};

export default rootReducer;
