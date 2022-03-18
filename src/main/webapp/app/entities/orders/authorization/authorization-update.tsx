import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IOrder } from 'app/shared/model/orders/order.model';
import { getEntities as getOrders } from 'app/entities/orders/order/order.reducer';
import { getEntity, updateEntity, createEntity, reset } from './authorization.reducer';
import { IAuthorization } from 'app/shared/model/orders/authorization.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { PaymentProvider } from 'app/shared/model/enumerations/payment-provider.model';

export const AuthorizationUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const orders = useAppSelector(state => state.order.entities);
  const authorizationEntity = useAppSelector(state => state.authorization.entity);
  const loading = useAppSelector(state => state.authorization.loading);
  const updating = useAppSelector(state => state.authorization.updating);
  const updateSuccess = useAppSelector(state => state.authorization.updateSuccess);
  const paymentProviderValues = Object.keys(PaymentProvider);
  const handleClose = () => {
    props.history.push('/authorization' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getOrders({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.expirationTime = convertDateTimeToServer(values.expirationTime);

    const entity = {
      ...authorizationEntity,
      ...values,
      order: orders.find(it => it.id.toString() === values.order.toString()),
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
          expirationTime: displayDefaultDateTime(),
        }
      : {
          paymentProvider: 'PAYPAL',
          ...authorizationEntity,
          expirationTime: convertDateTimeFromServer(authorizationEntity.expirationTime),
          order: authorizationEntity?.order?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="storeApp.ordersAuthorization.home.createOrEditLabel" data-cy="AuthorizationCreateUpdateHeading">
            <Translate contentKey="storeApp.ordersAuthorization.home.createOrEditLabel">Create or edit a Authorization</Translate>
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
                  id="authorization-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('storeApp.ordersAuthorization.status')}
                id="authorization-status"
                name="status"
                data-cy="status"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('storeApp.ordersAuthorization.authId')}
                id="authorization-authId"
                name="authId"
                data-cy="authId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('storeApp.ordersAuthorization.currencyCode')}
                id="authorization-currencyCode"
                name="currencyCode"
                data-cy="currencyCode"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('storeApp.ordersAuthorization.amount')}
                id="authorization-amount"
                name="amount"
                data-cy="amount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('storeApp.ordersAuthorization.expirationTime')}
                id="authorization-expirationTime"
                name="expirationTime"
                data-cy="expirationTime"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('storeApp.ordersAuthorization.paymentProvider')}
                id="authorization-paymentProvider"
                name="paymentProvider"
                data-cy="paymentProvider"
                type="select"
              >
                {paymentProviderValues.map(paymentProvider => (
                  <option value={paymentProvider} key={paymentProvider}>
                    {translate('storeApp.PaymentProvider' + paymentProvider)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="authorization-order"
                name="order"
                data-cy="order"
                label={translate('storeApp.ordersAuthorization.order')}
                type="select"
                required
              >
                <option value="" key="0" />
                {orders
                  ? orders.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/authorization" replace color="info">
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

export default AuthorizationUpdate;
