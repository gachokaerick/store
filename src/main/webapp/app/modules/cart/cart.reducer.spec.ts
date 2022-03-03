import { ICatalogItem } from 'app/shared/model/catalog/catalog-item.model';
import reducer, { addItemToCart, CartState, removeItemFromCart, setCartItems } from 'app/modules/cart/cart.reducer';
import configureStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import { ACTIONS } from 'app/config/constants';

const initialState: CartState<ICatalogItem> = {
  items: [],
};

describe('Cart reducer tests', () => {
  function isEmpty(element): boolean {
    if (element instanceof Array) {
      return element.length === 0;
    } else {
      return Object.keys(element).length === 0;
    }
  }

  function testInitialState(state) {
    expect(isEmpty(state.items));
  }

  describe('Common', () => {
    it('should return the initial state', () => {
      testInitialState(reducer(undefined, { type: '' }));
    });
  });

  describe('Actions', () => {
    let store;

    beforeEach(() => {
      const mockstore = configureStore();
      store = mockstore({});
    });

    it('dispatches ADD_TO_CART action', () => {
      store.dispatch(addItemToCart({}));
      expect(store.getActions()[0]).toMatchObject({
        type: ACTIONS.ADD_TO_CART,
        payload: {},
      });
    });

    it('dispatches REMOVE_FROM_CART action', () => {
      store.dispatch(removeItemFromCart({}));
      expect(store.getActions()[0]).toMatchObject({
        type: ACTIONS.REMOVE_FROM_CART,
        payload: {},
      });
    });

    it('dispatches SET_CART action', () => {
      store.dispatch(setCartItems([]));
      expect(store.getActions()[0]).toMatchObject({
        type: ACTIONS.SET_CART,
        payload: {},
      });
    });
  });

  describe('Successes', () => {
    it('should add item to cart', () => {
      const payload = { id: 1 };
      expect(
        reducer(undefined, {
          type: ACTIONS.ADD_TO_CART,
          payload,
        })
      ).toEqual({
        items: [...initialState.items, payload],
      });
    });

    it('should remove item from cart', () => {
      const payload = { id: 1 };
      expect(
        reducer(undefined, {
          type: ACTIONS.REMOVE_FROM_CART,
          payload,
        })
      ).toEqual({
        items: [...initialState.items],
      });
    });

    it('should set cart', () => {
      const payload = [{ id: 1 }];
      expect(
        reducer(undefined, {
          type: ACTIONS.SET_CART,
          payload,
        })
      ).toEqual({
        items: payload,
      });
    });
  });
});
