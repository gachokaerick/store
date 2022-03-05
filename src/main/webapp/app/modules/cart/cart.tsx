import React, { useEffect } from 'react';
import { Button, Card, Col, Image, Row, Space, Typography } from 'antd';
import { useAppSelector } from 'app/config/store';
import { RouteComponentProps } from 'react-router-dom';
import { DeleteOutlined, MinusOutlined, PlusOutlined } from '@ant-design/icons';

export const Cart = (props: RouteComponentProps<{ url: string }>) => {
  const { Text, Title, Paragraph } = Typography;
  const cart = useAppSelector(state => state.cart.items);

  useEffect(() => {
    // eslint-disable-next-line no-console
    console.log('cart: ', cart);
  }, [cart]);

  return (
    <Row
      gutter={[
        { xs: 8, sm: 16, md: 24, lg: 32 },
        { xs: 8, sm: 16, md: 24, lg: 32 },
      ]}
    >
      <Col lg={16} xs={24}>
        <Card title={`Cart (${cart.length})`} bordered={true}>
          <Space direction={'vertical'} className={'w-100'}>
            <Row>
              <Col xs={24} sm={6}>
                <Image src="content/images/imageonline-co-placeholder-image.jpg" height={100} />
              </Col>
              <Col xs={24} sm={18}>
                <Row>
                  <Col xs={24} md={12}>
                    <Title level={5}>Name</Title>
                    <Text className={'d-block'}>Description</Text>
                    <Text className={'d-block'}>Available: 3</Text>
                  </Col>
                  <Col xs={24} md={12} className={'text-md-right'}>
                    <Title level={4}>$200</Title>
                  </Col>
                </Row>
              </Col>
            </Row>
            <Row>
              <Col span={12}>
                <Button type={'text'} icon={<DeleteOutlined />} danger>
                  Remove
                </Button>
              </Col>
              <Col span={12} className={'text-right'}>
                <Space direction={'horizontal'}>
                  <Button type={'primary'} icon={<PlusOutlined />} />
                  <Text>1</Text>
                  <Button type={'primary'} icon={<MinusOutlined />} />
                </Space>
              </Col>
            </Row>
          </Space>
        </Card>
      </Col>
      <Col lg={8} xs={24}>
        <Card title={`Cart Summary`} bordered={true}>
          <Space direction={'vertical'} className={'w-100'}>
            <Row>
              <Col span={12}>
                <Text>Total</Text>
              </Col>
              <Col span={12} className={'text-md-right'}>
                <Title level={4}>$200</Title>
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
                <Text>Subtotal</Text>
              </Col>
              <Col span={12} className={'text-md-right'}>
                <Title level={4}>$200</Title>
              </Col>
            </Row>
            <Button type={'primary'} block>
              Checkout ($200)
            </Button>
          </Space>
        </Card>
      </Col>
    </Row>
  );
};

export default Cart;
