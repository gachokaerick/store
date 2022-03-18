import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import { Translate, translate } from 'react-jhipster';
import { NavDropdown } from './menu-components';

export const EntitiesMenu = props => (
  <NavDropdown
    icon="th-list"
    name={translate('global.menu.entities.main')}
    id="entity-menu"
    data-cy="entity"
    style={{ maxHeight: '80vh', overflow: 'auto' }}
  >
    <>{/* to avoid warnings when empty */}</>
    <MenuItem icon="asterisk" to="/address">
      <Translate contentKey="global.menu.entities.ordersAddress" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/payment">
      <Translate contentKey="global.menu.entities.ordersPayment" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/order-item">
      <Translate contentKey="global.menu.entities.ordersOrderItem" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/buyer">
      <Translate contentKey="global.menu.entities.ordersBuyer" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/notification">
      <Translate contentKey="global.menu.entities.notificationNotification" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/order">
      <Translate contentKey="global.menu.entities.ordersOrder" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/catalog-type">
      <Translate contentKey="global.menu.entities.catalogCatalogType" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/catalog-item">
      <Translate contentKey="global.menu.entities.catalogCatalogItem" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/catalog-brand">
      <Translate contentKey="global.menu.entities.catalogCatalogBrand" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/basket-item">
      <Translate contentKey="global.menu.entities.basketBasketItem" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/basket-checkout">
      <Translate contentKey="global.menu.entities.basketBasketCheckout" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/authorization">
      <Translate contentKey="global.menu.entities.ordersAuthorization" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/notification">
      <Translate contentKey="global.menu.entities.ordersNotification" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/catalog-type">
      <Translate contentKey="global.menu.entities.ordersCatalogType" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/catalog-item">
      <Translate contentKey="global.menu.entities.ordersCatalogItem" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/catalog-brand">
      <Translate contentKey="global.menu.entities.ordersCatalogBrand" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/basket-item">
      <Translate contentKey="global.menu.entities.ordersBasketItem" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/basket-checkout">
      <Translate contentKey="global.menu.entities.ordersBasketCheckout" />
    </MenuItem>
    {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
  </NavDropdown>
);
