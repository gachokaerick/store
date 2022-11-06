import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IOrder } from 'app/shared/model/order.model';
import { getEntities as getOrders } from 'app/entities/order/order.reducer';
import { getEntity, updateEntity, createEntity, reset } from './payment.reducer';
import { IPayment } from 'app/shared/model/payment.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const PaymentUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const orders = useAppSelector(state => state.order.entities);
  const paymentEntity = useAppSelector(state => state.payment.entity);
  const loading = useAppSelector(state => state.payment.loading);
  const updating = useAppSelector(state => state.payment.updating);
  const updateSuccess = useAppSelector(state => state.payment.updateSuccess);
  const handleClose = () => {
    props.history.push('/payment' + props.location.search);
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
    values.createTime = convertDateTimeToServer(values.createTime);
    values.updateTime = convertDateTimeToServer(values.updateTime);

    const entity = {
      ...paymentEntity,
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
          createTime: displayDefaultDateTime(),
          updateTime: displayDefaultDateTime(),
        }
      : {
          ...paymentEntity,
          createTime: convertDateTimeFromServer(paymentEntity.createTime),
          updateTime: convertDateTimeFromServer(paymentEntity.updateTime),
          order: paymentEntity?.order?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="storeApp.payment.home.createOrEditLabel" data-cy="PaymentCreateUpdateHeading">
            <Translate contentKey="storeApp.payment.home.createOrEditLabel">Create or edit a Payment</Translate>
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
                  id="payment-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('storeApp.payment.createTime')}
                id="payment-createTime"
                name="createTime"
                data-cy="createTime"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('storeApp.payment.updateTime')}
                id="payment-updateTime"
                name="updateTime"
                data-cy="updateTime"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('storeApp.payment.paymentStatus')}
                id="payment-paymentStatus"
                name="paymentStatus"
                data-cy="paymentStatus"
                type="text"
              />
              <ValidatedField
                label={translate('storeApp.payment.payerCountryCode')}
                id="payment-payerCountryCode"
                name="payerCountryCode"
                data-cy="payerCountryCode"
                type="text"
              />
              <ValidatedField
                label={translate('storeApp.payment.payerEmail')}
                id="payment-payerEmail"
                name="payerEmail"
                data-cy="payerEmail"
                type="text"
              />
              <ValidatedField
                label={translate('storeApp.payment.payerName')}
                id="payment-payerName"
                name="payerName"
                data-cy="payerName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('storeApp.payment.payerSurname')}
                id="payment-payerSurname"
                name="payerSurname"
                data-cy="payerSurname"
                type="text"
              />
              <ValidatedField
                label={translate('storeApp.payment.payerId')}
                id="payment-payerId"
                name="payerId"
                data-cy="payerId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('storeApp.payment.currency')}
                id="payment-currency"
                name="currency"
                data-cy="currency"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('storeApp.payment.amount')}
                id="payment-amount"
                name="amount"
                data-cy="amount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('storeApp.payment.paymentId')}
                id="payment-paymentId"
                name="paymentId"
                data-cy="paymentId"
                type="text"
              />
              <ValidatedField
                id="payment-order"
                name="order"
                data-cy="order"
                label={translate('storeApp.payment.order')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/payment" replace color="info">
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

export default PaymentUpdate;
