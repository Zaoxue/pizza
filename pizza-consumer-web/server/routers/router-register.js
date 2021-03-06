const Router = require('koa-router');
const { Root } = require('protobufjs');
const User = require('../entity/User');
const proto = require('../proto.json');
const net = require('../net');
const argv = require('yargs').argv;

const isMock = argv.isMock === 'true'
const root = Root.fromJSON(proto);
const reqProtoType = 'user.SignUpReq';
const respProtoType = 'user.SignUpResp';
const reqType = root.lookupType(reqProtoType);
const respType = root.lookupType(respProtoType);

const router = new Router();

router.post('/', async (ctx, next) => {
  const protoBuff = ctx.proto;
  const result = reqType.decode(protoBuff);

  // mock
  let body;

  if (isMock) {
    body = {
      resultType: 1,
      user: User.random(),
    };
  } else {
    try {
      body = await net.post('/addUser', result);
    } catch (e) {
      body = {
        resultType: 0,
      }
    }
  }

  const decodeBody = respType.encode(respType.create(body)).finish();

  ctx.body = decodeBody;
  await next();
});

module.exports = {
  router,
  command: 'REGISTER',
  reqUrl: '/add_user',
};
