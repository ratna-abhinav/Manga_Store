package com.example.shoppingdotcom.service;

import com.example.shoppingdotcom.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    public Product saveProduct(Product product);

    public List<Product> getAllProducts();

    public Boolean deleteProduct(Integer id);

    public Product getProductById(Integer id);

    public Product updateProduct(Product product, MultipartFile file);

    List<Product> getAllActiveProducts(String category);

    List<Product> searchProduct(String keyword);

    List<Product> searchProductAdmin(String keyword);
}
