import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  lang: localStorage.getItem('tnsmartbus_lang') || 'en', // 'en' | 'ta'
};

const languageSlice = createSlice({
  name: 'language',
  initialState,
  reducers: {
    setLanguage(state, action) {
      state.lang = action.payload;
      localStorage.setItem('tnsmartbus_lang', action.payload);
    },
    toggleLanguage(state) {
      state.lang = state.lang === 'en' ? 'ta' : 'en';
      localStorage.setItem('tnsmartbus_lang', state.lang);
    },
  },
});

export const { setLanguage, toggleLanguage } = languageSlice.actions;
export default languageSlice.reducer;
