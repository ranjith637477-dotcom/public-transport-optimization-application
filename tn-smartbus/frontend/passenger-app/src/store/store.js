import { configureStore } from '@reduxjs/toolkit';
import authReducer from './authSlice.js';
import languageReducer from './languageSlice.js';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    language: languageReducer,
  },
});
