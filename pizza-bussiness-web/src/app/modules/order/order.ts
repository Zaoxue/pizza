/**
 * Define the Order making the order list
 */

export interface Order {
  order_id: string;
  receive_name: string;
  phone: string;
  receive_address: string;
  total_amount: number;
  create_time: string;
  state: string;
}
