import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './order-item.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const OrderItemDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const orderItemEntity = useAppSelector(state => state.orderItem.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="orderItemDetailsHeading">
          <Translate contentKey="storeApp.orderItem.detail.title">OrderItem</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{orderItemEntity.id}</dd>
          <dt>
            <span id="productName">
              <Translate contentKey="storeApp.orderItem.productName">Product Name</Translate>
            </span>
          </dt>
          <dd>{orderItemEntity.productName}</dd>
          <dt>
            <span id="pictureUrl">
              <Translate contentKey="storeApp.orderItem.pictureUrl">Picture Url</Translate>
            </span>
          </dt>
          <dd>{orderItemEntity.pictureUrl}</dd>
          <dt>
            <span id="unitPrice">
              <Translate contentKey="storeApp.orderItem.unitPrice">Unit Price</Translate>
            </span>
          </dt>
          <dd>{orderItemEntity.unitPrice}</dd>
          <dt>
            <span id="discount">
              <Translate contentKey="storeApp.orderItem.discount">Discount</Translate>
            </span>
          </dt>
          <dd>{orderItemEntity.discount}</dd>
          <dt>
            <span id="units">
              <Translate contentKey="storeApp.orderItem.units">Units</Translate>
            </span>
          </dt>
          <dd>{orderItemEntity.units}</dd>
          <dt>
            <span id="productId">
              <Translate contentKey="storeApp.orderItem.productId">Product Id</Translate>
            </span>
          </dt>
          <dd>{orderItemEntity.productId}</dd>
          <dt>
            <Translate contentKey="storeApp.orderItem.order">Order</Translate>
          </dt>
          <dd>{orderItemEntity.order ? orderItemEntity.order.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/order-item" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/order-item/${orderItemEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default OrderItemDetail;
