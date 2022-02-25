import './landing.scss';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import React, { useEffect } from 'react';
import { REDIRECT_URL } from 'app/shared/util/url-utils';
import { Select, Space } from 'antd';
import { getEntities as getCatalogBrands } from 'app/entities/catalog/catalog-brand/catalog-brand.reducer';
import { getEntities as getCatalogTypes } from 'app/entities/catalog/catalog-type/catalog-type.reducer';
import { getEntities as getCatalogItems } from 'app/entities/catalog/catalog-item/catalog-item.reducer';

export const Landing = () => {
  const dispatch = useAppDispatch();
  const catalogBrands = useAppSelector(state => state.catalogBrand.entities);
  const catalogTypes = useAppSelector(state => state.catalogType.entities);

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
    dispatch(getCatalogItems({}));
  }, []);

  const { Option } = Select;

  return (
    <div>
      <section className={'landing-hero'} />
      <section className={'catalog-filters'}>
        <Space size={'small'} className={'h-100 container'}>
          <Select className="select-filter" mode="multiple" allowClear placeholder="Brand">
            {catalogBrands ? catalogBrands.map(brand => <Option key={brand.id}>{brand.brand}</Option>) : null}
          </Select>
          <Select className="select-filter" mode="multiple" allowClear placeholder="Type">
            {catalogTypes ? catalogTypes.map(type => <Option key={type.id}>{type.type}</Option>) : null}
          </Select>
        </Space>
      </section>
    </div>
  );
};

export default Landing;
