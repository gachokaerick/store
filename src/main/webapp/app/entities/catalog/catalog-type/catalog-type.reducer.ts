import axios from 'axios';
import { AnyAction, createAsyncThunk, isFulfilled, isPending, isRejected } from '@reduxjs/toolkit';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { IQueryParams, createEntitySlice, EntityState, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { ICatalogType, defaultValue } from 'app/shared/model/catalog/catalog-type.model';
import { ACTIONS } from 'app/config/constants';

const initialState: EntityState<ICatalogType> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
  selectedItem: defaultValue,
};

const apiUrl = 'services/catalog/api/catalog-types';

// Actions

export const getEntities = createAsyncThunk('catalogType/fetch_entity_list', async ({ page, size, sort }: IQueryParams) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}&` : '?'}cacheBuster=${new Date().getTime()}`;
  return axios.get<ICatalogType[]>(requestUrl);
});

export const getEntity = createAsyncThunk(
  'catalogType/fetch_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return axios.get<ICatalogType>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

export const createEntity = createAsyncThunk(
  'catalogType/create_entity',
  async (entity: ICatalogType, thunkAPI) => {
    const result = await axios.post<ICatalogType>(apiUrl, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const updateEntity = createAsyncThunk(
  'catalogType/update_entity',
  async (entity: ICatalogType, thunkAPI) => {
    const result = await axios.put<ICatalogType>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const partialUpdateEntity = createAsyncThunk(
  'catalogType/partial_update_entity',
  async (entity: ICatalogType, thunkAPI) => {
    const result = await axios.patch<ICatalogType>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const deleteEntity = createAsyncThunk(
  'catalogType/delete_entity',
  async (id: string | number, thunkAPI) => {
    const requestUrl = `${apiUrl}/${id}`;
    const result = await axios.delete<ICatalogType>(requestUrl);
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const selectType = (entityId: number) => {
  return { type: ACTIONS.SELECT_TYPE, payload: entityId };
};

// slice

export const CatalogTypeSlice = createEntitySlice({
  name: 'catalogType',
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
      .addCase(ACTIONS.SELECT_TYPE, (state, action: AnyAction) => {
        // eslint-disable-next-line no-console
        console.log('type payload: ', action.payload);
        if (action.payload) {
          state.selectedItem = state.entities.filter(it => it.id.toString() === action.payload)[0];
        } else {
          state.selectedItem = defaultValue;
        }
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

export const { reset } = CatalogTypeSlice.actions;

// Reducer
export default CatalogTypeSlice.reducer;
