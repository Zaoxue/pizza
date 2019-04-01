import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {AddressService} from "../address/address.service";
import {map} from "rxjs/operators";
import {Delivery} from "../../modules/delivery/delivery";

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json',
  })
};

@Injectable({
  providedIn: 'root'
})
export class DeliveryService {

  constructor(private http: HttpClient) { }

  getDriverList():Observable<any> {
    return this.http.get(AddressService.getDriverList(), httpOptions)
      .pipe(map((result: Response) => result.json()))
  }

  editDriver(driver: Delivery):Observable<any> {
    return this.http.post(AddressService.editDriver(), driver, httpOptions)
      .pipe(map((result: Response) => result.json()))
  }

  addDriver(driver: Delivery):Observable<any> {
    return this.http.post(AddressService.addDriver(), driver, httpOptions)
      .pipe(map((result: Response) => result.json()))
  }

}
