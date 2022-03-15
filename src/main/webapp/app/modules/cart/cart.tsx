import React, { useEffect } from 'react';
import { Button, Card, Col, Divider, Image, Radio, Row, Space, Spin, Typography } from 'antd';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { RouteComponentProps } from 'react-router-dom';
import { DeleteOutlined, EnvironmentOutlined, MinusOutlined, PlusOutlined, ShoppingCartOutlined } from '@ant-design/icons';
import { getEntities as getCatalogItems } from 'app/entities/catalog/catalog-item/catalog-item.reducer';
import { getEntities as getAddressItems, selectAddress } from 'app/entities/orders/address/address.reducer';
import { createEntity as createOrder } from 'app/entities/orders/order/order.reducer';
import { addItemToCart, removeItemFromCart, reduceFromCart, setCartItems } from 'app/modules/cart/cart.reducer';
import { ICartCatalogItem, ICatalogItem } from 'app/shared/model/catalog/catalog-item.model';
import { isIdPresent } from 'app/shared/util/entity-utils';
import { getLoginUrl, REDIRECT_URL } from 'app/shared/util/url-utils';
import { PayPalButtons, usePayPalScriptReducer } from '@paypal/react-paypal-js';
import PaypalCheckout from 'app/modules/checkout/paypal-checkout';
import { Tabs } from 'antd';
import { StickyContainer, Sticky } from 'react-sticky';
import './cart.scss';
import { selectBrand } from 'app/entities/catalog/catalog-brand/catalog-brand.reducer';
import { IOrder } from 'app/shared/model/orders/order.model';

