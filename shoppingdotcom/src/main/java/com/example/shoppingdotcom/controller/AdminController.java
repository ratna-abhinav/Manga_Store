package com.example.shoppingdotcom.controller;

import com.example.shoppingdotcom.model.Category;
import com.example.shoppingdotcom.model.Product;
import com.example.shoppingdotcom.model.ProductOrder;
import com.example.shoppingdotcom.model.Users;
import com.example.shoppingdotcom.service.*;
import com.example.shoppingdotcom.util.CommonUtils;
import com.example.shoppingdotcom.util.OrderStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @ModelAttribute
    public void getUserDetails(Principal p, Model m) {
        if (p != null) {
            String email = p.getName();
            Users currentUser = userService.getUserByEmail(email);
            m.addAttribute("users", currentUser);
            Integer countCart = cartService.getCountCart(currentUser.getId());
            m.addAttribute("countCart", countCart);
        }
        List<Category> activeCategories = categoryService.getAllActiveCategory();
        m.addAttribute("activeCategoriesSection", activeCategories);
    }


    @GetMapping("/")
    public String index() {
        return "admin/index";
    }

    @GetMapping("/loadAddProduct")
    public String loadAddProduct(Model m) {
        m.addAttribute("categories", categoryService.getAllCategory());
        return "admin/add_product";
    }

    @GetMapping("/add-admin")
    public String loadAdminAdd() {
        return "/admin/add_admin";
    }

    @GetMapping("/category")
    public String category(Model m) {
        m.addAttribute("categories", categoryService.getAllCategory());
        return "admin/category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {

        String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        category.setImageName(imageName);

        Boolean existsCategory = categoryService.existsCategory(category.getName());
        if (existsCategory) {
            session.setAttribute("errorMsg", "Category name already exists");
        } else {
            Category saveCategory = categoryService.saveCategory(category);
            if (ObjectUtils.isEmpty(saveCategory)) {
                session.setAttribute("errorMsg", "Category not saved! Internal server error");
            } else {
                try {
                    File folderPath = new ClassPathResource("static/img").getFile();
                    Path filePath = Paths.get(folderPath.getAbsolutePath() + File.separator + "category_img" + File.separator + imageName);

                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    session.setAttribute("succMsg", "Category saved successfully");
                } catch (IOException e) {
                    session.setAttribute("errorMsg", "Failed to save the category image: " + e.getMessage());
                }
            }
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id, HttpSession session) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            session.setAttribute("errorMsg", "Category not found");
            return "redirect:/admin/category";
        }

        Boolean deleteCategory = categoryService.deleteCategory(id);
        if (deleteCategory) {
            try {
                File folderPath = new ClassPathResource("static/img/category_img").getFile();
                Path filePath = Paths.get(folderPath.getAbsolutePath() + File.separator + category.getImageName());

                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
                session.setAttribute("succMsg", "Category deleted successfully");
            } catch (IOException e) {
                session.setAttribute("errorMsg", "Failed to delete category image: " + e.getMessage());
            }
        } else {
            session.setAttribute("errorMsg", "Category not deleted! Internal server error");
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable int id, Model m) {
        m.addAttribute("category", categoryService.getCategoryById(id));
        return "admin/edit_category";
    }

    @PostMapping("/updateCategory/{id}")
    public String updateCategory(@PathVariable("id") int id, @ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {

        category.setId(id);
        Category prevCategory = categoryService.getCategoryById(category.getId());
        String imageName = file.isEmpty() ? prevCategory.getImageName() : file.getOriginalFilename();
        if (!ObjectUtils.isEmpty(category)) {
            prevCategory.setName(category.getName());
            prevCategory.setIsActive(category.getIsActive());
            prevCategory.setImageName(imageName);
        }

        Category updatedCategory = categoryService.saveCategory(prevCategory);
        if (!ObjectUtils.isEmpty(updatedCategory)) {
            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator + imageName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("succMsg", "Category updated successfully");
        } else {
            session.setAttribute("errorMsg", "Category not updated! Internal server error");
        }
        return "redirect:/admin/loadEditCategory/" + category.getId();
    }

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image, HttpSession session) throws IOException {

        String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
        product.setImage(imageName);
        product.setDiscount(0);
        product.setDiscountedPrice(product.getPrice());

        Product saveProduct = productService.saveProduct(product);
        if (!ObjectUtils.isEmpty(saveProduct)) {
            File saveFile = new ClassPathResource("static/img").getFile();
            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator + imageName);

            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            session.setAttribute("succMsg", "Product saved successfully");
        } else {
            session.setAttribute("errorMsg", "Product not saved! Internal Server error");
        }
        return "redirect:/admin/loadAddProduct";
    }

    @GetMapping("/products")
    public String loadViewProduct(Model m, @RequestParam(defaultValue = "") String keyword) {
        List<Product> products = null;
        if (StringUtils.hasText(keyword)) {
            products = productService.searchProductAdmin(keyword);
        } else {
            products = productService.getAllProducts();
        }
        m.addAttribute("products", products);
        return "admin/products";
    }

    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable int id, HttpSession session) {
        Boolean deleteProduct = productService.deleteProduct(id);
        if (deleteProduct) {
            session.setAttribute("succMsg", "Product successfully deleted");
        } else {
            session.setAttribute("errorMsg", "Product not deleted! Internal server error");
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/editProduct/{id}")
    public String editProduct(@PathVariable int id, Model m) {
        m.addAttribute("product", productService.getProductById(id));
        m.addAttribute("categories", categoryService.getAllCategory());
        return "admin/edit_product";
    }

    @PostMapping("/updateProduct/{id}")
    public String updateProduct(@PathVariable("id") int id, @ModelAttribute Product product, @RequestParam("file") MultipartFile image, HttpSession session) {
        product.setId(id);
        if (product.getDiscount() < 0 || product.getDiscount() > 100) {
            session.setAttribute("errorMsg", "invalid Discount!");
        } else {
            Product updateProduct = productService.updateProduct(product, image);
            if (!ObjectUtils.isEmpty(updateProduct)) {
                session.setAttribute("succMsg", "Product successfully updated");
            } else {
                session.setAttribute("errorMsg", "Product not updated! Internal server error");
            }
        }
        return "redirect:/admin/editProduct/" + product.getId();
    }

    @GetMapping("/users")
    public String getAllUsers(Model m) {
        List<Users> users = userService.getUsers("ROLE_USER");
        m.addAttribute("allUsers", users);
        return "/admin/users";
    }

    @GetMapping("/updateStatus")
    public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id, HttpSession session) {
        Boolean f = userService.updateAccountStatus(id, status);
        if (f) {
            session.setAttribute("succMsg", "Account Status Updated");
        } else {
            session.setAttribute("errorMsg", "Account status not updated! Internal Server Error");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/orders")
    public String getAllOrders(Model m, @RequestParam(defaultValue = "") String orderId, HttpSession session) {

        List<ProductOrder> allOrders = new ArrayList<>();
        if (StringUtils.hasText(orderId)) {
            ProductOrder curOrder = orderService.getOrdersByOrderId(orderId.trim());
            if (ObjectUtils.isEmpty(curOrder)) {
                session.setAttribute("errorMsg", "No such OrderID present !!");
            } else {
                allOrders.add(curOrder);
            }
        } else {
            allOrders = orderService.getAllOrders();
        }
        m.addAttribute("orders", allOrders);
        return "admin/orders";
    }

    @PostMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {

        OrderStatus[] values = OrderStatus.values();
        String status = null;

        for (OrderStatus orderSt : values) {
            if (orderSt.getId().equals(st)) {
                status = orderSt.getName();
            }
        }

        ProductOrder updateOrder = orderService.updateOrderStatus(id, status);
        try {
            commonUtils.sendMailForProductOrder(updateOrder, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!ObjectUtils.isEmpty(updateOrder)) {
            session.setAttribute("succMsg", "Order Status Updated !!");
        } else {
            session.setAttribute("errorMsg", "Status not updated. Internal Server Error !!");
        }
        return "redirect:/admin/orders";
    }

    @GetMapping("/profile")
    public String profile() {
        return "/admin/profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute Users user, @RequestParam MultipartFile img, HttpSession session) {

        Users updateUserProfile = userService.updateUserProfile(user, img);
        if (ObjectUtils.isEmpty(updateUserProfile)) {
            session.setAttribute("errorMsg", "Profile not updated");
        } else {
            session.setAttribute("succMsg", "Profile Updated");
        }
        return "redirect:/admin/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, Principal p, HttpSession session) {

        Users loggedInUserDetails = commonUtils.getLoggedInUserDetails(p);
        boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());

        if (matches) {
            String encodePassword = passwordEncoder.encode(newPassword);
            loggedInUserDetails.setPassword(encodePassword);
            Users updatedUser = userService.updateUser(loggedInUserDetails);
            if (ObjectUtils.isEmpty(updatedUser)) {
                session.setAttribute("errorMsg", "Password not updated !! Error in server");
            } else {
                session.setAttribute("succMsg", "Password Updated sucessfully");
            }
        } else {
            session.setAttribute("errorMsg", "Current Password incorrect");
        }
        return "redirect:/admin/profile";
    }
}
