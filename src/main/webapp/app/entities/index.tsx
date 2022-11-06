import React from 'react';
import { Switch } from 'react-router-dom';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Address from './orders/address';
import Payment from './orders/payment';
import OrderItem from './orders/order-item';
import Buyer from './orders/buyer';
import Notification from './notification/notification';
import Order from './orders/order';
import CatalogType from './catalog/catalog-type';
import CatalogItem from './catalog/catalog-item';
import CatalogBrand from './catalog/catalog-brand';
import BasketItem from './basket/basket-item';
import BasketCheckout from './basket/basket-checkout';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}address`} component={Address} />
      <ErrorBoundaryRoute path={`${match.url}payment`} component={Payment} />
      <ErrorBoundaryRoute path={`${match.url}order-item`} component={OrderItem} />
      <ErrorBoundaryRoute path={`${match.url}buyer`} component={Buyer} />
      <ErrorBoundaryRoute path={`${match.url}notification`} component={Notification} />
      <ErrorBoundaryRoute path={`${match.url}order`} component={Order} />
      <ErrorBoundaryRoute path={`${match.url}catalog-type`} component={CatalogType} />
      <ErrorBoundaryRoute path={`${match.url}catalog-item`} component={CatalogItem} />
      <ErrorBoundaryRoute path={`${match.url}catalog-brand`} component={CatalogBrand} />
      <ErrorBoundaryRoute path={`${match.url}basket-item`} component={BasketItem} />
      <ErrorBoundaryRoute path={`${match.url}basket-checkout`} component={BasketCheckout} />
      {/* jhipster-needle-add-route-path - JHipster will add routes here */}
    </Switch>
  </div>
);

export default Routes;
