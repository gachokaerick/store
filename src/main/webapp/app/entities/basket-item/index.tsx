import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import BasketItem from './basket-item';
import BasketItemDetail from './basket-item-detail';
import BasketItemUpdate from './basket-item-update';
import BasketItemDeleteDialog from './basket-item-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={BasketItemUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={BasketItemUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={BasketItemDetail} />
      <ErrorBoundaryRoute path={match.url} component={BasketItem} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={BasketItemDeleteDialog} />
  </>
);

export default Routes;
