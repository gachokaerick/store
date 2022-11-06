import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity, updateEntity, createEntity, reset } from './basket-checkout.reducer';
import { IBasketCheckout } from 'app/shared/model/basket/basket-checkout.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const BasketCheckoutUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const basketCheckoutEntity = useAppSelector(state => state.basketCheckout.entity);
  const loading = useAppSelector(state => state.basketCheckout.loading);
  const updating = useAppSelector(state => state.basketCheckout.updating);
  const updateSuccess = useAppSelector(state => state.basketCheckout.updateSuccess);
  const handleClose = () => {
    props.history.push('/basket-checkout' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.createTime = convertDateTimeToServer(values.createTime);
    values.updateTime = convertDateTimeToServer(values.updateTime);

    const entity = {
      ...basketCheckoutEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          createTime: displayDefaultDateTime(),
          updateTime: displayDefaultDateTime(),
        }
      : {
          ...basketCheckoutEntity,
          createTime: convertDateTimeFromServer(basketCheckoutEntity.createTime),
          updateTime: convertDateTimeFromServer(basketCheckoutEntity.updateTime),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="storeApp.basketBasketCheckout.home.createOrEditLabel" data-cy="BasketCheckoutCreateUpdateHeading">
            <Translate contentKey="storeApp.basketBasketCheckout.home.createOrEditLabel">Create or edit a BasketCheckout</Translate>
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
                  id="basket-checkout-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('storeApp.basketBasketCheckout.street')}
                id="basket-checkout-street"
                name="street"
                data-cy="street"
                type="text"
              />
              <ValidatedField
                label={translate('storeApp.basketBasketCheckout.city')}
                id="basket-checkout-city"
                name="city"
                data-cy="city"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('storeApp.basketBasketCheckout.town')}
                id="basket-checkout-town"
                name="town"
                data-cy="town"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('storeApp.basketBasketCheckout.country')}
                id="basket-checkout-country"
                name="country"
                data-cy="country"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('storeApp.basketBasketCheckout.zipcode')}
                id="basket-checkout-zipcode"
                name="zipcode"
                data-cy="zipcode"
                type="text"
              />
              <ValidatedField
                label={translate('storeApp.basketBasketCheckout.createTime')}
                id="basket-checkout-createTime"
                name="createTime"
                data-cy="createTime"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('storeApp.basketBasketCheckout.updateTime')}
                id="basket-checkout-updateTime"
                name="updateTime"
                data-cy="updateTime"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('storeApp.basketBasketCheckout.paymentStatus')}
                id="basket-checkout-paymentStatus"
                name="paymentStatus"
                data-cy="paymentStatus"
                type="text"
              />
              <ValidatedField
                label={translate('storeApp.basketBasketCheckout.payerCountryCode')}
                id="basket-checkout-payerCountryCode"
                name="payerCountryCode"
                data-cy="payerCountryCode"
                type="text"
              />
              <ValidatedField
                label={translate('storeApp.basketBasketCheckout.payerEmail')}
                id="basket-checkout-payerEmail"
                name="payerEmail"
                data-cy="payerEmail"
                type="text"
              />
              <ValidatedField
                label={translate('storeApp.basketBasketCheckout.payerName')}
                id="basket-checkout-payerName"
                name="payerName"
                data-cy="payerName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('storeApp.basketBasketCheckout.payerSurname')}
                id="basket-checkout-payerSurname"
                name="payerSurname"
                data-cy="payerSurname"
                type="text"
              />
              <ValidatedField
                label={translate('storeApp.basketBasketCheckout.payerId')}
                id="basket-checkout-payerId"
                name="payerId"
                data-cy="payerId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('storeApp.basketBasketCheckout.currency')}
                id="basket-checkout-currency"
                name="currency"
                data-cy="currency"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('storeApp.basketBasketCheckout.amount')}
                id="basket-checkout-amount"
                name="amount"
                data-cy="amount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('storeApp.basketBasketCheckout.paymentId')}
                id="basket-checkout-paymentId"
                name="paymentId"
                data-cy="paymentId"
                type="text"
              />
              <ValidatedField
                label={translate('storeApp.basketBasketCheckout.userLogin')}
                id="basket-checkout-userLogin"
                name="userLogin"
                data-cy="userLogin"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('storeApp.basketBasketCheckout.description')}
                id="basket-checkout-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/basket-checkout" replace color="info">
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

export default BasketCheckoutUpdate;
