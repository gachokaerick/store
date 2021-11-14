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
      <Translate contentKey="global.menu.entities.address" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/payment">
      <Translate contentKey="global.menu.entities.payment" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/order-item">
      <Translate contentKey="global.menu.entities.orderItem" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/buyer">
      <Translate contentKey="global.menu.entities.buyer" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/notification">
      <Translate contentKey="global.menu.entities.notification" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/order">
      <Translate contentKey="global.menu.entities.order" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/catalog-type">
      <Translate contentKey="global.menu.entities.catalogType" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/catalog-item">
      <Translate contentKey="global.menu.entities.catalogItem" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/catalog-brand">
      <Translate contentKey="global.menu.entities.catalogBrand" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/basket-item">
      <Translate contentKey="global.menu.entities.basketItem" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/basket-checkout">
      <Translate contentKey="global.menu.entities.basketCheckout" />
    </MenuItem>
    {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
  </NavDropdown>
);
