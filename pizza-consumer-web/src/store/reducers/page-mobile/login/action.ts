/**
 * action type
 */
export const UPDATE_NAV_IDX = Symbol('UPDATE_NAV_IDX');
export const UPDATE_VARIFY_TIME = Symbol('UPDATE_VARIFY_TIME');
export const UPDATE_SET_VARIFY_TIME = Symbol('UPDATE_VARIFY_TIME');
export const UPDATE_REGISTER_VARIFY_TIME = Symbol('UPDATE_REGISTER_VARIFY_TIME');
export const UPDATE_BIND_VARIFY_TIME = Symbol('UPDATE_BIND_VARIFY_TIME');

/**
 * action creator
 */
const updateNavIdx = (navIdx: number) => ({
  navIdx,
  type: UPDATE_NAV_IDX,
});

const updateVarifyTime = (varifyTime: number) => ({
  varifyTime,
  type: UPDATE_VARIFY_TIME,
});

const updateSetVarifyTime = (varifyTime: number) => ({
  varifyTime,
  type: UPDATE_SET_VARIFY_TIME,
});

const updateRegisterVarifyTime = (varifyTime: number) => ({
  varifyTime,
  type: UPDATE_REGISTER_VARIFY_TIME,
});

const updateBindVarifyTime = (varifyTime: number) => ({
  varifyTime,
  type: UPDATE_BIND_VARIFY_TIME,
});

export default {
  updateNavIdx,
  updateVarifyTime,
  updateSetVarifyTime,
  updateRegisterVarifyTime,
  updateBindVarifyTime,
};
