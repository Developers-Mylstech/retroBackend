package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.*;
import com.mylstech.rentro.dto.response.CheckOutResponse;
import com.mylstech.rentro.dto.response.ProductResponse;
import com.mylstech.rentro.model.*;
import com.mylstech.rentro.repository.*;
import com.mylstech.rentro.service.CheckOutService;
import com.mylstech.rentro.service.ImageService;
import com.mylstech.rentro.service.ProductService;
import com.mylstech.rentro.util.CHECKOUT_STATUS;
import com.mylstech.rentro.util.ProductType;
import com.mylstech.rentro.util.SecurityUtils;
import com.mylstech.rentro.util.UNIT;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final Logger logger = LoggerFactory.getLogger ( ProductServiceImpl.class );
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductForRepository productForRepository;
    private final SellRepository sellRepository;
    private final RentRepository rentRepository;
    private final RequestQuotationRepository requestQuotationRepository;
    private final ServiceRepository serviceRepository;

    private final SpecificationFieldRepository specificationFieldRepository;
    private final ServiceFieldRepository serviceFieldRepository;
    private final AddressRepository addressRepository;
    private final SecurityUtils securityUtils;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final CheckOutRepository checkOutRepository;
    private final CheckOutService checkOutService;
    private final ImageService imageService;

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
    public List<ProductResponse> getAllProducts() {
        try {
            List<Product> products = productRepository.findAll ( );
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
                    .orElseThrow ( () -> new RuntimeException ( "Product not found with id: " + id ) );
            logger.debug ( "Found product with id {}: {}", id, product );
            return new ProductResponse ( product );
        }
        catch ( Exception e ) {
            logger.error ( "Error retrieving product with id: " + id, e );
            throw e;
        }
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
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

        // 5. Create ProductImages with the provided image URLs

        if ( request.getImageUrls ( ) != null && ! request.getImageUrls ( ).isEmpty ( ) ) {

            product.setImageUrls ( request.getImageUrls ( ) );

        } else {
            product.setImageUrls(new ArrayList<>());
        }
        // 6. Save the product
        Product savedProduct = productRepository.save ( product );

        return new ProductResponse ( savedProduct );
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById ( id )
                .orElseThrow ( () -> new RuntimeException ( "Product not found with id: " + id ) );

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

        // Update image URLs if provided
        if ( request.getImageUrls ( ) != null ) {

            product.getImageUrls().clear();
            product.setImageUrls ( new ArrayList<> ( request.getImageUrls ( ) ) );
        }

        //update Tag N Keyword
        if ( request.getTagNKeywords ( ) != null ) {
            product.setTagNKeywords ( request.getTagNKeywords ( ) );
        }
        // Save the updated product
        Product updatedProduct = productRepository.save ( product );

        return new ProductResponse ( updatedProduct );
    }


    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById ( id )
                .orElseThrow ( () -> new RuntimeException ( "Product not found with id: " + id ) );
        productRepository.delete ( product );
    }

    @Override
    @Transactional
    public ProductResponse addImageToProduct(Long productId, String imageUrl) {
        Product product = productRepository.findById ( productId )
                .orElseThrow ( () -> new RuntimeException ( "Product not found with id: " + productId ) );

        if ( product.getImageUrls ( ) == null ) {
            product.setImageUrls ( new ArrayList<> ( ) );
        }


        productRepository.save ( product );

        return new ProductResponse ( product );
    }

    @Override
    @Transactional
    public ProductResponse removeImageFromProduct(Long productId, String imageUrl) {
        Product product = productRepository.findById ( productId )
                .orElseThrow ( () -> new RuntimeException ( "Product not found with id: " + productId ) );

        if ( product.getImageUrls ( ) != null ) {
            product.getImageUrls ( ).remove ( imageUrl );
            productRepository.save ( product );
        }

        return new ProductResponse ( product );
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
        if ( rentRequest.getIsVatIncluded ( ) ) {
            existingRent.setVat ( vat );
            existingRent.setDiscountPrice ( existingRent.getMonthlyPrice ( ) +
                    (existingRent.getMonthlyPrice ( ) * (existingRent.getVat ( )
                            / 100)) );
        } else if ( ! rentRequest.getIsVatIncluded ( ) ) {
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
        if ( sellRequest.getIsVatIncluded ( ) ) {
            existingSell.setVat ( vat );
            existingSell.setDiscountPrice ( existingSell.getActualPrice ( ) +
                    (existingSell.getActualPrice ( ) * (existingSell.getVat ( )
                            / 100)) );
        } else if ( ! sellRequest.getIsVatIncluded ( ) ) {
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
                case SERVICE:
                    logger.debug ( "Fetching products available for SERVICE" );
                    products = productRepository.findByProductForServicesNotNull ( );
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
        logger.debug("Processing buy now request for product ID: {}", productId);

        if (!request.isValid()) {
            throw new IllegalArgumentException("Invalid buy now request");
        }

        try {
            // Get the current user
            AppUser currentUser = securityUtils.getCurrentUser();

            // Find the product
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

            // Validate product configuration
            if (product.getProductFor() == null) {
                throw new IllegalArgumentException("Product is not properly configured for purchase or rental");
            }

            // Validate product type compatibility
            if (request.getProductType() == ProductType.SELL &&
                    (product.getProductFor().getSell() == null)) {
                throw new IllegalArgumentException("This product is not available for purchase");
            }

            if (request.getProductType() == ProductType.RENT &&
                    (product.getProductFor().getRent() == null)) {
                throw new IllegalArgumentException("This product is not available for rent");
            }

            // Create a temporary cart for this purchase
            Cart cart = new Cart();
            cart.setUser(currentUser);
            cart.setTemporary(true); // Mark as temporary cart

            // Save the cart first to ensure it has an ID
            cart = cartRepository.save(cart);
            logger.debug("Created temporary cart with ID: {}", cart.getCartId());

            // Create the cart item
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setProductType(request.getProductType());

            // Set quantity and rent period based on product type
            if (request.getProductType() == ProductType.SELL) {
                cartItem.setQuantity(request.getQuantity());


                // Calculate price for sell item
                double unitPrice = product.getProductFor().getSell().getDiscountPrice();
                if (unitPrice <= 0) {
                    unitPrice = product.getProductFor().getSell().getActualPrice();
                }
                if (product.getProductFor().getSell().getVat() != null) {
                    unitPrice = unitPrice + (unitPrice * (product.getProductFor().getSell().getVat() / 100));
                }
                cartItem.setPrice(unitPrice * cartItem.getQuantity());
            } else if (request.getProductType() == ProductType.RENT) {
                cartItem.setQuantity(request.getQuantity());


                // Calculate price for rent item
                double monthlyPrice = product.getProductFor().getRent().getDiscountPrice();
                if (monthlyPrice <= 0) {
                    monthlyPrice = product.getProductFor().getRent().getMonthlyPrice();
                }
                if (product.getProductFor().getRent().getVat() != null) {
                    monthlyPrice = monthlyPrice + (monthlyPrice * (product.getProductFor().getRent().getVat() / 100));
                }
                cartItem.setPrice(monthlyPrice * cartItem.getQuantity() );
            }

            // Add to cart using the helper method
            cart.addItem(cartItem);

            // Calculate total price for cart
            cart.calculateTotalPrice();

            // Save the cart again with the item
            cart = cartRepository.save(cart);
            logger.debug("Updated cart with item, total price: {}", cart.getTotalPrice());

            // Handle address
            Address deliveryAddress = null;

            // If addressId is provided, use that address
            if (request.getAddressId() != null) {
                deliveryAddress = addressRepository.findById(request.getAddressId())
                        .orElseThrow(() -> new RuntimeException("Address not found with id: " + request.getAddressId()));

                // Verify address belongs to current user
                if (!deliveryAddress.getUser().getUserId().equals(currentUser.getUserId())) {
                    throw new RuntimeException("You don't have permission to use this address");
                }
            }
            // If inline address is provided, create a new address
            else if (request.getAddress() != null) {
                AddressRequest addressRequest = request.getAddress();
                deliveryAddress = addressRequest.toAddress();
                deliveryAddress.setUser(currentUser);
                deliveryAddress = addressRepository.save(deliveryAddress);
            }

            // Create checkout
            CheckOut checkOut = new CheckOut();
            checkOut.setCart(cart);
            checkOut.setName(request.getName());
            checkOut.setMobile(request.getMobile());
            checkOut.setEmail(request.getEmail());
            checkOut.setDeliveryDate(request.getDeliveryDate().toLocalDate ());
            checkOut.setPaymentOption(request.getPaymentOption());
            checkOut.setStatus(CHECKOUT_STATUS.PENDING);

            if (deliveryAddress != null) {
                checkOut.setDeliveryAddress(deliveryAddress);
                checkOut.setHomeAddress(deliveryAddress.getFormattedAddress());
            }

            // Save checkout
            CheckOut savedCheckOut = checkOutRepository.save(checkOut);
            logger.debug("Created checkout with ID: {}", savedCheckOut.getCheckoutId());

            // Place order immediately
            CheckOutResponse checkOutResponse = checkOutService.placeOrder(savedCheckOut.getCheckoutId());
            logger.debug("Placed order for checkout with ID: {}", savedCheckOut.getCheckoutId());

            return checkOutResponse;
        } catch (Exception e) {
            logger.error("Error processing buy now request: {}", e.getMessage(), e);
            throw e;
        }
    }
}
