import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import BasketCheckout from './basket-checkout';
import BasketCheckoutDetail from './basket-checkout-detail';
import BasketCheckoutUpdate from './basket-checkout-update';
import BasketCheckoutDeleteDialog from './basket-checkout-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={BasketCheckoutUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={BasketCheckoutUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={BasketCheckoutDetail} />
      <ErrorBoundaryRoute path={match.url} component={BasketCheckout} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={BasketCheckoutDeleteDialog} />
  </>
);

export default Routes;
