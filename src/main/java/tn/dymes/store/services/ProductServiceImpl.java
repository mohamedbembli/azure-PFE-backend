package tn.dymes.store.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.dymes.store.dtos.AddProductDTO;
import tn.dymes.store.dtos.DiscountDTO;
import tn.dymes.store.dtos.VariantDTO;
import tn.dymes.store.entites.*;
import tn.dymes.store.entites.Timer;
import tn.dymes.store.repositories.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class ProductServiceImpl implements IProductService{

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ElementRepository elementRepository;

    @Autowired
    IElementService elementService;

    @Autowired
    ProductImageRepository productImageRepository;

    @Autowired
    VariantRepository variantRepository;

    @Autowired
    TimerRepository timerRepository;

    @Autowired
    VisitorsRangeRepository visitorsRangeRepository;

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @Autowired
    DiscountRepository discountRepository;



    @Override
    public List<Product> getAll() {
        List<Product> products = (List<Product>) productRepository.findAll();
        return  products;
    }

    @Override
    public Product updateProduct(long productID, AddProductDTO addProductDTO, List<MultipartFile> images, List<MultipartFile> variantImages) throws IOException {
        Product product = productRepository.findById(productID).orElse(null);
        if (product != null){
            // general section
            product.setName(addProductDTO.productName());
            product.setBarCode(addProductDTO.productBarCode());
            product.setReference(addProductDTO.productRef());
            product.setSupplier(addProductDTO.productSupplier());
            product.setDescription(addProductDTO.description());
            // product status section
            product.setStatus(addProductDTO.productStatus());
            // price section
            product.setBuyPrice(addProductDTO.buyPrice());
            product.setSellPrice(addProductDTO.sellPrice());
            product.setSimpleDiscountType(addProductDTO.reductionType());
            product.setSimpleDiscountValue(addProductDTO.reductionValue());
            product.setTVA(addProductDTO.tax());
            // category section
            product.setCategory(null);// delete previous categoy
            ProductCategory productCategory =  productCategoryRepository.findById(addProductDTO.productCategoryID()).orElse(null);
            product.setCategory(productCategory);
            // stock section
            product.setStock(addProductDTO.stock());
            // shipping price section
            product.setShippingPrice(addProductDTO.shippingPrice());
            // images section
            //delete previous images
            List<ProductImage> productImageList1 = (List<ProductImage>) productImageRepository.findAll();
            String profilePath = "src/main/resources/static/uploads/products/";
            if (productImageList1 != null){
                for(ProductImage productImage: productImageList1){
                    if (productImage.getProduct().getId() == product.getId()){
                        String fileNameToDelete = productImage.getImageName(); // Specify the file name you want to delete
                        try {
                            Path filePath = Paths.get(profilePath, fileNameToDelete);
                            // Check if the file exists before attempting to delete it
                            if (Files.exists(filePath)) {
                                Files.delete(filePath);
                                System.out.println("File deleted successfully: " + fileNameToDelete);
                                productImageRepository.deleteById(productImage.getId()); // delete previous images
                            } else {
                                System.out.println("File not found: " + fileNameToDelete);
                            }
                        } catch (IOException e) {
                            System.err.println("Error deleting the file: " + e.getMessage());
                        }
                    }
                }
                product.setProductImages(null);
                productRepository.save(product);
            }
            //
            if ( images != null){
                List<ProductImage> productImageList = new ArrayList<>();
                int i=0;
                for (MultipartFile image: images){
                    ProductImage productImage = new ProductImage();
                    String fileName = this.uploadProductImage(image);
                    productImage.setImageName(fileName);
                    productImage.setPosition(i);
                    productImage.setProduct(product);
                    productImageRepository.save(productImage);
                    productImageList.add(productImage);
                    i++;
                }
                product.setProductImages(productImageList);
            }
            // discounts
            // delete previous discounts
            List<Discount> discounts = (List<Discount>) discountRepository.findAll();
            if (discounts != null){
                for (Discount discount: discounts){
                    if (discount.getProduct().getId() == product.getId()){
                        discountRepository.delete(discount);
                    }
                }
                product.setDiscounts(null);
                productRepository.save(product);
            }
            //end
            System.out.println("discountTable size = "+addProductDTO.discountTable().size());
            if ( addProductDTO.discountTable().size() > 0){
                List<Discount> discountList = new ArrayList<>();
                for(DiscountDTO discountDTO: addProductDTO.discountTable()){
                    Discount discount = new Discount();
                    discount.setQuantity(discountDTO.qte());
                    discount.setType(discountDTO.type());
                    discount.setValue(discountDTO.reduction());
                    discount.setFinalPrice(discountDTO.finalPrice());
                    discount.setProduct(product);
                    //
                    discountList.add(discount);
                    discountRepository.save(discount);
                }
                product.setDiscounts(discountList);
            }
            // delete previous variants
            List<Variant> variantList1 = (List<Variant>) variantRepository.findAll();
            String profilePathVariant = "src/main/resources/static/uploads/products/variants/";
            if (variantList1.size() > 0){
                for(Variant variant: variantList1){
                    if (variant.getProduct().getId() == product.getId()){
                        // delete variant elements
                        variant.setElements(null);
                        variantRepository.save(variant);
                        String fileNameToDelete = variant.getImage(); // Specify the file name you want to delete
                        variantRepository.deleteById(variant.getId()); // delete variant
                        try {
                            Path filePath = Paths.get(profilePathVariant, fileNameToDelete);
                            // Check if the file exists before attempting to delete it
                            if (Files.exists(filePath)) {
                                Files.delete(filePath);
                                System.out.println("File deleted successfully: " + fileNameToDelete);
                            } else {
                                System.out.println("File not found: " + fileNameToDelete);
                            }
                        } catch (IOException e) {
                            System.err.println("Error deleting the file: " + e.getMessage());
                        }
                    }
                }
                product.setVariants(null);
                productRepository.save(product);
            }
            //
            // variants section
            System.out.println("variants size = "+addProductDTO.variants().size());
            if ( addProductDTO.variants().size() > 0 ){
                List<Variant> variantList = new ArrayList<>();
                int i=0;
                for (VariantDTO variantDTO: addProductDTO.variants()){
                    Variant variant = new Variant();
                    variant.setStock(variantDTO.stock());
                    variant.setActif(variantDTO.actif());
                    variant.setPrice(variantDTO.price());
                    variant.setVariantByDefault(variantDTO.defaultVariant());
                    variant.setProduct(product);
                    if (variantDTO.image() != null){
                        String fileName =  this.uploadVariantImage(variantImages.get(i));
                        variant.setImage(fileName);
                        i++;
                    }else{
                        variant.setImage(null);
                    }
                    //
                    List<Element> elementList = new ArrayList<>();
                    for(String name: variantDTO.attributes()){
                        Element element = elementService.getElementByName(name);
                        if (element != null){
                            elementList.add(element);
                        }
                    }
                    variant.setElements(elementList);
                    //
                    variantList.add(variant);
                    variantRepository.save(variant);
                }
                product.setVariants(variantList);
                productRepository.save(product);
            }

            // delete previous live visitors section
            List<VisitorsRange> visitorsRanges = (List<VisitorsRange>) visitorsRangeRepository.findAll();
            if (visitorsRanges != null){
                for (VisitorsRange visitorsRange: visitorsRanges){
                    if (visitorsRange.getProduct().getId() == product.getId()){
                        visitorsRangeRepository.delete(visitorsRange);
                    }
                }
                product.setVisitorsRange(null);
                productRepository.save(product);
            }
            // live visitors section
            if ( addProductDTO.visitorStartRange() != null  && addProductDTO.visitorsEndRange() != null){
                VisitorsRange visitorsRange = new VisitorsRange();
                visitorsRange.setRangeFrom(addProductDTO.visitorStartRange());
                visitorsRange.setRangeTo(addProductDTO.visitorsEndRange());
                visitorsRange.setProduct(product);
                //
                product.setVisitorsRange(visitorsRange);
                visitorsRangeRepository.save(visitorsRange);
            }
            // delete previous timer
            List<Timer> timers = (List<Timer>) timerRepository.findAll();
            if (timers != null){
                for (Timer timer: timers){
                    if (timer.getProduct().getId() == product.getId()){
                        timerRepository.delete(timer);
                    }
                }
                product.setTimer(null);
                productRepository.save(product);
            }
            // timer section
            System.out.println("addProductDTO.nbHoursTimer() != null = "+(addProductDTO.nbHoursTimer() != null));
            System.out.println("addProductDTO.nbHoursTimer() = "+addProductDTO.nbHoursTimer());
            if (addProductDTO.nbHoursTimer() != null && addProductDTO.nbHoursTimer() > 0) {
                Timer timer = new Timer();
                timer.setNbHours(addProductDTO.nbHoursTimer());
                timer.setProduct(product);
                timerRepository.save(timer);
                product.setTimer(timer);
            }

            productRepository.save(product);
            return product;
        }
        return null;
    }

    @Override
    public byte[] getPublicPhoto(long productId) throws IOException {
        Product product = productRepository.findById(productId).orElse(null);
        String productsPath = "src/main/resources/static/uploads/products";
        String variantPath = "src/main/resources/static/uploads/products/variants";
        Path path=null;

        if (product != null) {
            if (product.getProductImages() != null && product.getProductImages().size() > 0) {
                for (ProductImage productImage: product.getProductImages()){
                    if (productImage.getPosition() == 0){
                        path = Paths.get(productsPath, productImage.getImageName());
                    }
                }
            } else if ( (product.getProductImages() == null || product.getProductImages().size() == 0) && (product.getVariants() != null && product.getVariants().size() > 0)) {
                path = Paths.get(variantPath, product.getVariants().get(0).getImage());
            } else {
                throw new IllegalStateException("No images or variants found for the product");
            }
        } else {
            throw new IllegalArgumentException("Product not found with ID: " + productId);
        }

        return Files.readAllBytes(path);
    }

    @Override
    public int getVariantsStock(long productID) {
        int count=0;
        Product product  = this.getProduct(productID);
        if (product != null){
            List<Variant> variantList = product.getVariants();
            if (variantList != null && variantList.size() > 0)
                for(Variant variant: variantList){
                    count+=variant.getStock();
                }
                return count;
        }
        return 0;
    }


    @Override
    public Product getProduct(long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null)
            return product;
        return null;
    }

    @Override
    public void addProduct(AddProductDTO addProductDTO, List<MultipartFile> images, List<MultipartFile> variantImages) throws IOException {
        System.out.println(" variants is null? = "+(addProductDTO.variants() == null));


        Product product = new Product();
        // Create a Date object
        Date currentDate = new Date();
        // Create a SimpleDateFormat object with the desired format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd:MM:yyyy HH:mm:ss");
        // Format the Date object to a string
        String formattedDate = dateFormat.format(currentDate);
        product.setCreation_date(formattedDate);
        // general section
        product.setName(addProductDTO.productName());
        product.setBarCode(addProductDTO.productBarCode());
        product.setReference(addProductDTO.productRef());
        product.setSupplier(addProductDTO.productSupplier());
        product.setDescription(addProductDTO.description());
        // product status section
        product.setStatus(addProductDTO.productStatus());
        // price section
        product.setBuyPrice(addProductDTO.buyPrice());
        product.setSellPrice(addProductDTO.sellPrice());
        product.setSimpleDiscountType(addProductDTO.reductionType());
        product.setSimpleDiscountValue(addProductDTO.reductionValue());
        product.setTVA(addProductDTO.tax());
        // category section
        ProductCategory productCategory =  productCategoryRepository.findById(addProductDTO.productCategoryID()).orElse(null);
        product.setCategory(productCategory);
        // stock section
        product.setStock(addProductDTO.stock());
        // shipping price section
        product.setShippingPrice(addProductDTO.shippingPrice());
        // save product to db
        productRepository.save(product);

        // images section
        if ( images != null){
            List<ProductImage> productImageList = new ArrayList<>();
            int i=0;
            for (MultipartFile image: images){
                ProductImage productImage = new ProductImage();
                String fileName = this.uploadProductImage(image);
                productImage.setImageName(fileName);
                productImage.setPosition(i);
                productImage.setProduct(product);
                productImageRepository.save(productImage);
                productImageList.add(productImage);
                i++;
            }
            product.setProductImages(productImageList);
        }
        // discounts
        System.out.println("discountTable size = "+addProductDTO.discountTable().size());
        if ( addProductDTO.discountTable().size() > 0){
            List<Discount> discountList = new ArrayList<>();
            for(DiscountDTO discountDTO: addProductDTO.discountTable()){
                Discount discount = new Discount();
                discount.setQuantity(discountDTO.qte());
                discount.setType(discountDTO.type());
                discount.setValue(discountDTO.reduction());
                discount.setFinalPrice(discountDTO.finalPrice());
                discount.setProduct(product);
                //
                discountList.add(discount);
                discountRepository.save(discount);
            }
            product.setDiscounts(discountList);
        }
        // variants section
            System.out.println("variants size = "+addProductDTO.variants().size());
            if ( addProductDTO.variants().size() > 0 ){
                List<Variant> variantList = new ArrayList<>();
                int i=0;
                for (VariantDTO variantDTO: addProductDTO.variants()){
                    Variant variant = new Variant();
                    variant.setStock(variantDTO.stock());
                    variant.setActif(variantDTO.actif());
                    variant.setPrice(variantDTO.price());
                    variant.setVariantByDefault(variantDTO.defaultVariant());
                    variant.setProduct(product);
                    if (variantDTO.image() != null){
                       String fileName =  this.uploadVariantImage(variantImages.get(i));
                       variant.setImage(fileName);
                       i++;
                    }else{
                        variant.setImage(null);
                    }
                    //
                    List<Element> elementList = new ArrayList<>();
                    for(String name: variantDTO.attributes()){
                        Element element = elementService.getElementByName(name);
                        if (element != null){
                           elementList.add(element);
                        }
                    }
                    variant.setElements(elementList);
                    //
                    variantList.add(variant);
                    variantRepository.save(variant);
                }
                product.setVariants(variantList);
             }

        // live visitors section
        if ( addProductDTO.visitorStartRange() != null  && addProductDTO.visitorsEndRange() != null){
            VisitorsRange visitorsRange = new VisitorsRange();
            visitorsRange.setRangeFrom(addProductDTO.visitorStartRange());
            visitorsRange.setRangeTo(addProductDTO.visitorsEndRange());
            visitorsRange.setProduct(product);
            //
            product.setVisitorsRange(visitorsRange);
            visitorsRangeRepository.save(visitorsRange);
        }
        // timer section
        System.out.println("addProductDTO.nbHoursTimer() != null = "+(addProductDTO.nbHoursTimer() != null));
        System.out.println("addProductDTO.nbHoursTimer() = "+addProductDTO.nbHoursTimer());
        if (addProductDTO.nbHoursTimer() != null && addProductDTO.nbHoursTimer() > 0) {
            Timer timer = new Timer();
            timer.setNbHours(addProductDTO.nbHoursTimer());
            timer.setProduct(product);
            timerRepository.save(timer);
            product.setTimer(timer);
        }

        productRepository.save(product);
    }


    public String uploadProductImage(MultipartFile file) throws IOException {
        // Define the uploads directory within the static resources
        String uploadsDirPath = "src/main/resources/static/uploads/products/";

        // Create the uploads directory if it doesn't exist
        File directory = new File(uploadsDirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Save the uploaded file to the uploads directory
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        Path filePath = Paths.get(uploadsDirPath, fileName);
        Files.write(filePath, file.getBytes());

        return fileName;

    }
    public String uploadVariantImage(MultipartFile file) throws IOException {
        // Define the uploads directory within the static resources
        String uploadsDirPath = "src/main/resources/static/uploads/products/variants/";

        // Create the uploads directory if it doesn't exist
        File directory = new File(uploadsDirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Save the uploaded file to the uploads directory
        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = generateUniqueFileName(originalFileName);
        Path filePath = Paths.get(uploadsDirPath, uniqueFileName);
        Files.write(filePath, file.getBytes());

        return uniqueFileName;

    }

    public String generateUniqueFileName(String originalFileName) {
        // Generate unique UUID
        String uuid = UUID.randomUUID().toString();

        // Extract the file extension from the original filename
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

        // Combine UUID and file extension to create a unique filename
        return "unique_" + uuid + fileExtension;
    }

    public static String shuffleString(String input) {
        char[] charArray = input.toCharArray();
        Random random = new Random();

        for (int i = charArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = charArray[i];
            charArray[i] = charArray[j];
            charArray[j] = temp;
        }

        return new String(charArray);
    }
}
