import React, { useEffect } from 'react';
import { Card, Col, Row, Space } from 'antd';
import { useAppSelector } from 'app/config/store';
import { RouteComponentProps } from 'react-router-dom';

export const Cart = (props: RouteComponentProps<{ url: string }>) => {
  const cart = useAppSelector(state => state.cart.items);

  useEffect(() => {
    // eslint-disable-next-line no-console
    console.log('cart: ', cart);
  }, cart);

  return (
    <Row>
      <Col span={16}>
        <Card title={`Cart (${cart.length})`} bordered={true}></Card>
      </Col>
      <Col span={8}>
        <Card title={`Cart Summary`} bordered={true}></Card>
      </Col>
    </Row>
  );
};

export default Cart;
