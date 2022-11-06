import { ICartCatalogItem } from 'app/shared/model/catalog/catalog-item.model';
import reducer, { addItemToCart, CartState, reduceFromCart, removeItemFromCart, setCartItems } from 'app/modules/cart/cart.reducer';
import configureStore from 'redux-mock-store';
import { ACTIONS } from 'app/config/constants';

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

    it('dispatches REDUCE_FROM_CART action', () => {
      store.dispatch(reduceFromCart({}, 2));
      expect(store.getActions()[0]).toMatchObject({
        type: ACTIONS.REDUCE_FROM_CART,
        payload: {},
      });
    });
  });

  describe('Successes', () => {
    it('should add item to cart', () => {
      const payload = { id: 1, price: 12 };
      expect(
        reducer(undefined, {
          type: ACTIONS.ADD_TO_CART,
          payload,
        })
      ).toEqual({
        items: [{ id: 1, price: 12, quantity: 1 }],
      });
    });

    it('should reduce item from cart', () => {
      const payload = { item: { id: 1 }, quantity: 1 };
      expect(
        reducer(undefined, {
          type: ACTIONS.REDUCE_FROM_CART,
          payload,
        })
      ).toEqual({
        items: [],
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
        items: [],
      });
    });

    it('should set cart', () => {
      const payload = [{ id: 1, price: 10, quantity: 1 }];
      expect(
        reducer(undefined, {
          type: ACTIONS.SET_CART,
          payload,
        })
      ).toEqual({
        items: [{ id: 1, price: 10, quantity: 1 }],
      });
    });
  });
});
