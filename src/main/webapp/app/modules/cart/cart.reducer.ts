import { ICartCatalogItem, ICatalogItem, itemToCartItem } from 'app/shared/model/catalog/catalog-item.model';
import { ACTIONS } from 'app/config/constants';
import { ActionReducerMapBuilder, AnyAction, createSlice } from '@reduxjs/toolkit';

export interface CartState<T> {
  items: ReadonlyArray<T>;
}

const initialState: CartState<ICartCatalogItem> = {
  items: [],
};

// Actions

export const addItemToCart = (item: ICatalogItem) => {
  return { type: ACTIONS.ADD_TO_CART, payload: item };
};

export const removeItemFromCart = (item: ICatalogItem) => {
  return { type: ACTIONS.REMOVE_FROM_CART, payload: item };
};

export const reduceFromCart = (item: ICatalogItem, quantity = 1) => {
  return { type: ACTIONS.REDUCE_FROM_CART, payload: { item, quantity } };
};

export const setCartItems = (items: ReadonlyArray<ICartCatalogItem>) => {
  return { type: ACTIONS.SET_CART, payload: items };
};

// slice

export const CartSlice = createSlice({
  name: 'Cart',
  initialState,
  reducers: {},
  extraReducers(builder: ActionReducerMapBuilder<CartState<ICartCatalogItem>>) {
    builder
      .addCase(ACTIONS.ADD_TO_CART, (state, action: AnyAction) => {
        if (action.payload) {
          const payloadItem: ICartCatalogItem = itemToCartItem(action.payload);
          const match = state.items.filter(it => it.id === payloadItem.id);
          if (match.length === 0) {
            state.items = [...state.items, payloadItem];
          } else {
            state.items = state.items.map(item => (item.id === payloadItem.id ? { ...item, quantity: item.quantity + 1 } : item));
          }
        }
      })
      .addCase(ACTIONS.REMOVE_FROM_CART, (state, action: AnyAction) => {
        if (action.payload) {
          const item: ICatalogItem = action.payload;
          state.items = state.items.filter(it => it.id !== item.id);
        }
      })
      .addCase(ACTIONS.REDUCE_FROM_CART, (state, action: AnyAction) => {
        if (action.payload) {
          const payloadItem: ICatalogItem = action.payload.item;
          const payloadQuantity: number = action.payload.quantity;
          state.items = state.items
            .map(item =>
              item.id === payloadItem.id
                ? item.quantity - payloadQuantity > 0
                  ? { ...item, quantity: item.quantity - payloadQuantity }
                  : null
                : item
            )
            .filter(item => item !== null);
        }
      })
      .addCase(ACTIONS.SET_CART, (state, action: AnyAction) => {
        if (action.payload) {
          state.items = action.payload;
        }
      });
  },
});

// Reducer
export default CartSlice.reducer;
