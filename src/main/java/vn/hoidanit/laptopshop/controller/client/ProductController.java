package vn.hoidanit.laptopshop.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductController {
    @GetMapping("/product/{id}")
    public String getProductDetail() {
        return "/client/product-detail/shop-detail";
    }
}
