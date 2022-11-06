import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './basket-checkout.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const BasketCheckoutDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const basketCheckoutEntity = useAppSelector(state => state.basketCheckout.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="basketCheckoutDetailsHeading">
          <Translate contentKey="storeApp.basketCheckout.detail.title">BasketCheckout</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{basketCheckoutEntity.id}</dd>
          <dt>
            <span id="street">
              <Translate contentKey="storeApp.basketCheckout.street">Street</Translate>
            </span>
          </dt>
          <dd>{basketCheckoutEntity.street}</dd>
          <dt>
            <span id="city">
              <Translate contentKey="storeApp.basketCheckout.city">City</Translate>
            </span>
          </dt>
          <dd>{basketCheckoutEntity.city}</dd>
          <dt>
            <span id="town">
              <Translate contentKey="storeApp.basketCheckout.town">Town</Translate>
            </span>
          </dt>
          <dd>{basketCheckoutEntity.town}</dd>
          <dt>
            <span id="country">
              <Translate contentKey="storeApp.basketCheckout.country">Country</Translate>
            </span>
          </dt>
          <dd>{basketCheckoutEntity.country}</dd>
          <dt>
            <span id="zipcode">
              <Translate contentKey="storeApp.basketCheckout.zipcode">Zipcode</Translate>
            </span>
          </dt>
          <dd>{basketCheckoutEntity.zipcode}</dd>
          <dt>
            <span id="createTime">
              <Translate contentKey="storeApp.basketCheckout.createTime">Create Time</Translate>
            </span>
          </dt>
          <dd>
            {basketCheckoutEntity.createTime ? (
              <TextFormat value={basketCheckoutEntity.createTime} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="updateTime">
              <Translate contentKey="storeApp.basketCheckout.updateTime">Update Time</Translate>
            </span>
          </dt>
          <dd>
            {basketCheckoutEntity.updateTime ? (
              <TextFormat value={basketCheckoutEntity.updateTime} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="paymentStatus">
              <Translate contentKey="storeApp.basketCheckout.paymentStatus">Payment Status</Translate>
            </span>
          </dt>
          <dd>{basketCheckoutEntity.paymentStatus}</dd>
          <dt>
            <span id="payerCountryCode">
              <Translate contentKey="storeApp.basketCheckout.payerCountryCode">Payer Country Code</Translate>
            </span>
          </dt>
          <dd>{basketCheckoutEntity.payerCountryCode}</dd>
          <dt>
            <span id="payerEmail">
              <Translate contentKey="storeApp.basketCheckout.payerEmail">Payer Email</Translate>
            </span>
          </dt>
          <dd>{basketCheckoutEntity.payerEmail}</dd>
          <dt>
            <span id="payerName">
              <Translate contentKey="storeApp.basketCheckout.payerName">Payer Name</Translate>
            </span>
          </dt>
          <dd>{basketCheckoutEntity.payerName}</dd>
          <dt>
            <span id="payerSurname">
              <Translate contentKey="storeApp.basketCheckout.payerSurname">Payer Surname</Translate>
            </span>
          </dt>
          <dd>{basketCheckoutEntity.payerSurname}</dd>
          <dt>
            <span id="payerId">
              <Translate contentKey="storeApp.basketCheckout.payerId">Payer Id</Translate>
            </span>
          </dt>
          <dd>{basketCheckoutEntity.payerId}</dd>
          <dt>
            <span id="currency">
              <Translate contentKey="storeApp.basketCheckout.currency">Currency</Translate>
            </span>
          </dt>
          <dd>{basketCheckoutEntity.currency}</dd>
          <dt>
            <span id="amount">
              <Translate contentKey="storeApp.basketCheckout.amount">Amount</Translate>
            </span>
          </dt>
          <dd>{basketCheckoutEntity.amount}</dd>
          <dt>
            <span id="paymentId">
              <Translate contentKey="storeApp.basketCheckout.paymentId">Payment Id</Translate>
            </span>
          </dt>
          <dd>{basketCheckoutEntity.paymentId}</dd>
          <dt>
            <span id="userLogin">
              <Translate contentKey="storeApp.basketCheckout.userLogin">User Login</Translate>
            </span>
          </dt>
          <dd>{basketCheckoutEntity.userLogin}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="storeApp.basketCheckout.description">Description</Translate>
            </span>
          </dt>
          <dd>{basketCheckoutEntity.description}</dd>
        </dl>
        <Button tag={Link} to="/basket-checkout" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/basket-checkout/${basketCheckoutEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default BasketCheckoutDetail;
