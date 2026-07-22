import { configureStore } from '@reduxjs/toolkit';
import inventoryReducer from './slices/inventorySlice';
import orderReducer from './slices/orderSlice';

export const store = configureStore({
  reducer: {
    inventory: inventoryReducer,
    orders: orderReducer,
  },
});
