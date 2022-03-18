import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './authorization.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const AuthorizationDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const authorizationEntity = useAppSelector(state => state.authorization.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="authorizationDetailsHeading">
          <Translate contentKey="storeApp.ordersAuthorization.detail.title">Authorization</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{authorizationEntity.id}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="storeApp.ordersAuthorization.status">Status</Translate>
            </span>
          </dt>
          <dd>{authorizationEntity.status}</dd>
          <dt>
            <span id="authId">
              <Translate contentKey="storeApp.ordersAuthorization.authId">Auth Id</Translate>
            </span>
          </dt>
          <dd>{authorizationEntity.authId}</dd>
          <dt>
            <span id="currencyCode">
              <Translate contentKey="storeApp.ordersAuthorization.currencyCode">Currency Code</Translate>
            </span>
          </dt>
          <dd>{authorizationEntity.currencyCode}</dd>
          <dt>
            <span id="amount">
              <Translate contentKey="storeApp.ordersAuthorization.amount">Amount</Translate>
            </span>
          </dt>
          <dd>{authorizationEntity.amount}</dd>
          <dt>
            <span id="expirationTime">
              <Translate contentKey="storeApp.ordersAuthorization.expirationTime">Expiration Time</Translate>
            </span>
          </dt>
          <dd>
            {authorizationEntity.expirationTime ? (
              <TextFormat value={authorizationEntity.expirationTime} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="paymentProvider">
              <Translate contentKey="storeApp.ordersAuthorization.paymentProvider">Payment Provider</Translate>
            </span>
          </dt>
          <dd>{authorizationEntity.paymentProvider}</dd>
          <dt>
            <Translate contentKey="storeApp.ordersAuthorization.order">Order</Translate>
          </dt>
          <dd>{authorizationEntity.order ? authorizationEntity.order.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/authorization" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/authorization/${authorizationEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AuthorizationDetail;
