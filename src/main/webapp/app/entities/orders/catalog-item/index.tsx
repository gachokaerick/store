import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import CatalogItem from './catalog-item';
import CatalogItemDetail from './catalog-item-detail';
import CatalogItemUpdate from './catalog-item-update';
import CatalogItemDeleteDialog from './catalog-item-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={CatalogItemUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={CatalogItemUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={CatalogItemDetail} />
      <ErrorBoundaryRoute path={match.url} component={CatalogItem} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={CatalogItemDeleteDialog} />
  </>
);

export default Routes;
