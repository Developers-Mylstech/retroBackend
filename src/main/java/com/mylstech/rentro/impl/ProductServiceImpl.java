package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.*;
import com.mylstech.rentro.dto.response.CheckOutResponse;
import com.mylstech.rentro.dto.response.ProductResponse;
import com.mylstech.rentro.exception.ResourceNotFoundException;
import com.mylstech.rentro.model.*;
import com.mylstech.rentro.repository.*;
import com.mylstech.rentro.service.CheckOutService;
import com.mylstech.rentro.service.ProductService;
import com.mylstech.rentro.util.ProductType;
import com.mylstech.rentro.util.SecurityUtils;
import com.mylstech.rentro.util.UNIT;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final String PRODUCT_NOT_FOUND_WITH_ID = "Product not found with id: ";
    private final ProductRepository productRepository;
    private final Logger logger = LoggerFactory.getLogger ( ProductServiceImpl.class );
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductForRepository productForRepository;
    private final SellRepository sellRepository;
    private final RentRepository rentRepository;
    private final ServiceRepository serviceRepository;
    private final SpecificationFieldRepository specificationFieldRepository;
    private final AddressRepository addressRepository;
    private final SecurityUtils securityUtils;
    private final CartRepository cartRepository;
    private final CheckOutRepository checkOutRepository;
    private final CheckOutService checkOutService;
    private final ImageRepository imageRepository;
    private final OurServiceRepository ourServiceRepository;
    private final CartItemRepository cartItemRepository;
    private final WishlistRepository wishlistRepository;
    private final OrderItemRepository orderItemRepository;


    @Value("${vat.value}")
    private Double vat;

    private static void addOts(ServiceRequest serviceRequest, com.mylstech.rentro.model.Service service) {
        if ( serviceRequest.getOts ( ) != null ) {
            service.setOts ( serviceRequest.getOts ( ).requestToServiceField ( ) );
        }
    }
    private static void addMmc(ServiceRequest serviceRequest, com.mylstech.rentro.model.Service service) {
        if ( serviceRequest.getMmc ( ) != null ) {
            service.setMmc ( serviceRequest.getMmc ( ).requestToServiceField ( ) );
        }
    }
    private static void addAmcGold(ServiceRequest serviceRequest, com.mylstech.rentro.model.Service service) {
        if ( serviceRequest.getAmcGold ( ) != null ) {
            service.setAmcGold ( serviceRequest.getAmcGold ( ).requestToServiceField ( ) );
        }
    }
    private static void addAmcBasic(ServiceRequest serviceRequest, com.mylstech.rentro.model.Service service) {
        if ( serviceRequest.getAmcBasic ( ) != null ) {
            service.setAmcBasic ( serviceRequest.getAmcBasic ( ).requestToServiceField ( ) );
        }
    }

    @Override
    @Cacheable(value = "products", key = "'allProducts'")
    public List<ProductResponse> getAllProducts() {
        try {
            List<Product> products = productRepository.findAllWithRelationships ( );
            logger.debug ( "Found {} products in database", products.size ( ) );
            return products.stream ( )
                    .map ( ProductResponse::new )
                    .sorted ( (p1, p2) -> p2.getProductId ( ).compareTo ( p1.getProductId ( ) ) )
                    .toList ( );
        }
        catch ( Exception e ) {
            logger.error ( "Error retrieving products from database", e );
            throw new RuntimeException ( "Failed to retrieve products", e );
        }
    }

    @Override
    public ProductResponse getProductById(Long id) {
        try {
            Product product = productRepository.findById ( id )
                    .orElseThrow ( () -> new ResourceNotFoundException ( "Product", "id", id ) );
            logger.debug ( "Found product with id {}: {}", id, product );
            return new ProductResponse ( product );
        }
        catch ( ResourceNotFoundException e ) {
            throw e;  // Let it propagate to be handled by GlobalExceptionHandler
        }
        catch ( Exception e ) {
            logger.error ( "Error retrieving product with id: " + id, e );
            throw e;
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", key = "'allProducts'")
    public ProductResponse createProduct(ProductRequest request) {
        try {
            logger.debug ( "Starting product creation process" );

            // Bottom-up approach: Create all related entities first, then the product

            // 1. Create or fetch the inventory
            Inventory inventory = null;
            if ( request.getInventory ( ) != null ) {
                inventory = request.getInventory ( ).requestToInventory ( );
            }

            // 2. Create or fetch the ProductFor entity and its related entities
            ProductFor productFor = null;

            if ( request.getProductFor ( ) != null ) {
                productFor = new ProductFor ( );

                // Set request quotation flag first
                if ( request.getProductFor ( ).getIsAvailableForRequestQuotation ( ) == Boolean.TRUE ) {
                    productFor.setIsAvailableForRequestQuotation ( Boolean.TRUE );
                } else {
                    // Default to false for backward compatibility
                    productFor.setIsAvailableForRequestQuotation ( Boolean.FALSE );
                    // Handle Sell entity
                    if ( request.getProductFor ( ).getSell ( ) != null ) {

                        // Create new Sell entity
                        Sell sell = request.getProductFor ( ).getSell ( ).requestToSell ( );
                        sell = sellRepository.save ( sell );
                        productFor.setSell ( sell );
                    }

                    // Handle Rent entity
                    if ( request.getProductFor ( ).getRent ( ) != null ) {
                        // Create new Rent entity
                        Rent rent = request.getProductFor ( ).getRent ( ).requestToRent ( );
                        rent = rentRepository.save ( rent );
                        productFor.setRent ( rent );
                    }

                    // Handle Service entity
                    if ( request.getProductFor ( ).getService ( ) != null ) {
                        com.mylstech.rentro.model.Service service = new com.mylstech.rentro.model.Service ( );
                        ServiceRequest serviceRequest = request.getProductFor ( ).getService ( );
                        addAmcBasic ( serviceRequest, service );
                        addAmcGold ( serviceRequest, service );
                        addMmc ( serviceRequest, service );
                        addOts ( serviceRequest, service );
                        service = serviceRepository.save ( service );
                        productFor.setServices ( service );
                    }
                }


                // Save the ProductFor entity first
                productFor = productForRepository.save ( productFor );
            }

            // 3. Create specifications
            List<Specification> specifications = null;
            if ( request.getSpecifications ( ) != null && ! request.getSpecifications ( ).isEmpty ( ) ) {
                // Get all specification names from the request
                List<String> requestNameList = request.getSpecifications ( ).stream ( )
                        .map ( SpecificationRequest::getName )
                        .toList ( );

                // Get all existing specification fields from DB
                List<SpecificationField> existingFields = specificationFieldRepository.findAll ( );
                Map<String, SpecificationField> existingFieldsMap = existingFields.stream ( )
                        .collect ( Collectors.toMap ( SpecificationField::getName, field -> field ) );

                // Create any missing specification fields
                requestNameList.stream ( )
                        .filter ( name -> ! existingFieldsMap.containsKey ( name ) )
                        .forEach ( name -> {
                            SpecificationField field = new SpecificationField ( );
                            field.setName ( name );
                            SpecificationField savedField = specificationFieldRepository.save ( field );
                            existingFieldsMap.put ( name, savedField );
                        } );

                // Create specifications with proper references to specification fields
                specifications = request.getSpecifications ( ).stream ( )
                        .filter ( spec -> spec.getName ( ) != null
                                && ! spec.getName ( ).trim ( ).isEmpty ( )
                                && spec.getValue ( ) != null
                                && ! spec.getValue ( ).trim ( ).isEmpty ( ) )
                        .map ( specRequest -> {
                            Specification spec = new Specification ( );
                            spec.setName ( specRequest.getName ( ) );
                            spec.setValue ( specRequest.getValue ( ) );
                            return spec;
                        } )
                        .toList ( );
            }

            // 4. Create the product
            Product product = new Product ( );
            product.setName ( request.getName ( ) );
            product.setDescription ( request.getDescription ( ) );
            product.setLongDescription ( request.getLongDescription ( ) );
            product.setManufacturer ( request.getManufacturer ( ) );
            product.setKeyFeatures ( request.getKeyFeatures ( ) );
            product.setSupplierName ( request.getSupplierName ( ) );
            product.setSupplierCode ( request.getSupplierCode ( ) );
            product.setModelNo ( request.getModelNo ( ) );

            // Set category if categoryId is provided
            if ( request.getCategoryId ( ) != null ) {
                Category category = categoryRepository.findById ( request.getCategoryId ( ) )
                        .orElseThrow ( () -> new RuntimeException ( "Category not found with id: " + request.getCategoryId ( ) ) );
                product.setCategory ( category );
            }

            // Set subcategory if subCategoryId is provided
            if ( request.getSubCategoryId ( ) != null ) {
                Category subCategory = categoryRepository.findById ( request.getSubCategoryId ( ) )
                        .orElseThrow ( () -> new RuntimeException ( "SubCategory not found with id: " + request.getSubCategoryId ( ) ) );
                product.setSubCategory ( subCategory );
            }

            // Set brand if brandId is provided
            if ( request.getBrandId ( ) != null ) {
                Brand brand = brandRepository.findById ( request.getBrandId ( ) )
                        .orElseThrow ( () -> new RuntimeException ( "Brand not found with id: " + request.getBrandId ( ) ) );
                product.setBrand ( brand );
            }

            // Set the inventory
            if ( inventory != null ) {
                product.setInventory ( inventory );
            }

            // Set the productFor
            if ( productFor != null ) {
                product.setProductFor ( productFor );
            }

            // Set specifications
            if ( specifications != null ) {
                product.setSpecification ( specifications );
            }
            //set Tag N Keyword
            if ( request.getTagNKeywords ( ) != null ) {
                product.setTagNKeywords ( request.getTagNKeywords ( ) );
            }

            // Handle OurServices if IDs are provided
            if ( request.getOurServiceIds ( ) != null && ! request.getOurServiceIds ( ).isEmpty ( ) ) {
                List<OurService> ourServices = ourServiceRepository.findAllById ( request.getOurServiceIds ( ) );

                if ( ourServices.size ( ) != request.getOurServiceIds ( ).size ( ) ) {
                    logger.warn ( "Some OurService IDs were not found. Found {} out of {} requested services.",
                            ourServices.size ( ), request.getOurServiceIds ( ).size ( ) );
                }

                product.setOurServices ( ourServices );
            }

            // Initialize images list
            product.setImages ( new ArrayList<> ( ) );

            // Set images using imageIds if provided
            if ( request.getImageIds ( ) != null && ! request.getImageIds ( ).isEmpty ( ) ) {
                List<Image> images = imageRepository.findAllById ( request.getImageIds ( ) );
                logger.debug ( "Found {} images by IDs", images.size ( ) );

                // Add each image individually to maintain bidirectional relationship
                for (Image image : images) {
                    product.addImage ( image );
                }
            }

            // For backward compatibility - handle imageUrls if provided
            /* 
            if ( request.getImageUrls ( ) != null && ! request.getImageUrls ( ).isEmpty ( ) ) {
                for (String url : request.getImageUrls ( )) {
                    // Check if this URL already exists in our images list to avoid duplicates
                    boolean urlAlreadyExists = product.getImages ( ).stream ( )
                            .anyMatch ( img -> img.getImageUrl ( ).equals ( url ) );

                    if ( ! urlAlreadyExists ) {
                        // Try to find existing image entity with this URL
                        Image image = imageRepository.findByImageUrl ( url )
                                .orElseGet ( () -> {
                                    Image newImage = new Image ( );
                                    newImage.setImageUrl ( url );
                                    return imageRepository.save ( newImage );
                                } );
                        product.addImage ( image );
                    }
                }
            }
            */

            // Save the product
            Product savedProduct = productRepository.save ( product );

            if ( logger.isDebugEnabled ( ) ) {
                logger.debug ( "Saved product with ID: {} and {} images",
                        savedProduct.getProductId ( ),
                        savedProduct.getImages ( ).size ( ) );
            }

            logger.debug ( "Successfully created product" );
            return new ProductResponse ( savedProduct );
        }
        catch ( Exception e ) {
            logger.error ( "Error creating product: {}", e.getMessage ( ), e );
            // Print the stack trace to see exactly where the error is occurring
            e.printStackTrace ( );
            throw e;
        }
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "products", key = "'allProducts'"),
            @CacheEvict(value = "productSearchCache", key = "#query.toLowerCase().trim()")
    }
    )
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById ( id )
                .orElseThrow ( () -> new RuntimeException ( PRODUCT_NOT_FOUND_WITH_ID + id ) );

        // Update basic fields if provided
        if ( request.getName ( ) != null ) {
            product.setName ( request.getName ( ) );
        }
        if ( request.getKeyFeatures ( ) != null ) {
            product.setKeyFeatures ( request.getKeyFeatures ( ) );
        }
        if ( request.getManufacturer ( ) != null ) {
            product.setManufacturer ( request.getManufacturer ( ) );
        }
        if ( request.getDescription ( ) != null ) {
            product.setDescription ( request.getDescription ( ) );
        }
        if ( request.getSupplierName ( ) != null ) {
            product.setSupplierName ( request.getSupplierName ( ) );
        }
        if ( request.getSupplierCode ( ) != null ) {
            product.setSupplierCode ( request.getSupplierCode ( ) );
        }
        if ( request.getModelNo ( ) != null ) {
            product.setModelNo ( request.getModelNo ( ) );
        }

        if ( request.getLongDescription ( ) != null ) {
            product.setLongDescription ( request.getLongDescription ( ) );
        }

        // Update category if categoryId is provided
        if ( request.getCategoryId ( ) != null ) {
            Category category = categoryRepository.findById ( request.getCategoryId ( ) )
                    .orElseThrow ( () -> new RuntimeException ( "Category not found with id: " + request.getCategoryId ( ) ) );
            product.setCategory ( category );
        }

        // Update subcategory if subCategoryId is provided
        if ( request.getSubCategoryId ( ) != null ) {
            Category subCategory = categoryRepository.findById ( request.getSubCategoryId ( ) )
                    .orElseThrow ( () -> new RuntimeException ( "SubCategory not found with id: " + request.getSubCategoryId ( ) ) );
            product.setSubCategory ( subCategory );
        }

        // Update brand if brandId is provided
        if ( request.getBrandId ( ) != null ) {
            Brand brand = brandRepository.findById ( request.getBrandId ( ) )
                    .orElseThrow ( () -> new RuntimeException ( "Brand not found with id: " + request.getBrandId ( ) ) );
            product.setBrand ( brand );
        }

        // Update inventory if provided
        if ( request.getInventory ( ) != null ) {
            Inventory inventory = product.getInventory ( );
            if ( inventory == null ) {
                inventory = new Inventory ( );
                product.setInventory ( inventory );
            }
            if ( request.getInventory ( ).getQuantity ( ) != null ) {
                inventory.setQuantity ( request.getInventory ( ).getQuantity ( ) );
            }
            if ( request.getInventory ( ).getSku ( ) != null ) {
                inventory.setSku ( request.getInventory ( ).getSku ( ) );
            }

            if ( request.getInventory ( ).getStockStatus ( ) != null ) {
                inventory.setStockStatus ( request.getInventory ( ).getStockStatus ( ) );
            }
        }

        // Update productFor if provided
        if ( request.getProductFor ( ) != null ) {
            ProductFor productFor = product.getProductFor ( );

            // Handle Sell entity
            if ( request.getProductFor ( ).getSell ( ) != null ) {

                // Create new Sell entity
                Sell existingSell = productFor.getSell ( );
                if ( existingSell == null ) {
                    existingSell = new Sell ( );
                }
                Sell updatedSell = updateSellFields ( existingSell, request.getProductFor ( ).getSell ( ) );
                updatedSell = sellRepository.save ( updatedSell );
                productFor.setSell ( updatedSell );
            }

            // Handle Rent entity
            if ( request.getProductFor ( ).getRent ( ) != null ) {
                Rent existingRent = productFor.getRent ( );
                if ( existingRent == null ) {
                    existingRent = new Rent ( );
                }
                Rent updatedRent = updateRentFields ( existingRent, request.getProductFor ( ).getRent ( ) );
                updatedRent = rentRepository.save ( updatedRent );
                productFor.setRent ( updatedRent );
            }


            // Handle Service entity
            if ( request.getProductFor ( ).getService ( ) != null ) {
                com.mylstech.rentro.model.Service existingService = productFor.getServices ( );
                if ( existingService == null ) {
                    existingService = new com.mylstech.rentro.model.Service ( );
                }

                ServiceRequest serviceRequest = request.getProductFor ( ).getService ( );

                // Handle AMC Basic
                if ( serviceRequest.getAmcBasic ( ) != null ) {
                    ServiceField existingAmcBasic = existingService.getAmcBasic ( );
                    if ( existingAmcBasic == null ) {
                        existingService.setAmcBasic ( serviceRequest.getAmcBasic ( ).requestToServiceField ( ) );
                    } else {
                        updateServiceFieldEntity ( existingAmcBasic, serviceRequest.getAmcBasic ( ) );
                    }
                }

                // Handle AMC Gold
                if ( serviceRequest.getAmcGold ( ) != null ) {
                    ServiceField existingAmcGold = existingService.getAmcGold ( );
                    if ( existingAmcGold == null ) {
                        existingService.setAmcGold ( serviceRequest.getAmcGold ( ).requestToServiceField ( ) );
                    } else {
                        updateServiceFieldEntity ( existingAmcGold, serviceRequest.getAmcGold ( ) );
                    }
                }

                // Handle MMC
                if ( serviceRequest.getMmc ( ) != null ) {
                    ServiceField existingMmc = existingService.getMmc ( );
                    if ( existingMmc == null ) {
                        existingService.setMmc ( serviceRequest.getMmc ( ).requestToServiceField ( ) );
                    } else {
                        updateServiceFieldEntity ( existingMmc, serviceRequest.getMmc ( ) );
                    }
                }

                // Handle OTS
                if ( serviceRequest.getOts ( ) != null ) {
                    ServiceField existingOts = existingService.getOts ( );
                    if ( existingOts == null ) {
                        existingService.setOts ( serviceRequest.getOts ( ).requestToServiceField ( ) );
                    } else {
                        updateServiceFieldEntity ( existingOts, serviceRequest.getOts ( ) );
                    }
                }

                existingService = serviceRepository.save ( existingService );
                productFor.setServices ( existingService );
            }
            // Save the ProductFor entity first
            productForRepository.save ( productFor );
        }

        // Update specifications if provided
        if ( request.getSpecifications ( ) != null && ! request.getSpecifications ( ).isEmpty ( ) ) {
            // First ensure all specification fields exist in DB
            List<String> requestNameList = request.getSpecifications ( ).stream ( )
                    .map ( SpecificationRequest::getName )
                    .toList ( );
            List<String> dbSpecificationFieldList = specificationFieldRepository.findAll ( )
                    .stream ( )
                    .map ( SpecificationField::getName )
                    .toList ( );

            // Find names in request not present in DB and create them
            List<String> notFoundInDB = requestNameList.stream ( )
                    .filter ( name -> ! dbSpecificationFieldList.contains ( name ) )
                    .toList ( );

            notFoundInDB.forEach ( name -> {
                SpecificationField field = new SpecificationField ( );
                field.setName ( name );
                specificationFieldRepository.save ( field );
            } );

            // Get existing specifications
            List<Specification> existingSpecs = product.getSpecification ( );
            if ( existingSpecs == null ) {
                existingSpecs = new ArrayList<> ( );
            }

            // Update or create specifications
            List<Specification> updatedSpecs = new ArrayList<> ( );
            for (SpecificationRequest specRequest : request.getSpecifications ( )) {
                // Try to find existing specification with same name
                Specification existingSpec = existingSpecs.stream ( )
                        .filter ( spec -> spec.getName ( ).equals ( specRequest.getName ( ) ) )
                        .findFirst ( )
                        .orElse ( null );

                if ( existingSpec != null ) {
                    // Update existing specification
                    if ( specRequest.getValue ( ) != null ) {
                        existingSpec.setValue ( specRequest.getValue ( ) );
                    }
                    updatedSpecs.add ( existingSpec );
                } else {
                    // Create new specification
                    Specification newSpec = specRequest.requestToSpecification ( );
                    updatedSpecs.add ( newSpec );
                }
            }

            product.setSpecification ( updatedSpecs );
        }

        // Update images if imageIds are provided
        if ( request.getImageIds ( ) != null ) {
            // Create a copy of the current images to safely remove them
            List<Image> currentImages = new ArrayList<> ( product.getImages ( ) );

            // Remove all current images from the product
            for (Image image : currentImages) {
                product.removeImage ( image );
            }

            // Add new images
            if ( ! request.getImageIds ( ).isEmpty ( ) ) {
                List<Image> images = imageRepository.findAllById ( request.getImageIds ( ) );
                logger.debug ( "Found {} images by IDs for update", images.size ( ) );

                // Add each image individually to maintain bidirectional relationship
                for (Image image : images) {
                    product.addImage ( image );
                }
            }
        }

        // For backward compatibility - handle imageUrls if provided
        /*
        if ( request.getImageUrls ( ) != null ) {
            // Clear existing images if we're explicitly setting new ones and imageIds wasn't provided
            if ( request.getImageIds ( ) == null ) {
                // Create a copy of the current images to safely remove them
                List<Image> currentImages = new ArrayList<> ( product.getImages ( ) );

                // Remove all current images from the product
                for (Image image : currentImages) {
                    product.removeImage ( image );
                }
            }

            // Add images from URLs
            for (String url : request.getImageUrls ( )) {
                // Check if this URL already exists in our images list to avoid duplicates
                boolean urlAlreadyExists = product.getImages ( ).stream ( )
                        .anyMatch ( img -> img.getImageUrl ( ).equals ( url ) );

                if ( ! urlAlreadyExists ) {
                    // Try to find existing image entity with this URL
                    Image image = imageRepository.findByImageUrl ( url )
                            .orElseGet ( () -> {
                                Image newImage = new Image ( );
                                newImage.setImageUrl ( url );
                                return imageRepository.save ( newImage );
                            } );
                    product.addImage ( image );
                }
            }
        }
        */

        //update Tag N Keyword
        if ( request.getTagNKeywords ( ) != null ) {
            product.setTagNKeywords ( request.getTagNKeywords ( ) );
        }

        // Update OurServices if IDs are provided
        if ( request.getOurServiceIds ( ) != null ) {
            // Clear existing services
            if ( product.getOurServices ( ) != null ) {
                product.getOurServices ( ).clear ( );
            } else {
                product.setOurServices ( new ArrayList<> ( ) );
            }

            // Add services by ID
            if ( ! request.getOurServiceIds ( ).isEmpty ( ) ) {
                List<OurService> ourServices = ourServiceRepository.findAllById ( request.getOurServiceIds ( ) );

                if ( ourServices.size ( ) != request.getOurServiceIds ( ).size ( ) ) {
                    logger.warn ( "Some OurService IDs were not found. Found {} out of {} requested services.",
                            ourServices.size ( ), request.getOurServiceIds ( ).size ( ) );
                }

                product.getOurServices ( ).addAll ( ourServices );
            }
        }

        // Save the updated product
        Product updatedProduct = productRepository.save ( product );

        if ( logger.isDebugEnabled ( ) ) {
            logger.debug ( "Updated product with ID: {} and {} images",
                    updatedProduct.getProductId ( ),
                    updatedProduct.getImages ( ).size ( ) );
        }

        return new ProductResponse ( updatedProduct );
    }


    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "products", key = "'allProducts'"),
            @CacheEvict(value = "productSearchCache", key = "#query.toLowerCase().trim()")
    }
    )
    public void deleteProduct(Long id) {
        try {
            logger.debug ( "Attempting to delete product with id: {}", id );

            Product product = productRepository.findById ( id )
                    .orElseThrow ( () -> new RuntimeException ( PRODUCT_NOT_FOUND_WITH_ID + id ) );

            List<Wishlist> wishlists = wishlistRepository.findByProductsContaining(product);
            for (Wishlist wishlist : wishlists) {
                wishlist.getProducts().remove(product);
            }
            wishlistRepository.saveAll(wishlists);

            List<CartItem> byProductProductId = cartItemRepository.findByProductProductId ( product.getProductId ( ) );
            for (CartItem cartItem : byProductProductId) {
                cartItemRepository.delete ( cartItem );
            }

            List<OrderItem> orderItems = orderItemRepository.findByProductProductId(product.getProductId());
            if (!orderItems.isEmpty()) {
                orderItemRepository.deleteAll(orderItems);
            }

            // First, remove all service associations
            if ( product.getOurServices ( ) != null && ! product.getOurServices ( ).isEmpty ( ) ) {
                logger.debug ( "Removing {} service associations from product", product.getOurServices ( ).size ( ) );

                // Create a copy to avoid ConcurrentModificationException
                List<OurService> servicesToRemove = new ArrayList<> ( product.getOurServices ( ) );

                // Remove each service association
                for (OurService service : servicesToRemove) {
                    product.removeOurService ( service );
                }

                // Save the product to update the associations
                product = productRepository.save ( product );
            }

            // Remove image associations (existing code)
            if ( product.getImages ( ) != null && ! product.getImages ( ).isEmpty ( ) ) {
                logger.debug ( "Removing {} image associations from product", product.getImages ( ).size ( ) );

                List<Image> imagesToRemove = new ArrayList<> ( product.getImages ( ) );

                for (Image image : imagesToRemove) {
                    product.removeImage ( image );
                }

                product = productRepository.save ( product );
            }

            // Now delete the product
            productRepository.delete ( product );
            logger.debug ( "Successfully deleted product with id: {}", id );
        }
        catch ( Exception e ) {
            logger.error ( "Error deleting product with id: " + id, e );
            throw new RuntimeException ( "Failed to delete product with id: " + id, e );
        }
    }

    @Override
    @Transactional
    public ProductResponse addImageToProduct(Long productId, Long imageId) {
        logger.debug ( "Adding image {} to product {}", imageId, productId );

        // Find the product
        Product product = productRepository.findById ( productId )
                .orElseThrow ( () -> new RuntimeException ( PRODUCT_NOT_FOUND_WITH_ID + productId ) );

        // Find the image
        Image image = imageRepository.findById ( imageId )
                .orElseThrow ( () -> new RuntimeException ( "Image not found with id: " + imageId ) );

        // Check if the product already has this image
        boolean hasImage = product.getImages ( ).stream ( )
                .anyMatch ( img -> img.getImageId ( ).equals ( imageId ) );

        if ( hasImage ) {
            logger.warn ( "Image {} is already associated with product {}", imageId, productId );
            return new ProductResponse ( product ); // Return unchanged
        }

        // Add the image to the product
        product.addImage ( image );

        // Save the product
        Product updatedProduct = productRepository.save ( product );
        logger.debug ( "Successfully added image {} to product {}", imageId, productId );

        return new ProductResponse ( updatedProduct );
    }

    @Override
    @Transactional
    public ProductResponse removeImageFromProduct(Long productId, Long imageId) {
        logger.debug ( "Removing image {} from product {}", imageId, productId );

        // Find the product
        Product product = productRepository.findById ( productId )
                .orElseThrow ( () -> new ResourceNotFoundException ( PRODUCT_NOT_FOUND_WITH_ID + productId ) );

        // Find the image
        Image image = imageRepository.findById ( imageId )
                .orElseThrow ( () -> new ResourceNotFoundException ( "Image not found with id: " + imageId ) );

        // Check if the product has this image
        boolean hasImage = product.getImages ( ).stream ( )
                .anyMatch ( img -> img.getImageId ( ).equals ( imageId ) );

        if ( ! hasImage ) {
            logger.warn ( "Image {} is not associated with product {}", imageId, productId );
            throw new ResourceNotFoundException ( "Image is not associated with this product" );
        }

        // Remove the image from the product
        product.removeImage ( image );

        // Save the product
        Product updatedProduct = productRepository.save ( product );
        logger.debug ( "Successfully removed image {} from product {}", imageId, productId );

        // Check if the image is now orphaned (not associated with any products)
        if ( image.getProducts ( ) == null || image.getProducts ( ).isEmpty ( ) ) {
            logger.debug ( "Image {} is now orphaned, marking for potential cleanup", imageId );
            // You could delete it here or leave it for the cleanup job
        }

        return new ProductResponse ( updatedProduct );
    }

    private Rent updateRentFields(Rent existingRent, RentRequest rentRequest) {
        if ( rentRequest.getMonthlyPrice ( ) != null ) {
            existingRent.setMonthlyPrice ( rentRequest.getMonthlyPrice ( ) );
        }
        if ( rentRequest.getDiscountUnit ( ) == UNIT.AED ) {
            existingRent.setDiscountPrice ( existingRent.getMonthlyPrice ( ) - rentRequest.getDiscountValue ( ) );
            existingRent.setDiscountUnit ( UNIT.AED );
            existingRent.setDiscountValue ( rentRequest.getDiscountValue ( ) );
        } else if ( rentRequest.getDiscountUnit ( ) == UNIT.PERCENTAGE ) {
            existingRent.setDiscountPrice ( existingRent.getMonthlyPrice ( ) -
                    (existingRent.getMonthlyPrice ( ) * (rentRequest.getDiscountValue ( )
                            / 100)) );
            existingRent.setDiscountUnit ( UNIT.PERCENTAGE );
            existingRent.setDiscountValue ( rentRequest.getDiscountValue ( ) );
        }
        if ( Boolean.TRUE.equals ( rentRequest.getIsVatIncluded ( ) ) ) {
            existingRent.setVat ( vat );
            existingRent.setDiscountPrice ( existingRent.getMonthlyPrice ( ) +
                    (existingRent.getMonthlyPrice ( ) * (existingRent.getVat ( )
                            / 100)) );
        } else if ( Boolean.FALSE.equals ( rentRequest.getIsVatIncluded ( ) ) ) {
            existingRent.setVat ( 0.0 );
        }

        if ( rentRequest.getBenefits ( ) != null ) {
            existingRent.setBenefits ( rentRequest.getBenefits ( ) );
        }

        return existingRent;
    }

    private Sell updateSellFields(Sell existingSell, SellRequest sellRequest) {
        if ( sellRequest.getActualPrice ( ) != null ) {
            existingSell.setActualPrice ( sellRequest.getActualPrice ( ) );
        }
        if ( sellRequest.getBenefits ( ) != null ) {
            existingSell.setBenefits ( sellRequest.getBenefits ( ) );
        }
        if ( sellRequest.getDiscountUnit ( ) == UNIT.AED ) {
            existingSell.setDiscountUnit ( UNIT.AED );
            existingSell.setDiscountPrice ( existingSell.getActualPrice ( ) - sellRequest.getDiscountValue ( ) );
            existingSell.setDiscountValue ( sellRequest.getDiscountValue ( ) );
        } else if ( sellRequest.getDiscountUnit ( ) == UNIT.PERCENTAGE ) {
            existingSell.setDiscountUnit ( UNIT.PERCENTAGE );
            existingSell.setDiscountPrice ( existingSell.getActualPrice ( ) - (existingSell.getActualPrice ( ) * (sellRequest.getDiscountValue ( ) / 100)) );
            existingSell.setDiscountValue ( sellRequest.getDiscountValue ( ) );
        }
        if ( Boolean.TRUE.equals ( sellRequest.getIsVatIncluded ( )) ) {
            existingSell.setVat ( vat );
            existingSell.setDiscountPrice ( existingSell.getActualPrice ( ) +
                    (existingSell.getActualPrice ( ) * (existingSell.getVat ( )
                            / 100)) );
        } else if ( Boolean.FALSE.equals ( sellRequest.getIsVatIncluded ( )) ) {
            existingSell.setVat ( 0.0 );
        }
        if ( sellRequest.getWarrantPeriod ( ) != null && sellRequest.getWarrantPeriod ( ) > 0 ) {
            existingSell.setWarrantPeriod ( sellRequest.getWarrantPeriod ( ) );
        }
        return existingSell;
    }


    private void updateServiceFieldEntity(ServiceField existingField, ServiceFieldRequest request) {
        if ( request.getPrice ( ) != null ) {
            existingField.setPrice ( request.getPrice ( ) );
        }
        if ( request.getLimitedTimePeriods ( ) != null ) {
            existingField.setLimitedTimePeriods ( request.getLimitedTimePeriods ( ) );
        }
        if ( request.getBenefits ( ) != null ) {
            existingField.getBenefits ( ).clear ( );
            existingField.setBenefits ( new ArrayList<> ( request.getBenefits ( ) ) );
        }

    }

    @Override
    public List<ProductResponse> getProductsByType(ProductType productType) {
        try {
            List<Product> products;

            // Query products based on the product type
            switch (productType) {
                case SELL:
                    logger.debug ( "Fetching products available for SELL" );
                    products = productRepository.findByProductForSellNotNull ( );
                    break;
                case RENT:
                    logger.debug ( "Fetching products available for RENT" );
                    products = productRepository.findByProductForRentNotNull ( );
                    break;
                case OTS:
                    logger.debug ( "Fetching products available for OTS" );
                    products = productRepository.findByProductForServicesOtsNotNull ( );
                    break;
                case MMC:
                    logger.debug ( "Fetching products available for MMC" );
                    products = productRepository.findByProductForServicesMmcNotNull ( );
                    break;
                case AMC_GOLD:
                    logger.debug ( "Fetching products available for AMC_GOLD" );
                    products = productRepository.findByProductForServicesAmcGoldNotNull ( );
                    break;
                case AMC_BASIC:
                    logger.debug ( "Fetching products available for AMC_BASIC" );
                    products = productRepository.findByProductForServicesAmcBasicNotNull ( );
                    break;
                default:
                    logger.warn ( "Unknown product type: {}", productType );
                    throw new IllegalArgumentException ( "Unknown product type: " + productType );
            }

            logger.debug ( "Found {} products of type {}", products.size ( ), productType );

            return products.stream ( )
                    .map ( ProductResponse::new )
                    .sorted ( (p1, p2) -> p2.getProductId ( ).compareTo ( p1.getProductId ( ) ) )
                    .toList ( );
        }
        catch ( Exception e ) {
            logger.error ( "Error retrieving products by type: " + productType, e );
            throw new RuntimeException ( "Failed to retrieve products by type: " + productType, e );
        }
    }

    @Override
    @Transactional
    public CheckOutResponse buyNow(Long productId, BuyNowRequest request) {
        logger.debug ( "Processing buy now request for product ID: {}", productId );
//
//        if (!request.isValid()) {
//            throw new IllegalArgumentException("Invalid buy now request");
//        }

        try {
            // Get the current user
            AppUser currentUser = securityUtils.getCurrentUser ( );

            // Find the product
            Product product = productRepository.findById ( productId )
                    .orElseThrow ( () -> new RuntimeException ( PRODUCT_NOT_FOUND_WITH_ID + productId ) );

            // Validate product configuration
            if ( product.getProductFor ( ) == null ) {
                throw new IllegalArgumentException ( "Product is not properly configured for purchase or rental" );
            }

            // Validate product type compatibility
            if ( request.getProductType ( ) == ProductType.SELL &&
                    (product.getProductFor ( ).getSell ( ) == null) ) {
                throw new IllegalArgumentException ( "This product is not available for purchase" );
            }

            if ( request.getProductType ( ) == ProductType.RENT &&
                    (product.getProductFor ( ).getRent ( ) == null) ) {
                throw new IllegalArgumentException ( "This product is not available for rent" );
            }

            // Create a temporary cart for this purchase
            Cart cart = new Cart ( );
            cart.setUser ( currentUser );
            cart.setTemporary ( true ); // Mark as temporary cart

            // Save the cart first to ensure it has an ID
            cart = cartRepository.save ( cart );
            logger.debug ( "Created temporary cart with ID: {}", cart.getCartId ( ) );

            // Create the cart item
            CartItem cartItem = new CartItem ( );
            cartItem.setProduct ( product );
            cartItem.setProductType ( request.getProductType ( ) );
//            cartItem.setQuantity ( 1 );

            // Set quantity and rent period based on product type
            if ( request.getProductType ( ) == ProductType.SELL ) {
                cartItem.setQuantity ( request.getQuantity ( ) );


                // Calculate price for sell item
                double unitPrice = product.getProductFor ( ).getSell ( ).getDiscountPrice ( );
                if ( unitPrice <= 0 ) {
                    unitPrice = product.getProductFor ( ).getSell ( ).getActualPrice ( );
                }
                logger.info ( "---->Unit price without vat: {}", unitPrice*cartItem.getQuantity ( ) );
                if ( product.getProductFor ( ).getSell ( ).getVat ( ) != null&& product.getProductFor ( ).getSell ( ).getVat ( ) != 0 ) {
                    unitPrice = unitPrice + (unitPrice * (product.getProductFor ( ).getSell ( ).getVat ( ) / 100));
                }
                logger.info ( "----->Unit price with vat: {}", unitPrice*cartItem.getQuantity ( ) );
                cartItem.setPrice ( unitPrice * cartItem.getQuantity ( ) );
            } else if ( request.getProductType ( ) == ProductType.RENT ) {
                cartItem.setQuantity ( request.getQuantity ( ) );


                // Calculate price for rent item
                double monthlyPrice = product.getProductFor ( ).getRent ( ).getDiscountPrice ( );
                if ( monthlyPrice <= 0 ) {
                    monthlyPrice = product.getProductFor ( ).getRent ( ).getMonthlyPrice ( );
                }
                logger.info ( "---->Unit price without vat: {}", monthlyPrice*cartItem.getQuantity ( ) );
                if ( product.getProductFor ( ).getRent ( ).getVat ( ) != null && product.getProductFor ( ).getRent ( ).getVat ( ) != 0 ) {
                    monthlyPrice = monthlyPrice + (monthlyPrice * (product.getProductFor ( ).getRent ( ).getVat ( ) / 100));
                }
                logger.info ( "----->Unit price with vat: {}", monthlyPrice*cartItem.getQuantity ( ) );
                cartItem.setPrice ( monthlyPrice * cartItem.getQuantity ( ) );
            } else if ( request.getProductType ( ) == ProductType.OTS ) {
                cartItem.setQuantity ( 1 );
                cartItem.setPrice ( product.getProductFor ( ).getServices ( ).getOts ( ).getPrice ( ) );
            } else if ( request.getProductType ( ) == ProductType.MMC ) {
                cartItem.setQuantity ( 1 );
                cartItem.setPrice ( product.getProductFor ( ).getServices ( ).getMmc ( ).getPrice ( ) );

            } else if ( request.getProductType ( ) == ProductType.AMC_GOLD ) {
                cartItem.setQuantity ( 1 );
                cartItem.setPrice ( product.getProductFor ( ).getServices ( ).getAmcBasic ( ).getPrice ( ) );

            } else if ( request.getProductType ( ) == ProductType.AMC_BASIC ) {
                cartItem.setQuantity ( 1 );
                cartItem.setPrice ( product.getProductFor ( ).getServices ( ).getAmcGold ( ).getPrice ( ) );
            }

            // Add to cart using the helper method
            cart.addItem ( cartItem );

            // Calculate total price for cart
            cart.calculateTotalPrice ( );

            // Save the cart again with the item
            cart = cartRepository.save ( cart );
            logger.debug ( "Updated cart with item, total price: {}", cart.getTotalPrice ( ) );

            // Handle address
            Address deliveryAddress = null;

            // If addressId is provided, use that address
            if ( request.getAddressId ( ) != null ) {
                deliveryAddress = addressRepository.findById ( request.getAddressId ( ) )
                        .orElseThrow ( () -> new RuntimeException ( "Address not found with id: " + request.getAddressId ( ) ) );

                // Verify address belongs to current user
                if ( ! deliveryAddress.getUser ( ).getUserId ( ).equals ( currentUser.getUserId ( ) ) ) {
                    throw new RuntimeException ( "You don't have permission to use this address" );
                }
            }
            // If inline address is provided, create a new address
            else if ( request.getAddress ( ) != null ) {
                AddressRequest addressRequest = request.getAddress ( );
                deliveryAddress = addressRequest.toAddress ( );
                deliveryAddress.setUser ( currentUser );
                deliveryAddress = addressRepository.save ( deliveryAddress );
            }

            // Create checkout
            CheckOut checkOut = new CheckOut ( );
            logger.info("cart id--------------------> {}",cart.getCartId());
            checkOut.setCart ( cart );
            logger.info("is cart temporary --------------------> {}",cart.isTemporary ());
            checkOut.setFirstName ( request.getFirstName ( ) );
            checkOut.setLastName ( request.getLastName ( ) );
            checkOut.setMobile ( request.getMobile ( ) );
            checkOut.setEmail ( request.getEmail ( ) );


            if ( deliveryAddress != null ) {
                checkOut.setDeliveryAddress ( deliveryAddress );
                checkOut.setHomeAddress ( deliveryAddress.getFormattedAddress ( ) );
            }
            // Save checkout
            CheckOut savedCheckOut = checkOutRepository.save ( checkOut );
            logger.debug ( "Created checkout with ID: {}", savedCheckOut.getCheckoutId ( ) );

            // Place order immediately
            CheckOutResponse checkOutResponse = checkOutService.placeOrder ( savedCheckOut.getCheckoutId ( ) );
            logger.debug ( "Placed order for checkout with ID: {}", savedCheckOut.getCheckoutId ( ) );

            return checkOutResponse;
        }
        catch ( Exception e ) {
            logger.error ( "Error processing buy now request: {}", e.getMessage ( ), e );
            throw e;
        }
    }

    @Override
    @Transactional
    public ProductResponse addServiceToProduct(Long productId, Long ourServiceId) {
        logger.debug ( "Adding service {} to product {}", ourServiceId, productId );

        Product product = productRepository.findById ( productId )
                .orElseThrow ( () -> new RuntimeException ( PRODUCT_NOT_FOUND_WITH_ID + productId ) );

        OurService ourService = ourServiceRepository.findById ( ourServiceId )
                .orElseThrow ( () -> new RuntimeException ( "OurService not found with id: " + ourServiceId ) );

        // Check if the service is already associated with the product
        boolean serviceExists = false;
        if ( product.getOurServices ( ) != null ) {
            serviceExists = product.getOurServices ( ).stream ( )
                    .anyMatch ( service -> service.getOurServiceId ( ).equals ( ourServiceId ) );
        } else {
            product.setOurServices ( new ArrayList<> ( ) );
        }

        if ( ! serviceExists ) {
            product.addOurService ( ourService );
            product = productRepository.save ( product );
            logger.debug ( "Successfully added service {} to product {}", ourServiceId, productId );
        } else {
            logger.debug ( "Service {} is already associated with product {}", ourServiceId, productId );
        }

        return new ProductResponse ( product );
    }

    @Override
    @Transactional
    public ProductResponse removeServiceFromProduct(Long productId, Long ourServiceId) {
        logger.debug ( "Removing service {} from product {}", ourServiceId, productId );

        Product product = productRepository.findById ( productId )
                .orElseThrow ( () -> new RuntimeException ( PRODUCT_NOT_FOUND_WITH_ID + productId ) );

        if ( product.getOurServices ( ) == null || product.getOurServices ( ).isEmpty ( ) ) {
            logger.warn ( "Product {} has no services", productId );
            return new ProductResponse ( product );
        }

        OurService serviceToRemove = product.getOurServices ( ).stream ( )
                .filter ( service -> service.getOurServiceId ( ).equals ( ourServiceId ) )
                .findFirst ( )
                .orElse ( null );

        if ( serviceToRemove == null ) {
            logger.warn ( "Service {} not found in product {}", ourServiceId, productId );
            return new ProductResponse ( product );
        }

        product.removeOurService ( serviceToRemove );
        product = productRepository.save ( product );
        logger.debug ( "Successfully removed service {} from product {}", ourServiceId, productId );

        return new ProductResponse ( product );
    }

    @Override
    public List<ProductResponse> searchByProductName(String query) {
        List<Product> products = productRepository.searchWithRelationships ( query );
        return products.stream ( ).map ( ProductResponse::new ).toList ( );
    }


    @Cacheable(value = "productSearchCache", key = "#query.toLowerCase().trim()")
    @Override
    public List<ProductResponse> searchByProductName1(String query) {
        String normalizedQuery = query.toLowerCase ( ).trim ( );

        // Use direct database search instead of loading all products
        List<Product> results = productRepository.searchWithRelationships ( normalizedQuery );

        return results.stream ( )
                .map ( ProductResponse::new )
                .toList ( );
    }

}
