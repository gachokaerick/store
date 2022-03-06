import React, { useEffect } from 'react';
import { Button, Card, Col, Image, Row, Space, Typography } from 'antd';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { RouteComponentProps } from 'react-router-dom';
import { DeleteOutlined, MinusOutlined, PlusOutlined } from '@ant-design/icons';
import { getEntities as getCatalogItems } from 'app/entities/catalog/catalog-item/catalog-item.reducer';
import { addItemToCart, removeItemFromCart, reduceFromCart } from 'app/modules/cart/cart.reducer';
import { ICatalogItem } from 'app/shared/model/catalog/catalog-item.model';
import { isIdPresent } from 'app/shared/util/entity-utils';

export const Cart = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();
  const { Text, Title } = Typography;
  const cart = useAppSelector(state => state.cart.items);
  const catalogItems = useAppSelector(state => state.catalogItem.entities);
  let checkoutTotal = 0;

  useEffect(() => {
    if (cart.length > 0) {
      const ids: string = cart.map(item => item.id).join();
      dispatch(getCatalogItems({ ids }));
    }
  }, []);

  useEffect(() => {
    checkoutTotal = catalogItems?.reduce((total, item) => {
      return total + item.price;
    }, 0);
  }, [catalogItems]);

  return (
    <Row
      gutter={[
        { xs: 8, sm: 16, md: 24, lg: 32 },
        { xs: 8, sm: 16, md: 24, lg: 32 },
      ]}
    >
      <Col lg={16} xs={24}>
        <Card title={`Cart (${catalogItems.length})`} bordered={true}>
          {catalogItems
            ? catalogItems
                .filter(item => isIdPresent(cart, item.id))
                .map(item => (
                  <Space key={item.id} direction={'vertical'} className={'w-100'}>
                    <Row>
                      <Col xs={24} sm={6}>
                        <Image src={item.pictureUrl} height={100} />
                      </Col>
                      <Col xs={24} sm={18}>
                        <Row>
                          <Col xs={24} md={12}>
                            <Title level={5}>Name</Title>
                            <Text className={'d-block'}>{item.description}</Text>
                            <Text className={'d-block'}>Available: {item.availableStock}</Text>
                          </Col>
                          <Col xs={24} md={12} className={'text-md-right'}>
                            <Title level={4}>${item.price}</Title>
                          </Col>
                        </Row>
                      </Col>
                    </Row>
                    <Row>
                      <Col span={12}>
                        <Button type={'text'} icon={<DeleteOutlined />} danger onClick={() => removeItemFromCart(item)}>
                          Remove
                        </Button>
                      </Col>
                      <Col span={12} className={'text-right'}>
                        <Space direction={'horizontal'}>
                          <Button type={'primary'} icon={<PlusOutlined />} onClick={() => reduceFromCart(item)} />
                          <Text>1</Text>
                          <Button type={'primary'} icon={<MinusOutlined />} onClick={() => addItemToCart(item)} />
                        </Space>
                      </Col>
                    </Row>
                  </Space>
                ))
            : null}
        </Card>
      </Col>
      <Col lg={8} xs={24}>
        <Card title={`Cart Summary`} bordered={true}>
          <Space direction={'vertical'} className={'w-100'}>
            <Row>
              <Col span={12}>
                <Text>Subtotal</Text>
              </Col>
              <Col span={12} className={'text-md-right'}>
                <Title level={4}>${checkoutTotal}</Title>
              </Col>
            </Row>
            <Row>
              <Col span={12}>
                <Text>Discount</Text>
              </Col>
              <Col span={12} className={'text-md-right'}>
                <Text>$0</Text>
              </Col>
            </Row>
            <Row>
              <Col span={12}>
                <Text>Total</Text>
              </Col>
              <Col span={12} className={'text-md-right'}>
                <Title level={4}>${checkoutTotal}</Title>
              </Col>
            </Row>
            <Button type={'primary'} block>
              Checkout (${checkoutTotal})
            </Button>
          </Space>
        </Card>
      </Col>
    </Row>
  );
};

export default Cart;
