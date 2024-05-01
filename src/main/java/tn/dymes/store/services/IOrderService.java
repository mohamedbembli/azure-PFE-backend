package tn.dymes.store.services;

import tn.dymes.store.entites.MyOrder;
import tn.dymes.store.entites.Promotion;

import java.util.List;
import java.util.Map;

public interface IOrderService {
    long addOrder(String date, Map<String, Object>[] checkoutDataArray, Map<String, Object>[] totalPrices, Float totalWithTax, Float shippingFees, Promotion promotion, String paymentType, String clientType);
    long addOrderFastCheckout(String date, String fullName, String phone, String address, Float shippingFees, Float totalWithTax, String clientType,
                              long productID, int qte, String description, Float total, String productType, String productName);

    long addOrderFastCheckoutClient(String date, String fullName, String phone, String address, Float shippingFees, Float totalWithTax, String clientType,
                              long productID, int qte, String description, Float total, String productType, String productName, String clientID);
    long addClientOrder(String date, Map<String, Object>[] checkoutDataArray, Map<String, Object>[] totalPrices, Float totalWithTax, Float shippingFees, Promotion promotion, String paymentType, String clientType, String clientID);
    List<MyOrder> getOrdersByUserId(String userId);
    void cancelOrder(long orderID, String userid);

    void changeOrderStatus(long orderid, long OLCpreviousID, long OLCNextID);


}
