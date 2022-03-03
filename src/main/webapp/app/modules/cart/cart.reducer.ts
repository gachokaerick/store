import { ICatalogItem } from 'app/shared/model/catalog/catalog-item.model';
import { ACTIONS } from 'app/config/constants';
import { ActionReducerMapBuilder, AnyAction, createSlice } from '@reduxjs/toolkit';

export interface CartState<T> {
  items: ReadonlyArray<T>;
}

const initialState: CartState<ICatalogItem> = {
  items: [],
};

// Actions

export const addItemToCart = (item: ICatalogItem) => {
  return { type: ACTIONS.ADD_TO_CART, payload: item };
};

export const removeItemFromCart = (item: ICatalogItem) => {
  return { type: ACTIONS.REMOVE_FROM_CART, payload: item };
};

export const setCartItems = (items: ReadonlyArray<ICatalogItem>) => {
  return { type: ACTIONS.SET_CART, payload: items };
};

// slice

export const CartSlice = createSlice({
  name: 'Cart',
  initialState,
  reducers: {},
  extraReducers(builder: ActionReducerMapBuilder<CartState<ICatalogItem>>) {
    builder
      .addCase(ACTIONS.ADD_TO_CART, (state, action: AnyAction) => {
        if (action.payload) {
          state.items.filter(it => it.id === action.payload.id).length === 0 ? (state.items = [...state.items, action.payload]) : null;
        }
      })
      .addCase(ACTIONS.REMOVE_FROM_CART, (state, action: AnyAction) => {
        if (action.payload) {
          state.items = state.items.filter(it => it.id !== action.payload.id);
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
