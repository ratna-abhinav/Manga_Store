package com.example.shoppingdotcom.controller;

import com.example.shoppingdotcom.model.CartItem;
import com.example.shoppingdotcom.model.Category;
import com.example.shoppingdotcom.model.Product;
import com.example.shoppingdotcom.model.Users;
import com.example.shoppingdotcom.service.CartService;
import com.example.shoppingdotcom.service.CategoryService;
import com.example.shoppingdotcom.service.ProductService;
import com.example.shoppingdotcom.service.UserService;
import com.example.shoppingdotcom.util.CommonUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private CartService cartService;

    @ModelAttribute
    public void getUserDetails(Principal p, Model m) {
        if (p != null) {
            String email = p.getName();
            Users currentUser = userService.getUserByEmail(email);
            m.addAttribute("users", currentUser);
            Integer cartQuantity = cartService.getCountCart(currentUser.getId());
            m.addAttribute("countCart", cartQuantity);
        }
        List<Category> activeCategories = categoryService.getAllActiveCategory();
        m.addAttribute("activeCategoriesSection", activeCategories);
    }

    @GetMapping("/")
    public String index(Model m) {
        return "index";
    }

    @GetMapping("/signin")
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

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute Users user, @RequestParam("img") MultipartFile file, HttpSession session) throws IOException {

        String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        user.setProfileImage(imageName);
        Users saveUser = userService.saveUser(user);

        if (!ObjectUtils.isEmpty(saveUser)) {
            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator + imageName);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("succMsg", "User registered successfully");
        } else {
            session.setAttribute("errorMsg", "User cannot be saved! Internal Server error");
        }
        return "redirect:/register";
    }

    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "forgot_password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("username") String email, HttpSession session, HttpServletRequest request)
            throws UnsupportedEncodingException, MessagingException {

        Users userByEmail = userService.getUserByEmail(email);
        if (ObjectUtils.isEmpty(userByEmail)) {
            session.setAttribute("errorMsg", "Invalid email !!");
        } else {
            String resetToken = UUID.randomUUID().toString();
            userService.updateUserResetToken(email, resetToken);

            String url = CommonUtils.generateUrl(request) + "/reset-password?token=" + resetToken;
            Boolean sendMail = commonUtils.sendMail(url, email);
            if (sendMail) {
                session.setAttribute("succMsg", "Please check your email. Password reset link has been sent !!");
            } else {
                session.setAttribute("errorMsg", "Email not sent ! Internal Server error");
            }
        }
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPassword(@RequestParam String token, HttpSession session, Model m) {

        Users userByToken = userService.getUserByToken(token);
        if (ObjectUtils.isEmpty(userByToken)) {
            m.addAttribute("msg", "Your link is invalid or expired !!");
            return "message";
        }
        m.addAttribute("token", token);
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String password, HttpSession session, Model m) {

        Users userByToken = userService.getUserByToken(token);
        if (ObjectUtils.isEmpty(userByToken)) {
            m.addAttribute("msg", "Your link is invalid or expired !!");
        } else {
            userByToken.setPassword(passwordEncoder.encode(password));
            userByToken.setResetToken(null);
            userService.updateUser(userByToken);
            m.addAttribute("msg","Password changed successfully !!");
        }
        return "message";
    }

    @GetMapping("/search")
    public String searchProduct(@RequestParam String keyword, Model m) {
        List<Product> searchProducts = productService.searchProduct(keyword);
        m.addAttribute("products", searchProducts);
        List<Category> categories = categoryService.getAllActiveCategory();
        m.addAttribute("categories", categories);
        return "product";

    }
}
