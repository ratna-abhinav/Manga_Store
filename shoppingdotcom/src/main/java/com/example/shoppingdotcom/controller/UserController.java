package com.example.shoppingdotcom.controller;

import com.example.shoppingdotcom.model.*;
import com.example.shoppingdotcom.service.*;
import com.example.shoppingdotcom.util.CommonUtils;
import com.example.shoppingdotcom.util.OrderStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CommonUtils commonUtils;

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
    public String home() {
        return "user/home";
    }

    @GetMapping("/addCart")
    public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {
        try {
            CartItem cartQuantity = cartService.getCurrentQuantity(pid, uid);
            Product curProduct = productService.getProductById(pid);
            if (!ObjectUtils.isEmpty(cartQuantity) && cartQuantity.getQuantity() == curProduct.getStock()) {
                session.setAttribute("errorMsg", "Cannot add more !!");
                return "redirect:/product/" + pid;
            }

            CartItem saveCartItem = cartService.saveCart(pid, uid);
            if (ObjectUtils.isEmpty(saveCartItem)) {
                session.setAttribute("errorMsg", "Failed to add the product !! Internal Server Error");
            } else {
                session.setAttribute("succMsg", "Added to cart !!");
            }
        } catch (Exception e) {
            session.setAttribute("errorMsg", "Cannot be added !!");
        }
        return "redirect:/product/" + pid;
    }

    @GetMapping("/cart")
    public String loadCartPage(Principal p, Model m) {
        Users user = getLoggedInUserDetails(p);
        List<CartItem> cartItems = cartService.getCartsByUser(user.getId());
        m.addAttribute("cartContents", cartItems);

        if (!cartItems.isEmpty()) {
            Double totalOrderPrice = cartItems.get(cartItems.size() - 1).getTotalOrderPrice();
            m.addAttribute("totalOrderPrice", totalOrderPrice);
            return "/user/cart";
        } else {
            m.addAttribute("msg", "Cart is currently empty !!");
            return "message";
        }
    }

    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid, HttpSession session) {
        Boolean updStatus = cartService.updateQuantity(sy, cid);
        if (!updStatus) {
            session.setAttribute("errorMsg", "Cannot add more !!");
        }
        return "redirect:/users/cart";
    }

    private Users getLoggedInUserDetails(Principal p) {
        String email = p.getName();
        return userService.getUserByEmail(email);
    }

    @GetMapping("/orders")
    public String orderPage(Principal p, Model m) {
        Users user = getLoggedInUserDetails(p);
        List<CartItem> carts = cartService.getCartsByUser(user.getId());
        if (!carts.isEmpty()) {
            Double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            Double totalOrderPrice = orderPrice + 100 + 50;
            m.addAttribute("orderPrice", orderPrice);
            m.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "/user/order";
    }

    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute OrderRequestDTO request, Principal p) throws Exception {
        Users user = getLoggedInUserDetails(p);
        orderService.saveOrder(user.getId(), request);
        cartService.resetCart(user.getId());
        return "redirect:/users/success";
    }

    @GetMapping("/success")
    public String loadSuccess() {
        return "/user/success";
    }

    @GetMapping("/user-orders")
    public String myOrder(Model m, Principal p) {
        Users loggedInUser = getLoggedInUserDetails(p);
        List<ProductOrder> orders = orderService.getOrdersByUser(loggedInUser.getId());
        if (ObjectUtils.isEmpty(orders)) {
            m.addAttribute("msg", "No Previous Orders !!");
            return "message";
        }
        m.addAttribute("orders", orders);

        double totalOrderPrice = 0.0;
        for (ProductOrder curProduct : orders) {
            if (Objects.equals(curProduct.getStatus(), OrderStatus.CANCELLED.getName())) continue;
            totalOrderPrice += curProduct.getPrice() * curProduct.getQuantity();
        }
        totalOrderPrice += 100 + 50;
        m.addAttribute("totalOrderPrice", totalOrderPrice);
        return "/user/my_orders";
    }

    @GetMapping("/update-status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session, Principal p, Model m) {

        OrderStatus[] values = OrderStatus.values();
        String status = null;
        for (OrderStatus curStatus : values) {
            if (curStatus.getId().equals(st)) {
                status = curStatus.getName();
            }
        }
        ProductOrder updatedOrder = orderService.updateOrderStatus(id, status);
        try {
            commonUtils.sendMailForProductOrder(updatedOrder, status);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!ObjectUtils.isEmpty(updatedOrder)) {
            session.setAttribute("succMsg", "Order Status Updated !!");
        } else {
            session.setAttribute("errorMsg", "Status not updated. Internal Server Error !!");
        }
        return "redirect:/users/user-orders";
    }

}
