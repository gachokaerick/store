import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText, UncontrolledTooltip } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { ICatalogBrand } from 'app/shared/model/orders/catalog-brand.model';
import { getEntities as getCatalogBrands } from 'app/entities/orders/catalog-brand/catalog-brand.reducer';
import { ICatalogType } from 'app/shared/model/orders/catalog-type.model';
import { getEntities as getCatalogTypes } from 'app/entities/orders/catalog-type/catalog-type.reducer';
import { getEntity, updateEntity, createEntity, reset } from './catalog-item.reducer';
import { ICatalogItem } from 'app/shared/model/orders/catalog-item.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const CatalogItemUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const catalogBrands = useAppSelector(state => state.catalogBrand.entities);
  const catalogTypes = useAppSelector(state => state.catalogType.entities);
  const catalogItemEntity = useAppSelector(state => state.catalogItem.entity);
  const loading = useAppSelector(state => state.catalogItem.loading);
  const updating = useAppSelector(state => state.catalogItem.updating);
  const updateSuccess = useAppSelector(state => state.catalogItem.updateSuccess);
  const handleClose = () => {
    props.history.push('/catalog-item' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getCatalogBrands({}));
    dispatch(getCatalogTypes({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...catalogItemEntity,
      ...values,
      catalogBrand: catalogBrands.find(it => it.id.toString() === values.catalogBrand.toString()),
      catalogType: catalogTypes.find(it => it.id.toString() === values.catalogType.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...catalogItemEntity,
          catalogBrand: catalogItemEntity?.catalogBrand?.id,
          catalogType: catalogItemEntity?.catalogType?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="storeApp.ordersCatalogItem.home.createOrEditLabel" data-cy="CatalogItemCreateUpdateHeading">
            <Translate contentKey="storeApp.ordersCatalogItem.home.createOrEditLabel">Create or edit a CatalogItem</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="catalog-item-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('storeApp.ordersCatalogItem.name')}
                id="catalog-item-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('storeApp.ordersCatalogItem.description')}
                id="catalog-item-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField
                label={translate('storeApp.ordersCatalogItem.price')}
                id="catalog-item-price"
                name="price"
                data-cy="price"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('storeApp.ordersCatalogItem.pictureFileName')}
                id="catalog-item-pictureFileName"
                name="pictureFileName"
                data-cy="pictureFileName"
                type="text"
              />
              <ValidatedField
                label={translate('storeApp.ordersCatalogItem.pictureUrl')}
                id="catalog-item-pictureUrl"
                name="pictureUrl"
                data-cy="pictureUrl"
                type="text"
              />
              <ValidatedField
                label={translate('storeApp.ordersCatalogItem.availableStock')}
                id="catalog-item-availableStock"
                name="availableStock"
                data-cy="availableStock"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('storeApp.ordersCatalogItem.restockThreshold')}
                id="catalog-item-restockThreshold"
                name="restockThreshold"
                data-cy="restockThreshold"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <UncontrolledTooltip target="restockThresholdLabel">
                <Translate contentKey="storeApp.ordersCatalogItem.help.restockThreshold" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('storeApp.ordersCatalogItem.maxStockThreshold')}
                id="catalog-item-maxStockThreshold"
                name="maxStockThreshold"
                data-cy="maxStockThreshold"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <UncontrolledTooltip target="maxStockThresholdLabel">
                <Translate contentKey="storeApp.ordersCatalogItem.help.maxStockThreshold" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('storeApp.ordersCatalogItem.onReorder')}
                id="catalog-item-onReorder"
                name="onReorder"
                data-cy="onReorder"
                check
                type="checkbox"
              />
              <ValidatedField
                id="catalog-item-catalogBrand"
                name="catalogBrand"
                data-cy="catalogBrand"
                label={translate('storeApp.ordersCatalogItem.catalogBrand')}
                type="select"
                required
              >
                <option value="" key="0" />
                {catalogBrands
                  ? catalogBrands.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.brand}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="catalog-item-catalogType"
                name="catalogType"
                data-cy="catalogType"
                label={translate('storeApp.ordersCatalogItem.catalogType')}
                type="select"
                required
              >
                <option value="" key="0" />
                {catalogTypes
                  ? catalogTypes.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.type}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/catalog-item" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default CatalogItemUpdate;
