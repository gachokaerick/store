import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import CatalogBrand from './catalog-brand';
import CatalogBrandDetail from './catalog-brand-detail';
import CatalogBrandUpdate from './catalog-brand-update';
import CatalogBrandDeleteDialog from './catalog-brand-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={CatalogBrandUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={CatalogBrandUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={CatalogBrandDetail} />
      <ErrorBoundaryRoute path={match.url} component={CatalogBrand} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={CatalogBrandDeleteDialog} />
  </>
);

export default Routes;
