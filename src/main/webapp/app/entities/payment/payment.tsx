import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Row, Table } from 'reactstrap';
import { Translate, TextFormat, getSortState, JhiPagination, JhiItemCount } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntities } from './payment.reducer';
import { IPayment } from 'app/shared/model/payment.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const Payment = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getSortState(props.location, ITEMS_PER_PAGE, 'id'), props.location.search)
  );

  const paymentList = useAppSelector(state => state.payment.entities);
  const loading = useAppSelector(state => state.payment.loading);
  const totalItems = useAppSelector(state => state.payment.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      })
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (props.location.search !== endURL) {
      props.history.push(`${props.location.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(props.location.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [props.location.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const { match } = props;

  return (
    <div>
      <h2 id="payment-heading" data-cy="PaymentHeading">
        <Translate contentKey="storeApp.payment.home.title">Payments</Translate>
        <div className="d-flex justify-content-end">
          <Button className="mr-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="storeApp.payment.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="storeApp.payment.home.createLabel">Create new Payment</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {paymentList && paymentList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="storeApp.payment.id">ID</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('createTime')}>
                  <Translate contentKey="storeApp.payment.createTime">Create Time</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('updateTime')}>
                  <Translate contentKey="storeApp.payment.updateTime">Update Time</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('paymentStatus')}>
                  <Translate contentKey="storeApp.payment.paymentStatus">Payment Status</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('payerCountryCode')}>
                  <Translate contentKey="storeApp.payment.payerCountryCode">Payer Country Code</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('payerEmail')}>
                  <Translate contentKey="storeApp.payment.payerEmail">Payer Email</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('payerName')}>
                  <Translate contentKey="storeApp.payment.payerName">Payer Name</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('payerSurname')}>
                  <Translate contentKey="storeApp.payment.payerSurname">Payer Surname</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('payerId')}>
                  <Translate contentKey="storeApp.payment.payerId">Payer Id</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('currency')}>
                  <Translate contentKey="storeApp.payment.currency">Currency</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('amount')}>
                  <Translate contentKey="storeApp.payment.amount">Amount</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('paymentId')}>
                  <Translate contentKey="storeApp.payment.paymentId">Payment Id</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="storeApp.payment.order">Order</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {paymentList.map((payment, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${payment.id}`} color="link" size="sm">
                      {payment.id}
                    </Button>
                  </td>
                  <td>{payment.createTime ? <TextFormat type="date" value={payment.createTime} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{payment.updateTime ? <TextFormat type="date" value={payment.updateTime} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{payment.paymentStatus}</td>
                  <td>{payment.payerCountryCode}</td>
                  <td>{payment.payerEmail}</td>
                  <td>{payment.payerName}</td>
                  <td>{payment.payerSurname}</td>
                  <td>{payment.payerId}</td>
                  <td>{payment.currency}</td>
                  <td>{payment.amount}</td>
                  <td>{payment.paymentId}</td>
                  <td>{payment.order ? <Link to={`order/${payment.order.id}`}>{payment.order.id}</Link> : ''}</td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${payment.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`${match.url}/${payment.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`${match.url}/${payment.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="storeApp.payment.home.notFound">No Payments found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={paymentList && paymentList.length > 0 ? '' : 'd-none'}>
          <Row className="justify-content-center">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </Row>
          <Row className="justify-content-center">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </Row>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default Payment;
