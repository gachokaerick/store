import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, UncontrolledTooltip, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './catalog-item.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const CatalogItemDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const catalogItemEntity = useAppSelector(state => state.catalogItem.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="catalogItemDetailsHeading">
          <Translate contentKey="storeApp.catalogItem.detail.title">CatalogItem</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{catalogItemEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="storeApp.catalogItem.name">Name</Translate>
            </span>
          </dt>
          <dd>{catalogItemEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="storeApp.catalogItem.description">Description</Translate>
            </span>
          </dt>
          <dd>{catalogItemEntity.description}</dd>
          <dt>
            <span id="price">
              <Translate contentKey="storeApp.catalogItem.price">Price</Translate>
            </span>
          </dt>
          <dd>{catalogItemEntity.price}</dd>
          <dt>
            <span id="pictureFileName">
              <Translate contentKey="storeApp.catalogItem.pictureFileName">Picture File Name</Translate>
            </span>
          </dt>
          <dd>{catalogItemEntity.pictureFileName}</dd>
          <dt>
            <span id="pictureUrl">
              <Translate contentKey="storeApp.catalogItem.pictureUrl">Picture Url</Translate>
            </span>
          </dt>
          <dd>{catalogItemEntity.pictureUrl}</dd>
          <dt>
            <span id="availableStock">
              <Translate contentKey="storeApp.catalogItem.availableStock">Available Stock</Translate>
            </span>
          </dt>
          <dd>{catalogItemEntity.availableStock}</dd>
          <dt>
            <span id="restockThreshold">
              <Translate contentKey="storeApp.catalogItem.restockThreshold">Restock Threshold</Translate>
            </span>
            <UncontrolledTooltip target="restockThreshold">
              <Translate contentKey="storeApp.catalogItem.help.restockThreshold" />
            </UncontrolledTooltip>
          </dt>
          <dd>{catalogItemEntity.restockThreshold}</dd>
          <dt>
            <span id="maxStockThreshold">
              <Translate contentKey="storeApp.catalogItem.maxStockThreshold">Max Stock Threshold</Translate>
            </span>
            <UncontrolledTooltip target="maxStockThreshold">
              <Translate contentKey="storeApp.catalogItem.help.maxStockThreshold" />
            </UncontrolledTooltip>
          </dt>
          <dd>{catalogItemEntity.maxStockThreshold}</dd>
          <dt>
            <span id="onReorder">
              <Translate contentKey="storeApp.catalogItem.onReorder">On Reorder</Translate>
            </span>
          </dt>
          <dd>{catalogItemEntity.onReorder ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="storeApp.catalogItem.catalogBrand">Catalog Brand</Translate>
          </dt>
          <dd>{catalogItemEntity.catalogBrand ? catalogItemEntity.catalogBrand.brand : ''}</dd>
          <dt>
            <Translate contentKey="storeApp.catalogItem.catalogType">Catalog Type</Translate>
          </dt>
          <dd>{catalogItemEntity.catalogType ? catalogItemEntity.catalogType.type : ''}</dd>
        </dl>
        <Button tag={Link} to="/catalog-item" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/catalog-item/${catalogItemEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CatalogItemDetail;
