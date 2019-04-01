const _ = require('lodash');

const CHARS = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'.split('');
const LENGTH = CHARS.length;

const getRandomStr = (num) =>
  _.range(num).reduce(
    (prev) => `${prev}${CHARS[_.random(0, LENGTH - 1)]}`,
    ''
  );


const NUM_CHARS_ARR = [1, 2, 3, 4, 5, 6, 7, 8, 9, 0];
const NUM_CHARS_ARR_LENGTH = NUM_CHARS_ARR.length;

const getRandomNum = (num) => {
  const rightPart = _.range(num - 1).reduce(
    (prev) => `${prev}${NUM_CHARS_ARR[_.random(0, NUM_CHARS_ARR_LENGTH - 1)]}`,
    ''
  )
  const leftPart = NUM_CHARS_ARR[_.random(0, NUM_CHARS_ARR_LENGTH - 2)]
  return parseInt(`${leftPart}${rightPart}`, 10);
}

module.exports = {
  getRandomStr,
  getRandomNum,
}
