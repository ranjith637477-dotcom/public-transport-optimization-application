import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  token: localStorage.getItem('tnsmartbus_token') || null,
  userId: localStorage.getItem('tnsmartbus_user_id') || null,
  fullName: localStorage.getItem('tnsmartbus_full_name') || null,
  role: localStorage.getItem('tnsmartbus_role') || null,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setCredentials(state, action) {
      const { token, userId, fullName, role } = action.payload;
      state.token = token;
      state.userId = userId;
      state.fullName = fullName;
      state.role = role;
      localStorage.setItem('tnsmartbus_token', token);
      localStorage.setItem('tnsmartbus_user_id', userId);
      localStorage.setItem('tnsmartbus_full_name', fullName);
      localStorage.setItem('tnsmartbus_role', role);
    },
    logout(state) {
      state.token = null;
      state.userId = null;
      state.fullName = null;
      state.role = null;
      localStorage.removeItem('tnsmartbus_token');
      localStorage.removeItem('tnsmartbus_user_id');
      localStorage.removeItem('tnsmartbus_full_name');
      localStorage.removeItem('tnsmartbus_role');
    },
  },
});

export const { setCredentials, logout } = authSlice.actions;
export default authSlice.reducer;
