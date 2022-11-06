import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './payment.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const PaymentDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const paymentEntity = useAppSelector(state => state.payment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="paymentDetailsHeading">
          <Translate contentKey="storeApp.ordersPayment.detail.title">Payment</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.id}</dd>
          <dt>
            <span id="createTime">
              <Translate contentKey="storeApp.ordersPayment.createTime">Create Time</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.createTime ? <TextFormat value={paymentEntity.createTime} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updateTime">
              <Translate contentKey="storeApp.ordersPayment.updateTime">Update Time</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.updateTime ? <TextFormat value={paymentEntity.updateTime} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="paymentStatus">
              <Translate contentKey="storeApp.ordersPayment.paymentStatus">Payment Status</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.paymentStatus}</dd>
          <dt>
            <span id="payerCountryCode">
              <Translate contentKey="storeApp.ordersPayment.payerCountryCode">Payer Country Code</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.payerCountryCode}</dd>
          <dt>
            <span id="payerEmail">
              <Translate contentKey="storeApp.ordersPayment.payerEmail">Payer Email</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.payerEmail}</dd>
          <dt>
            <span id="payerName">
              <Translate contentKey="storeApp.ordersPayment.payerName">Payer Name</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.payerName}</dd>
          <dt>
            <span id="payerSurname">
              <Translate contentKey="storeApp.ordersPayment.payerSurname">Payer Surname</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.payerSurname}</dd>
          <dt>
            <span id="payerId">
              <Translate contentKey="storeApp.ordersPayment.payerId">Payer Id</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.payerId}</dd>
          <dt>
            <span id="currency">
              <Translate contentKey="storeApp.ordersPayment.currency">Currency</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.currency}</dd>
          <dt>
            <span id="amount">
              <Translate contentKey="storeApp.ordersPayment.amount">Amount</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.amount}</dd>
          <dt>
            <span id="paymentId">
              <Translate contentKey="storeApp.ordersPayment.paymentId">Payment Id</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.paymentId}</dd>
          <dt>
            <Translate contentKey="storeApp.ordersPayment.order">Order</Translate>
          </dt>
          <dd>{paymentEntity.order ? paymentEntity.order.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/payment" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/payment/${paymentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PaymentDetail;
