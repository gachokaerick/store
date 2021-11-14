import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Col, Row, Table } from 'reactstrap';
import { Translate, TextFormat, getSortState, JhiPagination, JhiItemCount } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntities } from './basket-checkout.reducer';
import { IBasketCheckout } from 'app/shared/model/basket-checkout.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const BasketCheckout = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getSortState(props.location, ITEMS_PER_PAGE, 'id'), props.location.search)
  );

  const basketCheckoutList = useAppSelector(state => state.basketCheckout.entities);
  const loading = useAppSelector(state => state.basketCheckout.loading);
  const totalItems = useAppSelector(state => state.basketCheckout.totalItems);

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
      <h2 id="basket-checkout-heading" data-cy="BasketCheckoutHeading">
        <Translate contentKey="storeApp.basketCheckout.home.title">Basket Checkouts</Translate>
        <div className="d-flex justify-content-end">
          <Button className="mr-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="storeApp.basketCheckout.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="storeApp.basketCheckout.home.createLabel">Create new Basket Checkout</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {basketCheckoutList && basketCheckoutList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="storeApp.basketCheckout.id">ID</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('street')}>
                  <Translate contentKey="storeApp.basketCheckout.street">Street</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('city')}>
                  <Translate contentKey="storeApp.basketCheckout.city">City</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('town')}>
                  <Translate contentKey="storeApp.basketCheckout.town">Town</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('country')}>
                  <Translate contentKey="storeApp.basketCheckout.country">Country</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('zipcode')}>
                  <Translate contentKey="storeApp.basketCheckout.zipcode">Zipcode</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('createTime')}>
                  <Translate contentKey="storeApp.basketCheckout.createTime">Create Time</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('updateTime')}>
                  <Translate contentKey="storeApp.basketCheckout.updateTime">Update Time</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('paymentStatus')}>
                  <Translate contentKey="storeApp.basketCheckout.paymentStatus">Payment Status</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('payerCountryCode')}>
                  <Translate contentKey="storeApp.basketCheckout.payerCountryCode">Payer Country Code</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('payerEmail')}>
                  <Translate contentKey="storeApp.basketCheckout.payerEmail">Payer Email</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('payerName')}>
                  <Translate contentKey="storeApp.basketCheckout.payerName">Payer Name</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('payerSurname')}>
                  <Translate contentKey="storeApp.basketCheckout.payerSurname">Payer Surname</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('payerId')}>
                  <Translate contentKey="storeApp.basketCheckout.payerId">Payer Id</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('currency')}>
                  <Translate contentKey="storeApp.basketCheckout.currency">Currency</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('amount')}>
                  <Translate contentKey="storeApp.basketCheckout.amount">Amount</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('paymentId')}>
                  <Translate contentKey="storeApp.basketCheckout.paymentId">Payment Id</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('userLogin')}>
                  <Translate contentKey="storeApp.basketCheckout.userLogin">User Login</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('description')}>
                  <Translate contentKey="storeApp.basketCheckout.description">Description</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {basketCheckoutList.map((basketCheckout, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${basketCheckout.id}`} color="link" size="sm">
                      {basketCheckout.id}
                    </Button>
                  </td>
                  <td>{basketCheckout.street}</td>
                  <td>{basketCheckout.city}</td>
                  <td>{basketCheckout.town}</td>
                  <td>{basketCheckout.country}</td>
                  <td>{basketCheckout.zipcode}</td>
                  <td>
                    {basketCheckout.createTime ? (
                      <TextFormat type="date" value={basketCheckout.createTime} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {basketCheckout.updateTime ? (
                      <TextFormat type="date" value={basketCheckout.updateTime} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{basketCheckout.paymentStatus}</td>
                  <td>{basketCheckout.payerCountryCode}</td>
                  <td>{basketCheckout.payerEmail}</td>
                  <td>{basketCheckout.payerName}</td>
                  <td>{basketCheckout.payerSurname}</td>
                  <td>{basketCheckout.payerId}</td>
                  <td>{basketCheckout.currency}</td>
                  <td>{basketCheckout.amount}</td>
                  <td>{basketCheckout.paymentId}</td>
                  <td>{basketCheckout.userLogin}</td>
                  <td>{basketCheckout.description}</td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${basketCheckout.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`${match.url}/${basketCheckout.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                        to={`${match.url}/${basketCheckout.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
              <Translate contentKey="storeApp.basketCheckout.home.notFound">No Basket Checkouts found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={basketCheckoutList && basketCheckoutList.length > 0 ? '' : 'd-none'}>
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

export default BasketCheckout;