export const Cart = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();
  const { Text, Title } = Typography;
  const cart = useAppSelector(state => state.cart.items);
  const catalogItems = useAppSelector(state => state.catalogItem.entities);
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);
  const account = useAppSelector(state => state.authentication.account);
  const addresses = useAppSelector(state => state.address.entities);
  const selectedAddress = useAppSelector(state => state.address.selectedItem);
  const orderUpdateSuccess = useAppSelector(state => state.order.updateSuccess);
  const orderUpdating = useAppSelector(state => state.order.updating);

  const [{ isPending }] = usePayPalScriptReducer();
  const { TabPane } = Tabs;

  useEffect(() => {
    if (isAuthenticated) {
      // get user addresses
      dispatch(getAddressItems({ login: account.login }));
    }
  }, [isAuthenticated]);

  useEffect(() => {
    if (cart.length > 0 && catalogItems.length !== cart.length) {
      const ids: string = cart.map(item => item.id).join();
      dispatch(getCatalogItems({ ids }));
    }
  }, [cart]);

  useEffect(() => {
    if (orderUpdateSuccess) {
      dispatch(setCartItems([]));
      props.history.push('/order' + props.location.search);
    }
  }, [orderUpdateSuccess]);

  useEffect(() => {
    // eslint-disable-next-line no-console
    console.log('address selected: ', selectedAddress);
  }, [selectedAddress]);

  const getItemFromCartById = (id: number): ICartCatalogItem => {
    const items = cart?.filter(cartItem => cartItem.id === id);
    if (items?.length > 0) {
      return items[0];
    } else {
      return null;
    }
  };

  const getCatalogItemById = (id: number): ICatalogItem => {
    const items = catalogItems?.filter(item => item.id === id);
    if (items?.length > 0) {
      return items[0];
    } else {
      return null;
    }
  };

  const getCheckoutTotal = () =>
    cart?.reduce((total, item) => {
      const catalogItem = getCatalogItemById(item.id);
      return catalogItem ? total + catalogItem.price * item.quantity : total + item.price * item.quantity;
    }, 0);

  const renderTabBar = (barProps, DefaultTabBar) => (
    <Sticky bottomOffset={80}>
      {({ style, isSticky }) => (
        <DefaultTabBar {...barProps} className="site-custom-tab-bar" style={{ ...style, top: isSticky ? `${62 + style.top}px` : `0px` }} />
      )}
    </Sticky>
  );

  const selectOrderAddress = id => {
    // eslint-disable-next-line no-console
    console.log('selecting address: ', id);
    dispatch(selectAddress(id));
  };

  const submitOrder = (order: IOrder) => {
    dispatch(createOrder(order));
  };

  return (
    <Row
      gutter={[
        { xs: 8, sm: 16, md: 24, lg: 32 },
        { xs: 8, sm: 16, md: 24, lg: 32 },
      ]}
    >
      <Col lg={16} xs={24}>
        <StickyContainer>
          <Tabs defaultActiveKey="1" size={'large'} renderTabBar={renderTabBar}>
            <TabPane
              tab={
                <span>
                  <ShoppingCartOutlined />
                  Cart ({cart.length})
                </span>
              }
              key="1"
            >
              {catalogItems
                ? catalogItems
                    .filter(item => isIdPresent(cart, item.id))
                    .map((item, i, { length }) => (
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
                            <Button type={'text'} icon={<DeleteOutlined />} danger onClick={() => dispatch(removeItemFromCart(item))}>
                              Remove
                            </Button>
                          </Col>
                          <Col span={12} className={'text-right'}>
                            <Space direction={'horizontal'}>
                              <Button type={'primary'} icon={<PlusOutlined />} onClick={() => dispatch(addItemToCart(item))} />
                              <Text>{getItemFromCartById(item.id)?.quantity}</Text>
                              <Button type={'primary'} icon={<MinusOutlined />} onClick={() => dispatch(reduceFromCart(item))} />
                            </Space>
                          </Col>
                        </Row>
                        <Divider className={length - 1 === i ? 'd-none' : ''} />
                      </Space>
                    ))
                : null}
            </TabPane>
            <TabPane
              tab={
                <span>
                  <EnvironmentOutlined />
                  Address
                </span>
              }
              key="2"
            >
              {!isAuthenticated ? (
                <Text strong>Please sign in to select an address</Text>
              ) : addresses.length > 0 ? (
                <Radio.Group onChange={e => selectOrderAddress(e.target.value)}>
                  <Space direction="vertical">
                    {addresses.map(address => (
                      <Radio key={address.id} value={address.id}>
                        <p>
                          {address.country}, {address.city}, {address.town}, {address.street}, {address.zipcode}
                        </p>
                      </Radio>
                    ))}
                  </Space>
                </Radio.Group>
              ) : (
                <Button type={'primary'} onClick={() => props.history.push('/address')}>
                  Add Address
                </Button>
              )}
            </TabPane>
          </Tabs>
        </StickyContainer>
      </Col>
      <Col lg={8} xs={24}>
        <StickyContainer className={'h-100'}>
          <Sticky topOffset={0} bottomOffset={80}>
            {({ style, isSticky }) => (
              <Card title={`Cart Summary`} bordered={true} style={{ ...style, top: isSticky ? `${62 + style.top}px` : `0px` }}>
                <Space direction={'vertical'} className={'w-100'}>
                  <Row>
                    <Col span={12}>
                      <Text>Subtotal</Text>
                    </Col>
                    <Col span={12} className={'text-md-right'}>
                      <Title level={4}>${getCheckoutTotal()}</Title>
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
                      <Title level={4}>${getCheckoutTotal()}</Title>
                    </Col>
                  </Row>
                  {!isAuthenticated ? (
                    <Button type={'primary'} className={'font-weight-bold'} block onClick={() => props.history.push(getLoginUrl())}>
                      CHECKOUT (${getCheckoutTotal()})
                    </Button>
                  ) : (
                    <>
                      {isPending || orderUpdating ? (
                        <Spin />
                      ) : (
                        <>
                          {cart.length > 0 && selectedAddress?.id ? (
                            <PaypalCheckout
                              selectedAddress={selectedAddress}
                              checkoutTotal={getCheckoutTotal()}
                              catalogItems={catalogItems.filter(item => isIdPresent(cart, item.id))}
                              getItemFromCartById={id => getItemFromCartById(id)}
                              submitOrder={order => submitOrder(order)}
                            />
                          ) : (
                            <p className={'text-center'}>
                              {cart.length} {selectedAddress}
                            </p>
                          )}
                        </>
                      )}
                    </>
                  )}
                </Space>
              </Card>
            )}
          </Sticky>
        </StickyContainer>
      </Col>
    </Row>
  );
};

export default Cart;
