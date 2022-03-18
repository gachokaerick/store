import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './basket-item.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const BasketItemDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const basketItemEntity = useAppSelector(state => state.basketItem.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="basketItemDetailsHeading">
          <Translate contentKey="storeApp.ordersBasketItem.detail.title">BasketItem</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{basketItemEntity.id}</dd>
          <dt>
            <span id="productId">
              <Translate contentKey="storeApp.ordersBasketItem.productId">Product Id</Translate>
            </span>
          </dt>
          <dd>{basketItemEntity.productId}</dd>
          <dt>
            <span id="productName">
              <Translate contentKey="storeApp.ordersBasketItem.productName">Product Name</Translate>
            </span>
          </dt>
          <dd>{basketItemEntity.productName}</dd>
          <dt>
            <span id="unitPrice">
              <Translate contentKey="storeApp.ordersBasketItem.unitPrice">Unit Price</Translate>
            </span>
          </dt>
          <dd>{basketItemEntity.unitPrice}</dd>
          <dt>
            <span id="oldUnitPrice">
              <Translate contentKey="storeApp.ordersBasketItem.oldUnitPrice">Old Unit Price</Translate>
            </span>
          </dt>
          <dd>{basketItemEntity.oldUnitPrice}</dd>
          <dt>
            <span id="quantity">
              <Translate contentKey="storeApp.ordersBasketItem.quantity">Quantity</Translate>
            </span>
          </dt>
          <dd>{basketItemEntity.quantity}</dd>
          <dt>
            <span id="pictureUrl">
              <Translate contentKey="storeApp.ordersBasketItem.pictureUrl">Picture Url</Translate>
            </span>
          </dt>
          <dd>{basketItemEntity.pictureUrl}</dd>
          <dt>
            <span id="userLogin">
              <Translate contentKey="storeApp.ordersBasketItem.userLogin">User Login</Translate>
            </span>
          </dt>
          <dd>{basketItemEntity.userLogin}</dd>
        </dl>
        <Button tag={Link} to="/basket-item" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/basket-item/${basketItemEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default BasketItemDetail;
