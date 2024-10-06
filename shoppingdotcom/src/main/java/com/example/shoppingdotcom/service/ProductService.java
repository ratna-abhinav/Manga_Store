package com.example.shoppingdotcom.service;

import com.example.shoppingdotcom.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    public Product saveProduct(Product product);

    public List<Product> getAllProducts();

    Page<Product> getAllProductsPagination(Integer pageNo, Integer pageSize);

    public Boolean deleteProduct(Integer id);

    public Product getProductById(Integer id);

    List<Product> getProductsByCategory(String category);

    public Product updateProduct(Product product, MultipartFile file);

    List<Product> getAllActiveProducts(String category);

    List<Product> searchProduct(String keyword);

    List<Product> searchProductAdmin(String keyword);

    Page<Product> searchProductPagination(Integer pageNo, Integer pageSize, String ch);

    Page<Product> searchProductAdminPagination(Integer pageNo, Integer pageSize, String keyword);

    Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize, String category);
}
