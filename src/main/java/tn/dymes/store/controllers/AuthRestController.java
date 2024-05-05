package tn.dymes.store.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;
import tn.dymes.store.entites.*;
import tn.dymes.store.repositories.*;
import tn.dymes.store.services.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
public class AuthRestController {

    @Autowired
    UpsellRepository upsellRepository;

    @Autowired
    MyOrderRepository orderRepository;

    @Autowired
    IRatingService ratingService;

    @Autowired
    RatingRepository ratingRepository;
    @Autowired
    private ICheckoutFieldService checkoutFieldService;

    @Autowired
    private IAuthService authService;

    @Autowired
    IOrderService orderService;

    @Autowired
    IAttributeService attributeService;

    @Autowired
    IPromotionService promotionService;

    @Autowired
    AttributeRepository attributeRepository;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    OrderLifeCycleRepository orderLifeCycleRepository;

    @Autowired
    IOrderLifeCycleService orderLifeCycleService;

    @Autowired
    IStoreService storeService;

    @Autowired
    IUpsellService upsellService;

    @Autowired
    private IUserService userService;

    @Autowired
    ProductImageRepository productImageRepository;

    @Autowired
    IProductImageService productImageService;

    @Autowired
    private VariantRepository variantRepository;

    @Autowired
    private IVariantService variantService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private IProductService productService;

    @Autowired
    private IProductCategoryService productCategoryService;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    JwtDecoder jwtDecoder;


    @GetMapping("/public/messageTest")
   // @PreAuthorize("hasAuthority('SCOPE_USER')")
    public Map<String, Object> messageTest(){
        return Map.of("message","Hello From");
    }

    @PostMapping("/addMessage")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public Map<String, Object> addMessage(String message){
        return Map.of("message","message added success = "+message

        );
    }

    @PostMapping("/token")
    public Map<String, String> token(Authentication authentication){
        String email = authentication.getName();
        return authService.generateToken(email,false);

    }

    @PostMapping(path = "/public/updatePassword")
    public ResponseEntity<Map<String, String>> updatePass(String pass, String email) {
        try {
            User user = userService.findUserByEmail(email);
            if (user!= null) {
                this.authService.resetPassword(pass, email);
                return new ResponseEntity<>(Map.of("message", "Votre mot de passe à été changé avec succès."), HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(Map.of("message","Réssayer!"),HttpStatus.OK);
            }
        } catch (Exception e) {
            // Return an error response if any exception occurs during the upload process.
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.OK);
        }
    }

