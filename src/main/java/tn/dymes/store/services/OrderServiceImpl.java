package tn.dymes.store.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.dymes.store.entites.*;
import tn.dymes.store.repositories.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class OrderServiceImpl implements IOrderService{

    @Autowired
    UpsellRepository upsellRepository;

    @Autowired
    OrderLifeCycleRepository orderLifeCycleRepository;

    @Autowired
    IUpsellService upsellService;

    @Autowired
    IVariantService variantService;

    @Autowired
    VariantRepository variantRepository;

    @Autowired
    IProductService productService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    IMailService mailService;

    @Autowired
    MyOrderRepository orderRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MyOrderProductDataRepository myOrderProductDataRepository;


    @Override
    public long addOrder(String date, Map<String, Object>[] checkoutDataArray, Map<String, Object>[] totalPrices, Float totalWithTax, Float shippingFees, Promotion promotion, String paymentType, String clientType) {
        boolean isEmailAvailable = false;
        MyOrder order = new MyOrder();
        // others data
        order.setStatus("En attente");
        order.setClientType(clientType);
        if (clientType.equals("GUEST")){
            order.setUser(null);
        }
        order.setPaymentType(paymentType);
        order.setTotalWithTax(totalWithTax);
        order.setDate(date);
        order.setShippingFees(shippingFees);
        order.setPromotion(promotion);
        //checkoutDataArray
        for (Map<String, Object> entry : checkoutDataArray) {
            String key = entry.get("key").toString();
            String value = entry.get("value").toString();
            if (key.equals("Nom et prenom")){
                order.setFullName(value);
            }
            if (key.equals("Pays")){
                order.setPays(value);
            }
            if (key.equals("Adresse")){
                order.setAddress(value);
            }
            if (key.equals("Code TVA")){
                order.setTVAcode(value);
            }
            if (key.equals("Région")){
                order.setState(value);
            }
            if (key.equals("Ville")){
                order.setCity(value);
            }
            if (key.equals("Code postal")){
                order.setZipCode(value);
            }
            if (key.equals("Téléphone")){
                order.setPrincipalPhone(value);
            }
            if (key.equals("Téléphone supplémentaire")){
                order.setSecodaryPhone(value);
            }
            if (key.equals("Email") && isValidEmail(value)){
                order.setEmail(value);
                isEmailAvailable = true;
            }
            if (key.equals("Commentaire")){
                order.setComment(value);
            }
        }
        //totalPrices
        List<MyOrderProductData> myOrderProductDataList = new ArrayList<>();
        for (Map<String, Object> entry : totalPrices) {
            MyOrderProductData myOrderProductData = new MyOrderProductData();
            for (Map.Entry<String, Object> mapEntry : entry.entrySet()) {
                String key = mapEntry.getKey();
                String value = mapEntry.getValue().toString();
                switch (key) {
                    case "name":
                        myOrderProductData.setName(value);
                        break;
                    case "id":
                        myOrderProductData.setProduct_id(Long.parseLong(value));
                        break;
                    case "type":
                        myOrderProductData.setType(value);
                        break;
                    case "qte":
                        myOrderProductData.setQuantity(Integer.parseInt(value));
                        break;
                    case "total":
                        myOrderProductData.setTotal(Float.parseFloat(value));
                        break;
                    case "description":
                        myOrderProductData.setDescription(value);
                        break;
                }
            }
            // stock operation
            if (myOrderProductData.getType().equals("variant")){
                Variant variant = variantRepository.findById(myOrderProductData.getProduct_id()).orElse(null);
                if (variant.getStock() > 0){
                    variant.setStock(variant.getStock() - myOrderProductData.getQuantity());
                    variantRepository.save(variant);
                }
                else {
                    return -1;
                }
            }

            if (myOrderProductData.getType().equals("product")){
                Product product = productRepository.findById(myOrderProductData.getProduct_id()).orElse(null);
                if (product.getStock() > 0){
                    product.setStock(product.getStock() - myOrderProductData.getQuantity());
                    productRepository.save(product);
                }
                else{
                    return -1;
                }
            }
            //end

            myOrderProductDataList.add(myOrderProductData);
            myOrderProductDataRepository.save(myOrderProductData);
        }
        order.setMyOrderProductsData(myOrderProductDataList);
        orderRepository.save(order);
        if (isEmailAvailable){
            this.mailService.sendEmail(order.getEmail(), "Nouvelle Commande #"+order.getId(), "Cher " + order.getFullName() +
                     ",\nMerci pour votre commande! \nNous avons bien reçu votre commande et nous vous confirmons que celle-ci est maintenant confirmée. Nous vous contacterons sous peu pour confirmer les détails de votre commande. \nCordialement, \nL'equipe Dymes. ");
        }
        return order.getId();
    }

    @Override
    public long addOrderFastCheckout(String date, String fullName, String phone, String address, Float shippingFees, Float totalWithTax, String clientType,
                                     long productID, int qte, String description, Float total, String productType, String productName) {

        MyOrder order = new MyOrder();
        order.setStatus("En attente");
        order.setClientType(clientType);
        if (clientType.equals("GUEST")){
            order.setUser(null);
        }
        order.setPaymentType("COD");
        order.setTotalWithTax(totalWithTax);
        order.setDate(date);
        order.setShippingFees(shippingFees);
        order.setFullName(fullName);
        order.setPrincipalPhone(phone);
        order.setAddress(address);

        MyOrderProductData orderProductData = new MyOrderProductData();
        orderProductData.setName(productName);
        orderProductData.setDescription(description);
        orderProductData.setType(productType);
        orderProductData.setTotal(total);
        orderProductData.setProduct_id(productID);
        orderProductData.setQuantity(qte);
        myOrderProductDataRepository.save(orderProductData);
        List<MyOrderProductData> myOrderProductDataList = new ArrayList<>();
        myOrderProductDataList.add(orderProductData);
        order.setMyOrderProductsData(myOrderProductDataList);

        // stock operation
        Product product = productRepository.findById(orderProductData.getProduct_id()).orElse(null);
           if (product.getStock() > 0){
                product.setStock(product.getStock() - orderProductData.getQuantity());
                productRepository.save(product);
            }
            else{
                return -1;
            }
        //end

        orderRepository.save(order);
        return order.getId();
    }

    @Override
    public long addOrderFastCheckoutClient(String date, String fullName, String phone, String address, Float shippingFees, Float totalWithTax, String clientType, long productID, int qte, String description, Float total, String productType, String productName, String clientID) {
        User user = userRepository.findById(clientID).orElse(null);
        if (user != null) {
            MyOrder order = new MyOrder();
            // set user to order
            order.setUser(user);
            order.setStatus("En attente");
            order.setClientType(clientType);
            order.setDate(date);
            order.setPaymentType("COD");
            order.setTotalWithTax(totalWithTax);
            order.setShippingFees(shippingFees);
            order.setFullName(fullName);
            order.setPrincipalPhone(phone);
            order.setAddress(address);

            MyOrderProductData orderProductData = new MyOrderProductData();
            orderProductData.setName(productName);
            orderProductData.setDescription(description);
            orderProductData.setType(productType);
            orderProductData.setTotal(total);
            orderProductData.setProduct_id(productID);
            orderProductData.setQuantity(qte);
            myOrderProductDataRepository.save(orderProductData);
            List<MyOrderProductData> myOrderProductDataList = new ArrayList<>();
            myOrderProductDataList.add(orderProductData);
            order.setMyOrderProductsData(myOrderProductDataList);

            // stock operation
            Product product = productRepository.findById(orderProductData.getProduct_id()).orElse(null);
            if (product.getStock() > 0) {
                product.setStock(product.getStock() - orderProductData.getQuantity());
                productRepository.save(product);
            } else {
                return -1;
            }
            //end

            orderRepository.save(order);
            return order.getId();
        }
        return -1;
    }

    @Override
    public long addClientOrder(String date, Map<String, Object>[] checkoutDataArray, Map<String, Object>[] totalPrices, Float totalWithTax, Float shippingFees, Promotion promotion, String paymentType, String clientType, String clientID) {
        User user = userRepository.findById(clientID).orElse(null);
        if (user != null){
            boolean isEmailAvailable = false;
            MyOrder order = new MyOrder();
            // set user to order
            order.setUser(user);
            // others data
            order.setDate(date);
            order.setStatus("En attente");
            order.setClientType(clientType);
            order.setPaymentType(paymentType);
            order.setTotalWithTax(totalWithTax);
            order.setShippingFees(shippingFees);
            order.setPromotion(promotion);
            //checkoutDataArray
            for (Map<String, Object> entry : checkoutDataArray) {
                String key = entry.get("key").toString();
                String value = entry.get("value").toString();
                if (key.equals("Nom et prenom")){
                    order.setFullName(value);
                }
                if (key.equals("Pays")){
                    order.setPays(value);
                }
                if (key.equals("Adresse")){
                    order.setAddress(value);
                }
                if (key.equals("Code TVA")){
                    order.setTVAcode(value);
                }
                if (key.equals("Région")){
                    order.setState(value);
                }
                if (key.equals("Ville")){
                    order.setCity(value);
                }
                if (key.equals("Code postal")){
                    order.setZipCode(value);
                }
                if (key.equals("Téléphone")){
                    order.setPrincipalPhone(value);
                }
                if (key.equals("Téléphone supplémentaire")){
                    order.setSecodaryPhone(value);
                }
                if (key.equals("Email") && isValidEmail(value)){
                    order.setEmail(value);
                    isEmailAvailable = true;
                }
                if (key.equals("Commentaire")){
                    order.setComment(value);
                }
            }
            //totalPrices
            List<MyOrderProductData> myOrderProductDataList = new ArrayList<>();
            for (Map<String, Object> entry : totalPrices) {
                MyOrderProductData myOrderProductData = new MyOrderProductData();
                for (Map.Entry<String, Object> mapEntry : entry.entrySet()) {
                    String key = mapEntry.getKey();
                    String value = mapEntry.getValue().toString();
                    switch (key) {
                        case "name":
                            myOrderProductData.setName(value);
                            break;
                        case "id":
                            myOrderProductData.setProduct_id(Long.parseLong(value));
                            break;
                        case "type":
                            myOrderProductData.setType(value);
                            break;
                        case "qte":
                            myOrderProductData.setQuantity(Integer.parseInt(value));
                            break;
                        case "total":
                            myOrderProductData.setTotal(Float.parseFloat(value));
                            break;
                        case "description":
                            myOrderProductData.setDescription(value);
                            break;
                    }
                }

                // stock operation
                if (myOrderProductData.getType().equals("variant")){
                    Variant variant = variantRepository.findById(myOrderProductData.getProduct_id()).orElse(null);
                    if (variant.getStock() > 0){
                        variant.setStock(variant.getStock() - myOrderProductData.getQuantity());
                        variantRepository.save(variant);
                    }
                    else {
                        return -1;
                    }
                }

                if (myOrderProductData.getType().equals("product")){
                    Product product = productRepository.findById(myOrderProductData.getProduct_id()).orElse(null);
                    if (product.getStock() > 0){
                        product.setStock(product.getStock() - myOrderProductData.getQuantity());
                        productRepository.save(product);
                    }
                    else{
                        return -1;
                    }
                }
                //end
                myOrderProductDataList.add(myOrderProductData);
                myOrderProductDataRepository.save(myOrderProductData);
            }
            order.setMyOrderProductsData(myOrderProductDataList);
            orderRepository.save(order);
            if (isEmailAvailable){
                this.mailService.sendEmail(order.getEmail(), "Nouvelle Commande #"+order.getId(), "Cher " + order.getFullName() +
                        ",\nMerci pour votre commande! \nNous avons bien reçu votre commande et nous vous confirmons que celle-ci est maintenant confirmée. Nous vous contacterons sous peu pour confirmer les détails de votre commande.: \nCordialement, \nL'equipe Dymes: ");
            }
            else{
                this.mailService.sendEmail(user.getEmail(), "Nouvelle Commande #"+order.getId(), "Cher " + order.getFullName() +
                        ",\nMerci pour votre commande! \nNous avons bien reçu votre commande et nous vous confirmons que celle-ci est maintenant confirmée. Nous vous contacterons sous peu pour confirmer les détails de votre commande. \nCordialement, \nL'equipe Dymes. ");
            }
            return order.getId();
        }
        return -1;
    }

    @Override
    public List<MyOrder> getOrdersByUserId(String userId) {
        List<MyOrder> orders = orderRepository.findAll();
        List<MyOrder> filtredOrders = new ArrayList<>();
        for (MyOrder order: orders) {
            if (order.getUser().getId().equals(userId)){
                filtredOrders.add(order);
            }
        }
        return filtredOrders;
    }

    @Override
    public void cancelOrder(long orderID, String userid) {
       MyOrder myOrder = orderRepository.findById(orderID).orElse(null);
       if (myOrder.getUser().getId().equals(userid)){
           myOrder.setStatus("Annulée");
           for (MyOrderProductData myOrderProductData: myOrder.getMyOrderProductsData()) {
               if (myOrderProductData.getType().equals("variant")){
                   Variant variant = variantRepository.findById(myOrderProductData.getProduct_id()).orElse(null);
                   variant.setStock(variant.getStock()+myOrderProductData.getQuantity());
                   variantRepository.save(variant);
               }
               if (myOrderProductData.getType().equals("product")){
                   Product product = productRepository.findById(myOrderProductData.getProduct_id()).orElse(null);
                   product.setStock(product.getStock()+myOrderProductData.getQuantity());
                   productRepository.save(product);
               }
           }
       }
       orderRepository.save(myOrder);
    }

    @Override
    public void changeOrderStatus(long orderid, long OLCpreviousID, long OLCNextID) {
        MyOrder myOrder = orderRepository.findById(orderid).orElse(null);
        if (myOrder != null){
            OrderLifeCycle previousOLC = orderLifeCycleRepository.findById(OLCpreviousID).orElse(null);
            OrderLifeCycle nextOLC = orderLifeCycleRepository.findById(OLCNextID).orElse(null);
            // CASE previousOLC = 0
            if (previousOLC.getAction().equals("0") && nextOLC.getAction().equals("0")){
                myOrder.setStatus(nextOLC.getStepName());
                orderRepository.save(myOrder);
            }
            if (previousOLC.getAction().equals("0") && nextOLC.getAction().equals("1")){
                for (MyOrderProductData myOrderProductData: myOrder.getMyOrderProductsData()) {
                    if (myOrderProductData.getType().equals("variant")){
                        Variant variant = variantRepository.findById(myOrderProductData.getProduct_id()).orElse(null);
                        variant.setStock(variant.getStock()+myOrderProductData.getQuantity());
                        variantRepository.save(variant);
                    }
                    if (myOrderProductData.getType().equals("product")){
                        Product product = productRepository.findById(myOrderProductData.getProduct_id()).orElse(null);
                        product.setStock(product.getStock()+myOrderProductData.getQuantity());
                        productRepository.save(product);
                    }
                }
                myOrder.setStatus(nextOLC.getStepName());
                orderRepository.save(myOrder);
            }
            if (previousOLC.getAction().equals("0") && nextOLC.getAction().equals("2")){
                for (MyOrderProductData myOrderProductData: myOrder.getMyOrderProductsData()) {
                    if (myOrderProductData.getType().equals("variant")){
                        Variant variant = variantRepository.findById(myOrderProductData.getProduct_id()).orElse(null);
                        variant.setStock(variant.getStock()-myOrderProductData.getQuantity());
                        variantRepository.save(variant);
                    }
                    if (myOrderProductData.getType().equals("product")){
                        Product product = productRepository.findById(myOrderProductData.getProduct_id()).orElse(null);
                        product.setStock(product.getStock()-myOrderProductData.getQuantity());
                        productRepository.save(product);
                    }
                }
                myOrder.setStatus(nextOLC.getStepName());
                orderRepository.save(myOrder);
            }
            // CASE previousOLC = 1
            if (previousOLC.getAction().equals("1") && nextOLC.getAction().equals("0")){
                for (MyOrderProductData myOrderProductData: myOrder.getMyOrderProductsData()) {
                    if (myOrderProductData.getType().equals("variant")){
                        Variant variant = variantRepository.findById(myOrderProductData.getProduct_id()).orElse(null);
                        variant.setStock(variant.getStock()-myOrderProductData.getQuantity());
                        variantRepository.save(variant);
                    }
                    if (myOrderProductData.getType().equals("product")){
                        Product product = productRepository.findById(myOrderProductData.getProduct_id()).orElse(null);
                        product.setStock(product.getStock()-myOrderProductData.getQuantity());
                        productRepository.save(product);
                    }
                }
                myOrder.setStatus(nextOLC.getStepName());
                orderRepository.save(myOrder);
            }
            if (previousOLC.getAction().equals("1") && nextOLC.getAction().equals("1")){
                myOrder.setStatus(nextOLC.getStepName());
                orderRepository.save(myOrder);
            }
            if (previousOLC.getAction().equals("1") && nextOLC.getAction().equals("2")){
                for (MyOrderProductData myOrderProductData: myOrder.getMyOrderProductsData()) {
                    if (myOrderProductData.getType().equals("variant")){
                        Variant variant = variantRepository.findById(myOrderProductData.getProduct_id()).orElse(null);
                        variant.setStock(variant.getStock()-myOrderProductData.getQuantity());
                        variantRepository.save(variant);
                    }
                    if (myOrderProductData.getType().equals("product")){
                        Product product = productRepository.findById(myOrderProductData.getProduct_id()).orElse(null);
                        product.setStock(product.getStock()-myOrderProductData.getQuantity());
                        productRepository.save(product);
                    }
                }
                myOrder.setStatus(nextOLC.getStepName());
                orderRepository.save(myOrder);
            }
            // CASE previousOLC = 2
            if (previousOLC.getAction().equals("2") && nextOLC.getAction().equals("0")){
                for (MyOrderProductData myOrderProductData: myOrder.getMyOrderProductsData()) {
                    if (myOrderProductData.getType().equals("variant")){
                        Variant variant = variantRepository.findById(myOrderProductData.getProduct_id()).orElse(null);
                        variant.setStock(variant.getStock()-myOrderProductData.getQuantity());
                        variantRepository.save(variant);
                    }
                    if (myOrderProductData.getType().equals("product")){
                        Product product = productRepository.findById(myOrderProductData.getProduct_id()).orElse(null);
                        product.setStock(product.getStock()-myOrderProductData.getQuantity());
                        productRepository.save(product);
                    }
                }
                myOrder.setStatus(nextOLC.getStepName());
                orderRepository.save(myOrder);
            }
            if (previousOLC.getAction().equals("2") && nextOLC.getAction().equals("1")){
                for (MyOrderProductData myOrderProductData: myOrder.getMyOrderProductsData()) {
                    if (myOrderProductData.getType().equals("variant")){
                        Variant variant = variantRepository.findById(myOrderProductData.getProduct_id()).orElse(null);
                        variant.setStock(variant.getStock()-myOrderProductData.getQuantity());
                        variantRepository.save(variant);
                    }
                    if (myOrderProductData.getType().equals("product")){
                        Product product = productRepository.findById(myOrderProductData.getProduct_id()).orElse(null);
                        product.setStock(product.getStock()-myOrderProductData.getQuantity());
                        productRepository.save(product);
                    }
                }
                myOrder.setStatus(nextOLC.getStepName());
                orderRepository.save(myOrder);
            }
            if (previousOLC.getAction().equals("2") && nextOLC.getAction().equals("2")){
                myOrder.setStatus(nextOLC.getStepName());
                orderRepository.save(myOrder);
            }
        }
    }


    boolean isValidEmail(String email) {
        String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
