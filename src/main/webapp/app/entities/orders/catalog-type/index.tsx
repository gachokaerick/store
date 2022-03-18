import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import CatalogType from './catalog-type';
import CatalogTypeDetail from './catalog-type-detail';
import CatalogTypeUpdate from './catalog-type-update';
import CatalogTypeDeleteDialog from './catalog-type-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={CatalogTypeUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={CatalogTypeUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={CatalogTypeDetail} />
      <ErrorBoundaryRoute path={match.url} component={CatalogType} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={CatalogTypeDeleteDialog} />
  </>
);

export default Routes;