    @PostMapping(path = "/public/requestForPasswordInit")
    public ResponseEntity<Map<String, String>>  authorizePasswordInitialization(String authorizationCode, String email){
        try {
            if (this.authService.authorizePasswordInitialization(authorizationCode, email).equals("Merci!"))
                return ResponseEntity.ok(Map.of("message","Votre code secret est valide"));
            else
                return ResponseEntity.ok(Map.of("message",this.authService.authorizePasswordInitialization(authorizationCode, email)));
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("messsage",e.getMessage()),HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/public/forgotPassword")
    public ResponseEntity<Map<String, String>> forgotPassword(String email){
        try {
            User user = userService.findUserByEmail(email);
            if (user!= null){
                this.authService.sendActivationCode(email);
                return ResponseEntity.ok(Map.of("message","Le code d'activation a été envoyé à "+email));
            }
            else {
                return new ResponseEntity<>(Map.of("message","Cet e-mail n'est associé à aucun compte."),HttpStatus.OK);
            }
        }catch (Exception e) {
            return new ResponseEntity<>(Map.of("message","Erreur interne"),HttpStatus.OK);
        }
    }


    @GetMapping(path = "/public/getStoreData")
    public ResponseEntity<Store> getStoreDataPublic() {
        try {
            Store store = this.storeRepository.findById("Dymes").orElse(null);
            return new ResponseEntity<>(store, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping(value = "/public/OLC/{stepID}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]> getPublicOLCPhoto(@PathVariable long stepID) throws IOException {
        try{
            OrderLifeCycle orderLifeCycle = this.orderLifeCycleRepository.findById(stepID).orElse(null);
            if (orderLifeCycle!= null){
                System.out.println("test value image = "+this.orderLifeCycleService.getPublicPhoto(orderLifeCycle.getId()));
                // Return the file content as a ResponseEntity
                return ResponseEntity.ok()
                        .body(this.orderLifeCycleService.getPublicPhoto(orderLifeCycle.getId()));
            }
            else{
                // Handle file not found or not readable
                return ResponseEntity.notFound().build();
            }
        }
        catch(IOException e){
            // Handle exceptions
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping(value = "/public/variant/{variantID}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]>  getPublicVariantPhoto(@PathVariable long variantID) throws IOException {
        try{
            Variant variant = this.variantRepository.findById(variantID).orElse(null);
            if (variant!= null){
                System.out.println("test value image = "+this.variantService.getPublicPhoto(variant.getId()));
                // Return the file content as a ResponseEntity
                return ResponseEntity.ok()
                        .body(this.variantService.getPublicPhoto(variant.getId()));
            }
            else{
                // Handle file not found or not readable
                return ResponseEntity.notFound().build();
            }
        }
        catch(IOException e){
            // Handle exceptions
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/public/productImage/{productImageID}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]>  getPublicProductImagePhoto(@PathVariable long productImageID) throws IOException {
        try{
            ProductImage productImage = this.productImageRepository.findById(productImageID).orElse(null);
            if (productImage!= null){
                System.out.println("test value image = "+this.productImageService.getPublicPhoto(productImage.getId()));
                // Return the file content as a ResponseEntity
                return ResponseEntity.ok()
                        .body(this.productImageService.getPublicPhoto(productImage.getId()));
            }
            else{
                // Handle file not found or not readable
                return ResponseEntity.notFound().build();
            }
        }
        catch(IOException e){
            // Handle exceptions
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/public/store/{type}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]>  getPublicStorePhoto(@PathVariable String type) throws IOException {
        try{
            Store store = storeRepository.findById("Dymes").orElse(null);
            if (store!= null){
                System.out.println("test value image = "+this.storeService.getPublicPhoto(type));
                // Return the file content as a ResponseEntity
                return ResponseEntity.ok()
                        .body(this.storeService.getPublicPhoto(type));
            }
            else{
                // Handle file not found or not readable
                return ResponseEntity.notFound().build();
            }
        }
        catch(IOException e){
            // Handle exceptions
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/public/upsell/{upsellID}/{position}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]>  getPublicUpsellPhoto(@PathVariable long upsellID, @PathVariable String position) throws IOException {
        try{
            Upsell upsell = this.upsellRepository.findById(upsellID).orElse(null);
            if (upsell!= null){
                System.out.println("test value image = "+this.upsellService.getPublicPhoto(upsell.getId(),position));
                // Return the file content as a ResponseEntity
                return ResponseEntity.ok()
                        .body(this.upsellService.getPublicPhoto(upsell.getId(),position));
            }
            else{
                // Handle file not found or not readable
                return ResponseEntity.notFound().build();
            }
        }
        catch(IOException e){
            // Handle exceptions
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/public/product/{productID}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]>  getPublicProductPhoto(@PathVariable long productID) throws IOException {
        try{
            Product product = this.productRepository.findById(productID).orElse(null);
            if (product!= null){
                System.out.println("test value image = "+this.productService.getPublicPhoto(product.getId()));
                // Return the file content as a ResponseEntity
                return ResponseEntity.ok()
                        .body(this.productService.getPublicPhoto(product.getId()));
            }
            else{
                // Handle file not found or not readable
                return ResponseEntity.notFound().build();
            }
        }
        catch(IOException e){
            // Handle exceptions
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/public/profilePhoto/{userId}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]>  getPublicPhoto(@PathVariable String userId) throws IOException {
        try{
            User user = this.userRepository.findById(userId).orElse(null);
            if (user!= null){
                System.out.println("test value image = "+this.userService.getPublicPhoto(user.getId()));
                // Return the file content as a ResponseEntity
                return ResponseEntity.ok()
                        .body(this.userService.getPublicPhoto(user.getId()));
            }
            else{
                // Handle file not found or not readable
                return ResponseEntity.notFound().build();
            }
        }
        catch(IOException e){
            // Handle exceptions
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/public/getProductCategoryImage/{pCategoryID}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]> getProductCategoryImage(@PathVariable Long pCategoryID) throws IOException {
        try{
            ProductCategory productCategory = this.productCategoryRepository.findById(pCategoryID).orElse(null);
            if (productCategory!= null){
                System.out.println("test value image = "+this.productCategoryService.getPublicPhoto(productCategory.getId()));
                // Return the file content as a ResponseEntity
                return ResponseEntity.ok()
                        .body(this.productCategoryService.getPublicPhoto(productCategory.getId()));
            }
            else{
                // Handle file not found or not readable
                return ResponseEntity.notFound().build();
            }
        }
        catch(IOException e){
            // Handle exceptions
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/public/auth")
    public ResponseEntity<Map<String, String>> auth(String grantType, String email, String password, boolean withRefreshToken, String refreshToken){
        try {
            if (grantType.equals("password")){
                System.out.println("grantType is password");
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(email,password)
                );
                Map<String, String> idToken = authService.generateToken(email,withRefreshToken);
                return new ResponseEntity<>(idToken, HttpStatus.OK);
            }
            else if (grantType.equals("refreshToken")) {
                if (refreshToken == null) return new ResponseEntity<>(Map.of("errorMessage","refreshToken is required"), HttpStatus.OK);
                Jwt decodedJwt = jwtDecoder.decode(refreshToken);
                String subject = decodedJwt.getClaim("email");
                System.out.println("subject refresh token= "+subject);
                Map<String,String> idToken = authService.generateToken(subject,withRefreshToken);
                return new ResponseEntity<>(idToken, HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(Map.of("errorMessage","grantType is not supported"), HttpStatus.OK);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("errorMessage",e.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping(path = "/public/getProductCategories")
    public ResponseEntity<List<ProductCategory>> getAllProductCategoryPublic() {
        try {
            List<ProductCategory> productCategories = this.productCategoryService.retrieveAllProductCategory();
            System.out.println("Number of product category found: " + productCategories.size());
            if (productCategories.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(productCategories, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/public/getAllProducts")
    public ResponseEntity<List<Product>> getAllProductsPublic() {
        try {
            List<Product> products = this.productService.getAll();
            System.out.println("Number of products found: " + products.size());
            if (products.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/public/getVariantsStock/{productID}")
    public ResponseEntity<Map<String, String>> getVariantsStock(@PathVariable long productID) {
        try {
            return ResponseEntity.ok(Map.of("count",String.valueOf(this.productService.getVariantsStock(productID))));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(Map.of("message","Réssayer!"),HttpStatus.OK);
        }
    }

    @GetMapping(path = "/public/getProduct/{productID}")
    public ResponseEntity<Product> getProductPublic(@PathVariable long productID) {
        try {

            Product product = this.productService.getProduct(productID);
            if (product != null){
                return new ResponseEntity<>(product, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/public/attribute/{attributeID}")
    public ResponseEntity<Attribute> getPublicAttribute(@PathVariable long attributeID) {
        try {
            Attribute attribute = this.attributeRepository.findById(attributeID).orElse(null);
            if (attribute == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            }
            return new ResponseEntity<>(attribute, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/public/getAllAttributes")
    public ResponseEntity<List<Attribute>> getAllAttributesPublic() {
        try {
            List<Attribute> attributes = this.attributeService.getAll();
            if (attributes == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(attributes, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/public/promotion/{codePromo}")
    public ResponseEntity<Promotion> getPublicPromoCode(@PathVariable String codePromo) {
        try {
            Promotion promotion = this.promotionService.findPromotionByCode(codePromo);
            return new ResponseEntity<>(promotion, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/public/checkoutFields/getAll")
    public ResponseEntity<List<CheckoutField>> getAllCheckoutFieldsPublic() {
        try {
            List<CheckoutField> checkoutFields = this.checkoutFieldService.getAll();
            System.out.println("Number of checkoutFields found: " + checkoutFields.size());
            if (checkoutFields.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(checkoutFields, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/public/orders/add")
    public ResponseEntity<Map<String, String>> addOrder(@RequestParam("checkoutDataArray") String checkoutDataArrayJson,
                                                        @RequestParam(name = "totalPrices") String totalPricesJson,
                                                        @RequestParam(name = "totalWithTax") String totalWithTax,
                                                        @RequestParam(name = "shippingFees") String shippingFees,
                                                        @RequestParam(name = "promotion") String promotionJson,
                                                        @RequestParam(name = "clientType") String clientType,
                                                        @RequestParam(name = "paymentType") String paymentType) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Parse the JSON strings into arrays
            Map<String, Object>[] checkoutDataArray = objectMapper.readValue(checkoutDataArrayJson, new TypeReference<Map<String, Object>[]>() {});
            Map<String, Object>[] totalPrices = objectMapper.readValue(totalPricesJson, new TypeReference<Map<String, Object>[]>() {});

            // Deserialize promotion JSON string into a Promotion object
            Promotion promotion = null;
            if (!promotionJson.equals("null")) {
                promotion = objectMapper.readValue(promotionJson, Promotion.class);
            }

            // Get the current date and time
            LocalDateTime now = LocalDateTime.now();
            // Define the desired date format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            // Format the date and time using the formatter
            String formattedDateTime = now.format(formatter);

            // Add the order
            long orderid = this.orderService.addOrder(formattedDateTime, checkoutDataArray, totalPrices, Float.parseFloat(totalWithTax), Float.parseFloat(shippingFees), promotion, paymentType, clientType);
            if (orderid == -1){
                return new ResponseEntity<>(Map.of("message","out of stock."), HttpStatus.OK);
            }
            return new ResponseEntity<>(Map.of("message", "Order added successfully."+orderid), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.toString()), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping(path = "/public/addClient")
    public ResponseEntity<Map<String, String>> addClient(@RequestParam(name = "email") String email,
                                                         @RequestParam(name = "pass") String pass,
                                                         @RequestParam(name = "confirmPass") String confirmPass) {
        try {
            if (this.userService.findUserByEmail(email) != null){
                return new ResponseEntity<>(Map.of("message","User already exist"), HttpStatus.OK);
            }
            this.userService.addClient(email,pass,confirmPass);
            return new ResponseEntity<>(Map.of("message","User added success."), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/public/addRating")
    public ResponseEntity<Map<String, String>> addRating(@RequestParam(name = "fullname") String fullname,
                                                         @RequestParam(name = "emailOrPhone") String emailOrPhone,
                                                         @RequestParam(name = "comment") String comment,
                                                         @RequestParam(name = "stars") String stars,
                                                         @RequestParam(name = "productID") String productID
                                                         ) {
        try {

            this.ratingService.addRating(fullname,emailOrPhone,comment,Integer.valueOf(stars),Long.parseLong(productID));
            return new ResponseEntity<>(Map.of("message","Rating added success."), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/public/getAllRatingPublic/{productID}")
    public ResponseEntity<List<Rating>> getAllRatingPublic(@PathVariable String productID) {
        try {
            List<Rating> ratingList = this.ratingService.getRatingListByProductID(Long.parseLong(productID));
            System.out.println("Number of Rating found: " + ratingList.size());
            if (ratingList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(ratingList, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/public/order/{orderID}")
    public ResponseEntity<MyOrder> getPublicOrder(@PathVariable String orderID) {
        try {
            MyOrder myOrder = this.orderRepository.findById(Long.parseLong(orderID)).orElse(null);
            return new ResponseEntity<>(myOrder, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/public/orders/addFastCheckout")
    public ResponseEntity<Map<String, String>> fastCheckoutGuest(@RequestParam(name = "fullName") String fullName,
                                                                 @RequestParam(name = "phone") String phone,
                                                                 @RequestParam(name = "address") String address,
                                                                 @RequestParam(name = "shippingFees") String shippingFees,
                                                                 @RequestParam(name = "totalWithTax") String totalWithTax,
                                                                 @RequestParam(name = "productID") String productID,
                                                                 @RequestParam(name = "productType") String productType,
                                                                 @RequestParam(name = "productName") String productName,
                                                                 @RequestParam(name = "qte") String qte,
                                                                 @RequestParam(name = "description") String description,
                                                                 @RequestParam(name = "total") String total
    ) {
        try {
            // Get the current date and time
            LocalDateTime now = LocalDateTime.now();
            // Define the desired date format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            // Format the date and time using the formatter
            String formattedDateTime = now.format(formatter);
                long orderid = this.orderService.addOrderFastCheckout(formattedDateTime, fullName,phone,address,Float.parseFloat(shippingFees),Float.parseFloat(totalWithTax),"GUEST",Long.parseLong(productID),Integer.parseInt(qte)
                        ,description,Float.parseFloat(total),productType,productName);

            if (orderid == -1){
                return new ResponseEntity<>(Map.of("message","out of stock."), HttpStatus.OK);
            }
            return new ResponseEntity<>(Map.of("message","Order added successfully."+orderid), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }



    @GetMapping(path = "/public/upsellOffer/{upsellID}")
    public ResponseEntity<Upsell> getPublicUpsellOffer(@PathVariable String upsellID) {
        try {
            Upsell upsell = this.upsellRepository.findById(Long.parseLong(upsellID)).orElse(null);
            return new ResponseEntity<>(upsell, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/public/approveUpsell")
    public ResponseEntity<Map<String, String>> approveUpsell(@RequestParam(name = "upsellID") String upsellID) {
        try {

            this.upsellService.approveUpsell(Long.parseLong(upsellID));
            return new ResponseEntity<>(Map.of("message","Upsell approved successfully."), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/public/cancelUpsell")
    public ResponseEntity<Map<String, String>> cancelUpsell(@RequestParam(name = "upsellID") String upsellID) {
        try {

            this.upsellService.cancelUpsell(Long.parseLong(upsellID));
            return new ResponseEntity<>(Map.of("message","Upsell cancelled successfully."), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message",e.toString()),HttpStatus.BAD_REQUEST);
        }
    }




}
