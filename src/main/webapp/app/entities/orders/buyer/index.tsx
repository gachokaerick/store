import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Buyer from './buyer';
import BuyerDetail from './buyer-detail';
import BuyerUpdate from './buyer-update';
import BuyerDeleteDialog from './buyer-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={BuyerUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={BuyerUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={BuyerDetail} />
      <ErrorBoundaryRoute path={match.url} component={Buyer} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={BuyerDeleteDialog} />
  </>
);

export default Routes;
