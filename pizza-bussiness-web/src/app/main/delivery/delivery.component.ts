import {Component, OnInit} from '@angular/core';
import {Delivery} from "../../modules/delivery/delivery";
import {SystemManageService} from "../../services/system-manage/system-manage.service";
import {DeliveryService} from "../../services/delivery/delivery.service";

@Component({
  selector: 'app-delivery',
  templateUrl: './delivery.component.html',
  styleUrls: ['./delivery.component.scss']
})
export class DeliveryComponent implements OnInit {

  cols: any[];
  deliveries: Delivery[];
  tempDel: Delivery;
  dialogHeader: string;
  displayChangeDialog: boolean;
  displayAddDialog: boolean;

  constructor(private deliveryService: DeliveryService) {
  }

  ngOnInit() {
    this.dialogHeader = '';
    this.displayChangeDialog = false;
    this.displayAddDialog = false;


    this.cols = [
      {field: 'id', header: '配送员ID'},
      {field: 'name', header: '姓名'},
      {field: 'phone', header: '手机号'},
      {field: 'shopId', header: '绑定的店ID'},
    ];

  }

  addDelivery() {
    this.tempDel = new Delivery();
    this.dialogHeader = '新增配送员';
    this.displayAddDialog = true;
  }

  onRowCancel(del: Delivery) {
    if (this.deliveries.find(obj => obj.id == del.id) != null) {
      this.deliveries = this.deliveries.filter(obj => obj.id != del.id);
    }
  }

  editDelivery(del: Delivery) {
    this.dialogHeader = '配送员基本信息修改';
    this.tempDel = del;
    this.displayChangeDialog = true;
  }

  submitChangedDel() {
    this.displayChangeDialog = false;
    this.tempDel = null;
  }

  submitAddedDel() {
    this.displayAddDialog = false;
    this.tempDel = null;
  }

  closeDialog() {
    this.displayChangeDialog = false;
    this.displayAddDialog = false;
    this.tempDel = null;
  }


}
