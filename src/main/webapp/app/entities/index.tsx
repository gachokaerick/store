import React from 'react';
import { Switch } from 'react-router-dom';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Address from './address';
import Payment from './payment';
import OrderItem from './order-item';
import Buyer from './buyer';
import Notification from './notification';
import Order from './order';
import CatalogType from './catalog-type';
import CatalogItem from './catalog-item';
import CatalogBrand from './catalog-brand';
import BasketItem from './basket-item';
import BasketCheckout from './basket-checkout';
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
