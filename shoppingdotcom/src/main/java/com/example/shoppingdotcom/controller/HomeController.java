package com.example.shoppingdotcom.controller;

import com.example.shoppingdotcom.model.Category;
import com.example.shoppingdotcom.model.Product;
import com.example.shoppingdotcom.service.CategoryService;
import com.example.shoppingdotcom.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String index(Model m) {
        return "index";
    }

    @GetMapping("/login")
    public String login(Model m) {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model m) {
        return "register";
    }

    @GetMapping("/products")
    public String listAllProducts(Model m, @RequestParam(value = "category", defaultValue = "") String category) {
        List<Category> categories = categoryService.getAllActiveCategory();
        List<Product> products = productService.getAllActiveProducts(category);
        m.addAttribute("categories", categories);
        m.addAttribute("products", products);
        m.addAttribute("paramValue", category);
        return "product";
    }

    @GetMapping("/product/{id}")
    public String viewCurrentProduct(@PathVariable int id, Model m) {
        Product product = productService.getProductById(id);
        m.addAttribute("product", product);
        return "view_product";
    }
}
