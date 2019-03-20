import * as React from 'react';
import './index.scss';
import Banner from '@biz-components/Banner';
import { OpenType } from '@biz-components/PageAssets';
import i18n from '@utils/i18n';
import { MainAssetName } from '../index';
import autobind from 'autobind-decorator';
import { neetStatusBar } from '@utils/device';
import cx from 'classnames';
import Icon from '@biz-components/Icon';
import { CART_ORDER_ID } from '@entity/Order';

interface SettlementProps {
  onPageChange(idx: MainAssetName, openType?: OpenType, ...extraInfo: any[]): void;
  entityStore: any;
}

interface SettlementState {
}

export default class Settlement extends React.PureComponent<SettlementProps, SettlementState> {

  @autobind
  handleOrderClick() {
    const { onPageChange } = this.props;
    onPageChange(MainAssetName.Pay, OpenType.RIGHT);
  }

  @autobind
  handleLeftClick(e: React.MouseEvent<React.ReactNode>) {
    const { onPageChange } = this.props;
    onPageChange(MainAssetName.Main, OpenType.LEFT);
  }

  renderMiddle() {
    return <span className="settlement-middle">{i18n('确认订单')}</span>;
  }

  render() {
    const { entityStore } = this.props;
    const { pizzas, orders } = entityStore;

    const currOrder = orders[CART_ORDER_ID] || {};
    const pizzaIds = currOrder.pizzas;

    let price = 0;
    if (currOrder && currOrder.num) {
      const { num } = currOrder;
      price = Object.entries(num).reduce((prev, [pizzaId, count]) => {
        const currPizza = pizzas[parseInt(pizzaId, 10)];
        const perPrice = currPizza.price;
        return prev + (count as number) * perPrice;
      }, 0);
    }

    return (
      <div className="settlement-wrapper">
        <Banner
          right={null}
          middle={this.renderMiddle()}
          leftClick={this.handleLeftClick}
        />
        <div className={cx({
          'settlement-content': true,
          'settlement-content_status': neetStatusBar,
        })}>
          <div className="settlement-pizzas">
            <div className="settlement-pizzasTitle">
              {i18n('商品信息')}
            </div>
            {
              pizzaIds && pizzaIds.map((pizzaId: number) => {
                const currPizza = pizzas[pizzaId];
                const currNums = currOrder.num;
                const currNum = currNums[pizzaId];

                if (currNum === 0) {
                  return null;
                }

                return (
                  <div className="settlement-pizzaItem" key={pizzaId}>
                    <div className="settlement-pizzaItemImage">
                      <Icon name="pisa" classnames="settlement-pizzaItemPisa" />
                    </div>
                    <div className="settlement-pizzaItemName">
                      {currPizza.name}
                    </div>
                    <div className="settlement-pizzaItemNum">
                      <span>{i18n('x')}</span>
                      {currNum}
                    </div>
                    <div className="settlement-pizzaItemPrice">
                      <span>{i18n('¥')}</span>
                      {currPizza.price}
                    </div>
                  </div>
                );
              })
            }
          </div>
          <div className="settlement-sum">
            <span className="settlement-prompt">
              {i18n('应付合计:')}
            </span>
            <span className="settlement-unit">
              {i18n('¥')}
            </span>
            <span className="settlement-totalPrice">
              {price}
            </span>
            <span
              className="settlement-order"
              onClick={this.handleOrderClick}
            >
              {i18n('下单')}
            </span>
          </div>
        </div>
      </div>
    );
  }
}
