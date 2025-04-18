package com.mylstech.rentro.impl;

import com.mylstech.rentro.dto.request.ProductRequest;
import com.mylstech.rentro.dto.request.ServiceRequest;
import com.mylstech.rentro.dto.request.SpecificationRequest;
import com.mylstech.rentro.dto.response.ProductResponse;
import com.mylstech.rentro.model.*;
import com.mylstech.rentro.repository.*;
import com.mylstech.rentro.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductForRepository productForRepository;
    private final SellRepository sellRepository;
    private final RentRepository rentRepository;
    private final RequestQuotationRepository requestQuotationRepository;
    private final ServiceRepository serviceRepository;
    private final ProductImagesRepository productImagesRepository;
    private final SpecificationFieldRepository specificationFieldRepository;


    @Value("${vat.value}")
    private Double vat;

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll ( ).stream ( )
                .map ( ProductResponse::new )
                .collect ( Collectors.toList ( ) );
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById ( id )
                .orElseThrow ( () -> new RuntimeException ( "Product not found with id: " + id ) );
        return new ProductResponse ( product );
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
                sell.setVat ( vat );
                sell = sellRepository.save ( sell );
                productFor.setSell ( sell );
            }

            // Handle Rent entity
            if ( request.getProductFor ( ).getRent ( ) != null ) {

                // Create new Rent entity
                Rent rent = request.getProductFor ( ).getRent ( ).requestToRent ( );
                rent.setVat ( vat );
                rent = rentRepository.save ( rent );
                productFor.setRent ( rent );
            }

            // Handle RequestQuotation entity
            if ( request.getProductFor ( ).getRequestQuotation ( ) != null ) {

                // Create new RequestQuotation entity
                RequestQuotation requestQuotation = request.getProductFor ( ).getRequestQuotation ( ).requestToRequestQuotation ( );
                requestQuotation.setVat ( vat );
                requestQuotation = requestQuotationRepository.save ( requestQuotation );
                productFor.setRequestQuotation ( requestQuotation );
            }

            // Handle Service entity
            if ( request.getProductFor ( ).getService ( ) != null ) {
                com.mylstech.rentro.model.Service service = new com.mylstech.rentro.model.Service ( );
                ServiceRequest serviceRequest = request.getProductFor ( ).getService ( );
                if ( serviceRequest.getAmcBasic ( ) != null ) {
                    service.setAmcBasic ( serviceRequest.getAmcBasic ( ).requestToServiceField ( ) );
                }
                if ( serviceRequest.getAmcGold ( ) != null ) {
                    service.setAmcGold ( serviceRequest.getAmcGold ( ).requestToServiceField ( ) );
                }
                if ( serviceRequest.getMmc ( ) != null ) {
                    service.setMmc ( serviceRequest.getMmc ( ).requestToServiceField ( ) );
                }
                if ( serviceRequest.getOts ( ) != null ) {
                    service.setOts ( serviceRequest.getOts ( ).requestToServiceField ( ) );
                }
                serviceRepository.save ( service );
            }

            // Save the ProductFor entity first
            productFor = productForRepository.save ( productFor );
        }

        // 3. Create specifications
        List<Specification> specifications = null;
        if ( request.getSpecifications ( ) != null && ! request.getSpecifications ( ).isEmpty ( ) ) {
            List<String> requestNameList = request.getSpecifications ( ).stream ( ).map ( SpecificationRequest::getName ).toList ( );
            List<String> dbSpecificationFieldList = specificationFieldRepository.findAll ( ).stream ( ).map ( SpecificationField::getName ).toList ( );
            // Find names in request not present in DB
            List<String> notFoundInDB = requestNameList.stream ( )
                    .filter ( name -> ! dbSpecificationFieldList.contains ( name ) )
                    .toList ( );

            // Print them
            notFoundInDB.forEach ( name -> {
                SpecificationField field = new SpecificationField ( );
                field.setName ( name );
                specificationFieldRepository.save ( field );
            } );
            specifications = request.getSpecifications ( ).stream ( )
                    .map ( specRequest -> specRequest.requestToSpecification ( ) )
                    .collect ( Collectors.toList ( ) );
        }

        // 4. Create the product
        Product product = new Product ( );
        product.setName ( request.getName ( ) );
        product.setDescription ( request.getDescription ( ) );
        product.setLongDescription ( request.getLongDescription ( ) );

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

        // 5. Create ProductImages with the provided image URLs
        ProductImages productImages = new ProductImages ( );
        if ( request.getImageUrls ( ) != null && ! request.getImageUrls ( ).isEmpty ( ) ) {
            productImages.setImageUrls ( new ArrayList<> ( request.getImageUrls ( ) ) );
        } else {
            productImages.setImageUrls ( new ArrayList<> ( ) );
        }
        product.setProductImages ( productImages );
        productImages.setProduct ( product );

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

        if ( request.getDescription ( ) != null ) {
            product.setDescription ( request.getDescription ( ) );
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
                Sell sell = request.getProductFor ( ).getSell ( ).requestToSell ( );
                sell.setVat ( vat );
                sell = sellRepository.save ( sell );
                productFor.setSell ( sell );
            }

            // Handle Rent entity
            if ( request.getProductFor ( ).getRent ( ) != null ) {

                // Create new Rent entity
                Rent rent = request.getProductFor ( ).getRent ( ).requestToRent ( );
                rent.setVat ( vat );
                rent = rentRepository.save ( rent );
                productFor.setRent ( rent );
            }

            // Handle RequestQuotation entity
            if ( request.getProductFor ( ).getRequestQuotation ( ) != null ) {

                // Create new RequestQuotation entity
                RequestQuotation requestQuotation = request.getProductFor ( ).getRequestQuotation ( ).requestToRequestQuotation ( );
                requestQuotation.setVat ( vat );
                requestQuotation = requestQuotationRepository.save ( requestQuotation );
                productFor.setRequestQuotation ( requestQuotation );
            }

            // Handle Service entity
            if ( request.getProductFor ( ).getService ( ) != null ) {
                com.mylstech.rentro.model.Service service = new com.mylstech.rentro.model.Service ( );
                ServiceRequest serviceRequest = request.getProductFor ( ).getService ( );
                if ( serviceRequest.getAmcBasic ( ) != null ) {
                    service.setAmcBasic ( serviceRequest.getAmcBasic ( ).requestToServiceField ( ) );
                }
                if ( serviceRequest.getAmcGold ( ) != null ) {
                    service.setAmcGold ( serviceRequest.getAmcGold ( ).requestToServiceField ( ) );
                }
                if ( serviceRequest.getMmc ( ) != null ) {
                    service.setMmc ( serviceRequest.getMmc ( ).requestToServiceField ( ) );
                }
                if ( serviceRequest.getOts ( ) != null ) {
                    service.setOts ( serviceRequest.getOts ( ).requestToServiceField ( ) );
                }
                serviceRepository.save ( service );
            }

            // Save the ProductFor entity first
            productFor = productForRepository.save ( productFor );
        }

        // Update specifications if provided
        if ( request.getSpecifications ( ) != null && ! request.getSpecifications ( ).isEmpty ( ) ) {
            List<Specification> specifications = request.getSpecifications ( ).stream ( )
                    .map ( specRequest -> specRequest.requestToSpecification ( ) )
                    .collect ( Collectors.toList ( ) );
            product.setSpecification ( specifications );
        }

        // Update image URLs if provided
        if ( request.getImageUrls ( ) != null ) {
            ProductImages productImages = product.getProductImages ( );
            if ( productImages == null ) {
                productImages = new ProductImages ( );
                productImages.setProduct ( product );
                product.setProductImages ( productImages );
            }
            productImages.setImageUrls ( new ArrayList<> ( request.getImageUrls ( ) ) );
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

        ProductImages productImages = product.getProductImages ( );
        if ( productImages == null ) {
            productImages = new ProductImages ( );
            productImages.setImageUrls ( new ArrayList<> ( ) );
            productImages.setProduct ( product );
            product.setProductImages ( productImages );
        }

        List<String> imageUrls = productImages.getImageUrls ( );
        if ( imageUrls == null ) {
            imageUrls = new ArrayList<> ( );
            productImages.setImageUrls ( imageUrls );
        }

        if ( ! imageUrls.contains ( imageUrl ) ) {
            imageUrls.add ( imageUrl );
        }

        productRepository.save ( product );

        return new ProductResponse ( product );
    }

    @Override
    @Transactional
    public ProductResponse removeImageFromProduct(Long productId, String imageUrl) {
        Product product = productRepository.findById ( productId )
                .orElseThrow ( () -> new RuntimeException ( "Product not found with id: " + productId ) );

        ProductImages productImages = product.getProductImages ( );
        if ( productImages != null && productImages.getImageUrls ( ) != null ) {
            productImages.getImageUrls ( ).remove ( imageUrl );
            productRepository.save ( product );
        }

        return new ProductResponse ( product );
    }
}
