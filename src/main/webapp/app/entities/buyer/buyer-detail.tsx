import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './buyer.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const BuyerDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const buyerEntity = useAppSelector(state => state.buyer.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="buyerDetailsHeading">
          <Translate contentKey="storeApp.buyer.detail.title">Buyer</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{buyerEntity.id}</dd>
          <dt>
            <span id="firstName">
              <Translate contentKey="storeApp.buyer.firstName">First Name</Translate>
            </span>
          </dt>
          <dd>{buyerEntity.firstName}</dd>
          <dt>
            <span id="lastName">
              <Translate contentKey="storeApp.buyer.lastName">Last Name</Translate>
            </span>
          </dt>
          <dd>{buyerEntity.lastName}</dd>
          <dt>
            <span id="gender">
              <Translate contentKey="storeApp.buyer.gender">Gender</Translate>
            </span>
          </dt>
          <dd>{buyerEntity.gender}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="storeApp.buyer.email">Email</Translate>
            </span>
          </dt>
          <dd>{buyerEntity.email}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="storeApp.buyer.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{buyerEntity.phone}</dd>
          <dt>
            <Translate contentKey="storeApp.buyer.user">User</Translate>
          </dt>
          <dd>{buyerEntity.user ? buyerEntity.user.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/buyer" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/buyer/${buyerEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default BuyerDetail;
