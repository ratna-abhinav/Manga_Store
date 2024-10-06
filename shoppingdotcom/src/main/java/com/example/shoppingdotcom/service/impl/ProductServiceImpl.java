package com.example.shoppingdotcom.service.impl;

import com.example.shoppingdotcom.model.Category;
import com.example.shoppingdotcom.model.Product;
import com.example.shoppingdotcom.repository.CategoryRepository;
import com.example.shoppingdotcom.repository.ProductRepository;
import com.example.shoppingdotcom.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Page<Product> getAllProductsPagination(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return productRepository.findAll(pageable);
    }

    @Override
    public Boolean deleteProduct(Integer id) {
        Product product = productRepository.findById(id).orElse(null);

        if (!ObjectUtils.isEmpty(product)) {
            productRepository.delete(product);
            return true;
        }
        return false;
    }

    @Override
    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public Product updateProduct(Product product, MultipartFile image) {

        Product dbProduct = getProductById(product.getId());
        String imageName = image.isEmpty() ? dbProduct.getImage() : image.getOriginalFilename();

        dbProduct.setTitle(product.getTitle());
        dbProduct.setDescription(product.getDescription());
        dbProduct.setCategory(product.getCategory());
        dbProduct.setPrice(product.getPrice());
        dbProduct.setStock(product.getStock());
        dbProduct.setImage(imageName);
        dbProduct.setIsActive(product.getIsActive());
        dbProduct.setDiscount(product.getDiscount());

        Double disocuntValue = product.getPrice() * (product.getDiscount() / 100.0);
        Double discountedPrice = product.getPrice() - disocuntValue;
        dbProduct.setDiscountedPrice(discountedPrice);

        Product updateProduct = productRepository.save(dbProduct);
        if (!ObjectUtils.isEmpty(updateProduct)) {
            if (!image.isEmpty()) {
                try {
                    File saveFile = new ClassPathResource("static/img").getFile();
                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator + image.getOriginalFilename());
                    Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return product;
        }
        return null;
    }

    @Override
    public List<Product> getAllActiveProducts(String category) {
        List<Product> products = null;
        if (ObjectUtils.isEmpty(category)) {
            List<Category> activeCategories = categoryRepository.findByIsActiveTrue();
            for (Category curCategory : activeCategories) {
                List<Product> curCategoryProducts = productRepository.findByIsActiveTrueAndCategory(curCategory.getName());
                if (!ObjectUtils.isEmpty(curCategoryProducts)) {
                    if (products == null) products = curCategoryProducts;
                    else products.addAll(curCategoryProducts);
                }
            }
        } else {
            products = productRepository.findByIsActiveTrueAndCategory(category);
        }
        return products;
    }

    @Override
    public List<Product> searchProduct(String keyword) {
        return productRepository.findByIsActiveTrueAndTitleContainingIgnoreCaseOrIsActiveTrueAndCategoryContainingIgnoreCase(keyword, keyword);
    }

    @Override
    public List<Product> searchProductAdmin(String keyword) {
        return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(keyword, keyword);
    }

    @Override
    public Page<Product> searchProductPagination(Integer pageNo, Integer pageSize, String keyword) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return productRepository.findByIsActiveTrueAndTitleContainingIgnoreCaseOrIsActiveTrueAndCategoryContainingIgnoreCase(keyword, keyword, pageable);
    }

    @Override
    public Page<Product> searchProductAdminPagination(Integer pageNo, Integer pageSize, String keyword) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(keyword, keyword, pageable);
    }


    @Override
    public Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize, String category) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Product> productsPage;

        if (ObjectUtils.isEmpty(category)) {
            List<Product> allProducts = new ArrayList<>();
            List<Category> activeCategories = categoryRepository.findByIsActiveTrue();

            for (Category curCategory : activeCategories) {
                Page<Product> curCategoryProductsPage = productRepository.findByIsActiveTrueAndCategory(curCategory.getName(), Pageable.unpaged());
                if (!curCategoryProductsPage.isEmpty()) {
                    allProducts.addAll(curCategoryProductsPage.getContent());
                }
            }
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageSize), allProducts.size());

            List<Product> paginatedProducts;
            if (start > allProducts.size()) {
                paginatedProducts = new ArrayList<>();
            } else {
                paginatedProducts = allProducts.subList(start, end);
            }
            productsPage = new PageImpl<>(paginatedProducts, pageable, allProducts.size());
        } else {
            productsPage = productRepository.findByIsActiveTrueAndCategory(category, pageable);
        }
        return productsPage;
    }

}
