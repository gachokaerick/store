import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Authorization from './authorization';
import AuthorizationDetail from './authorization-detail';
import AuthorizationUpdate from './authorization-update';
import AuthorizationDeleteDialog from './authorization-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={AuthorizationUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={AuthorizationUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={AuthorizationDetail} />
      <ErrorBoundaryRoute path={match.url} component={Authorization} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={AuthorizationDeleteDialog} />
  </>
);

export default Routes;
