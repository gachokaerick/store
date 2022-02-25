import './landing.scss';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import React, { useEffect } from 'react';
import { REDIRECT_URL } from 'app/shared/util/url-utils';
import { Button, Col, Image, Pagination, Row, Select, Space, Typography } from 'antd';
import { getEntities as getCatalogBrands } from 'app/entities/catalog/catalog-brand/catalog-brand.reducer';
import { getEntities as getCatalogTypes } from 'app/entities/catalog/catalog-type/catalog-type.reducer';

export const Landing = () => {
  const { Text } = Typography;
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
    // dispatch(getCatalogItems({}));
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

      <section>
        <Row gutter={16} justify={'center'} className={'mt-4'}>
          <Col span={8}>
            <Row justify={'center'}>
              <Space align={'center'} direction={'vertical'}>
                <Image className={'catalog-image'} src={'../../../content/images/imageonline-co-placeholder-image.jpg'} />
                <Button type={'primary'}>Add To Cart</Button>
                <Text>Leather Pants</Text>
                <Text strong>$12.00</Text>
              </Space>
            </Row>
          </Col>
        </Row>
        <Row justify={'center'} className={'mt-4'}>
          <Pagination total={85} showSizeChanger showQuickJumper showTotal={total => `Total ${total} items`} />
        </Row>
      </section>
    </div>
  );
};

export default Landing;
