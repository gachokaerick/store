import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending, isRejected } from '@reduxjs/toolkit';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { IQueryParams, createEntitySlice, EntityState, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { ICatalogItem, defaultValue } from 'app/shared/model/catalog/catalog-item.model';

const initialState: EntityState<ICatalogItem> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export type CatalogItemsQueryParams = {
  query?: string;
  page?: number;
  size?: number;
  sort?: string;
  ids?: string;
  name?: string;
  description?: string;
  catalogBrand?: string;
  catalogType?: string;
  term?: string;
};

const apiUrl = 'services/catalog/api/catalog-items';

// Actions

export const getEntities = createAsyncThunk(
  'catalogItem/fetch_entity_list',
  async ({ page, size, sort, catalogBrand, catalogType, ids }: CatalogItemsQueryParams) => {
    let requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}&` : '?'}cacheBuster=${new Date().getTime()}`;
    if (catalogBrand) {
      requestUrl = requestUrl + `&catalogBrand=${catalogBrand}`;
    }
    if (catalogType) {
      requestUrl = requestUrl + `&catalogType=${catalogType}`;
    }
    if (ids) {
      requestUrl = requestUrl + `&ids=${ids}`;
    }
    return axios.get<ICatalogItem[]>(requestUrl);
  }
);

export const getEntity = createAsyncThunk(
  'catalogItem/fetch_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return axios.get<ICatalogItem>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

export const createEntity = createAsyncThunk(
  'catalogItem/create_entity',
  async (entity: ICatalogItem, thunkAPI) => {
    const result = await axios.post<ICatalogItem>(apiUrl, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const updateEntity = createAsyncThunk(
  'catalogItem/update_entity',
  async (entity: ICatalogItem, thunkAPI) => {
    const result = await axios.put<ICatalogItem>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const partialUpdateEntity = createAsyncThunk(
  'catalogItem/partial_update_entity',
  async (entity: ICatalogItem, thunkAPI) => {
    const result = await axios.patch<ICatalogItem>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const deleteEntity = createAsyncThunk(
  'catalogItem/delete_entity',
  async (id: string | number, thunkAPI) => {
    const requestUrl = `${apiUrl}/${id}`;
    const result = await axios.delete<ICatalogItem>(requestUrl);
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

// slice

export const CatalogItemSlice = createEntitySlice({
  name: 'catalogItem',
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

export const { reset } = CatalogItemSlice.actions;

// Reducer
export default CatalogItemSlice.reducer;
