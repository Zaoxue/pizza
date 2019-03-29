const Router = require('koa-router');
const { Root } = require('protobufjs');
const proto = require('../proto.json');
const net = require('../net');
const argv = require('yargs').argv;

const isMock = argv.isMock === 'true';
const root = Root.fromJSON(proto);
const reqProtoType = 'order.CancelOrderReq';
const respProtoType = 'order.CancelOrderResp';
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
    };
  } else {
    try {
      response = await net.post('/cancelOrder', result);
      body = {
        resultType: 1,
      }
    } catch (e) {
      body = {
        resultType: 0,
      }
    }
  }

  const decodeBody = respType.encode(respType.create(body)).finish();

  ctx.body = decodeBody;
  next();
});

module.exports = {
  router,
  command: 'CANCEL_ORDER',
  reqUrl: '/cancel_order',
};
