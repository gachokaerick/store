import axios from 'axios';
import { AnyAction, createAsyncThunk, isFulfilled, isPending, isRejected } from '@reduxjs/toolkit';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { IQueryParams, createEntitySlice, EntityState, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { IAddress, defaultValue } from 'app/shared/model/orders/address.model';
import { ACTIONS } from 'app/config/constants';

const initialState: EntityState<IAddress> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export type IAddressQueryParams = {
  query?: string;
  page?: number;
  size?: number;
  sort?: string;
  login?: string;
};

const apiUrl = 'services/orders/api/addresses';

// Actions

export const getEntities = createAsyncThunk('address/fetch_entity_list', async ({ page, size, sort, login }: IAddressQueryParams) => {
  let requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}&` : '?'}cacheBuster=${new Date().getTime()}`;
  if (login) {
    requestUrl = requestUrl + `&login=${login}`;
  }
  return axios.get<IAddress[]>(requestUrl);
});

export const getEntity = createAsyncThunk(
  'address/fetch_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return axios.get<IAddress>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

export const createEntity = createAsyncThunk(
  'address/create_entity',
  async (entity: IAddress, thunkAPI) => {
    const result = await axios.post<IAddress>(apiUrl, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const updateEntity = createAsyncThunk(
  'address/update_entity',
  async (entity: IAddress, thunkAPI) => {
    const result = await axios.put<IAddress>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const partialUpdateEntity = createAsyncThunk(
  'address/partial_update_entity',
  async (entity: IAddress, thunkAPI) => {
    const result = await axios.patch<IAddress>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const deleteEntity = createAsyncThunk(
  'address/delete_entity',
  async (id: string | number, thunkAPI) => {
    const requestUrl = `${apiUrl}/${id}`;
    const result = await axios.delete<IAddress>(requestUrl);
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const selectAddress = (entityId: number) => {
  return { type: ACTIONS.SELECT_ADDRESS, payload: entityId };
};

// slice

export const AddressSlice = createEntitySlice({
  name: 'address',
  initialState,
  extraReducers(builder) {
    builder
      .addCase(getEntity.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload.data;
      })
      .addCase(deleteEntity.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
      })
      .addMatcher(isFulfilled(getEntities), (state, action) => {
        return {
          ...state,
          loading: false,
          entities: action.payload.data,
          totalItems: parseInt(action.payload.headers['x-total-count'], 10),
        };
      })
      .addMatcher(isFulfilled(createEntity, updateEntity, partialUpdateEntity), (state, action) => {
        state.updating = false;
        state.loading = false;
        state.updateSuccess = true;
        state.entity = action.payload.data;
      })
      .addMatcher(isPending(getEntities, getEntity), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.loading = true;
      })
      .addMatcher(isPending(createEntity, updateEntity, partialUpdateEntity, deleteEntity), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.updating = true;
      });
  },
});

export const { reset } = AddressSlice.actions;

// Reducer
export default AddressSlice.reducer;
