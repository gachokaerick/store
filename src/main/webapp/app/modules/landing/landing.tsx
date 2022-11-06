import './landing.scss';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import React, { useEffect, useState } from 'react';
import { REDIRECT_URL } from 'app/shared/util/url-utils';
import { Button, Col, Image, Row, Select, Space, Typography } from 'antd';
import { getEntities as getCatalogBrands, selectBrand } from 'app/entities/catalog/catalog-brand/catalog-brand.reducer';
import { getEntities as getCatalogTypes, selectType } from 'app/entities/catalog/catalog-type/catalog-type.reducer';
import { getEntities as getCatalogItems } from 'app/entities/catalog/catalog-item/catalog-item.reducer';
import { getSortState, JhiItemCount, JhiPagination } from 'react-jhipster';
import { isIdPresent, overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { RouteComponentProps } from 'react-router-dom';
import { ICatalogItem } from 'app/shared/model/catalog/catalog-item.model';
import { addItemToCart, removeItemFromCart } from 'app/modules/cart/cart.reducer';
import { PlusOutlined, CloseOutlined } from '@ant-design/icons';

export const Landing = (props: RouteComponentProps<{ url: string }>) => {
  const { Text } = Typography;
  const { Option } = Select;

  const dispatch = useAppDispatch();
  const catalogBrands = useAppSelector(state => state.catalogBrand.entities);
  const catalogTypes = useAppSelector(state => state.catalogType.entities);
  const catalogItems = useAppSelector(state => state.catalogItem.entities);
  const totalItems = useAppSelector(state => state.catalogItem.totalItems);
  const selectedBrand = useAppSelector(state => state.catalogBrand.selectedItem);
  const selectedType = useAppSelector(state => state.catalogType.selectedItem);
  const cart = useAppSelector(state => state.cart.items);

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getSortState(props.location, 6, 'id'), props.location.search)
  );

  const catalogItemsQueryParams = {
    page: paginationState.activePage - 1,
    size: 6,
    sort: `${paginationState.sort},${paginationState.order}`,
    catalogBrand: selectedBrand?.brand,
    catalogType: selectedType?.type,
  };

  useEffect(() => {
    const redirectURL = localStorage.getItem(REDIRECT_URL);
    if (redirectURL) {
      localStorage.removeItem(REDIRECT_URL);
      location.href = `${location.origin}${redirectURL}`;
    }
  });

  useEffect(() => {
    dispatch(getCatalogBrands({}));
    dispatch(getCatalogTypes({}));
  }, []);

  useEffect(() => {
    dispatch(getCatalogItems(catalogItemsQueryParams));
  }, [paginationState]);

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const brandSelection = id => {
    dispatch(selectBrand(id));
  };

  const typeSelection = id => {
    dispatch(selectType(id));
  };

  useEffect(() => {
    dispatch(getCatalogItems(catalogItemsQueryParams));
  }, [selectedBrand, selectedType]);

  const addToCart = (item: ICatalogItem) => {
    dispatch(addItemToCart(item));
  };

  const removeFromCart = (item: ICatalogItem) => {
    dispatch(removeItemFromCart(item));
  };

  return (
    <div>
      <section className={'landing-hero'} />
      <section className={'catalog-filters'}>
        <Space size={'small'} className={'h-100 container'}>
          <Select
            className="select-filter"
            allowClear
            placeholder="Brand"
            onSelect={(id, option) => brandSelection(id)}
            onClear={() => brandSelection(null)}
          >
            {catalogBrands ? catalogBrands.map(brand => <Option key={brand.id}>{brand.brand}</Option>) : null}
          </Select>
          <Select
            className="select-filter"
            allowClear
            placeholder="Type"
            onSelect={(id, option) => typeSelection(id)}
            onClear={() => typeSelection(null)}
          >
            {catalogTypes ? catalogTypes.map(type => <Option key={type.id}>{type.type}</Option>) : null}
          </Select>
        </Space>
      </section>

      <section>
        <Row gutter={16} justify={'center'}>
          {catalogItems
            ? catalogItems.map(item => (
                <Col span={8} key={item.id} className={'mt-4'}>
                  <Row justify={'center'}>
                    <Space align={'center'} direction={'vertical'}>
                      <Image className={'catalog-image'} src={item.pictureUrl} />
                      <Button type={'primary'} hidden={isIdPresent(cart, item.id)} icon={<PlusOutlined />} onClick={() => addToCart(item)}>
                        Add to Cart
                      </Button>
                      <Button danger hidden={!isIdPresent(cart, item.id)} icon={<CloseOutlined />} onClick={() => removeFromCart(item)}>
                        Remove from Cart
                      </Button>
                      <Text>{item.name}</Text>
                      <Text strong>${item.price}</Text>
                    </Space>
                  </Row>
                </Col>
              ))
            : null}
        </Row>
        <Row justify={'center'} className={'mt-4'}>
          <Space direction={'vertical'}>
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </Space>
        </Row>
      </section>
    </div>
  );
};

export default Landing;
