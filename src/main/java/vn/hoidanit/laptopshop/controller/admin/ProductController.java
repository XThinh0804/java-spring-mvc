package vn.hoidanit.laptopshop.controller.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.service.ProductService;
import vn.hoidanit.laptopshop.service.UploadService;

@Controller
public class ProductController {

    private final UploadService uploadService;
    private final ProductService productService;

    public ProductController(
            UploadService uploadService,
            ProductService productService) {
        this.uploadService = uploadService;
        this.productService = productService;
    }

    @GetMapping("/admin/product")
    public String getProduct(Model model) {
        List<Product> prs = this.productService.fetchProducts();
        model.addAttribute("products", prs);
        return "admin/product/show";
    }

    @GetMapping("/admin/product/create")
    public String getCreateProductPage(Model model) {
        model.addAttribute("newProduct", new Product());
        return "admin/product/create";
    }

    @PostMapping("/admin/product/create")
    public String handleCreateProduct(
            @ModelAttribute("newProduct") @Valid Product pr,
            BindingResult newProductBindingResult,
            @RequestParam("imageFile") MultipartFile file) {
        // validate
        if (newProductBindingResult.hasErrors()) {
            return "admin/product/create";
        }

        // upload image
        String image = this.uploadService.handleSaveUploadFile(file, "product");
        pr.setImage(image);

        this.productService.createProduct(pr);

        return "redirect:/admin/product";
    }

    @GetMapping("/admin/product/{id}")
    public String getViewProduct(Model model, @PathVariable("id") long id) {
        Optional<Product> product = this.productService.getProductById(id);
        model.addAttribute("product", product.get());
        return "/admin/product/detail";
    }

    @GetMapping("/admin/product/update/{id}")
    public String getUpdateProduct(Model model, @PathVariable("id") long id) {
        Optional<Product> product = this.productService.getProductById(id);
        model.addAttribute("product", product.get());
        return "/admin/product/update";
    }

    @PostMapping("/admin/product/update")
    public String postUpdateProduct(Model model, @ModelAttribute("product") @Valid Product product,
            BindingResult newProductBindingResult, @RequestParam("imageFile") MultipartFile file) {
        if (newProductBindingResult.hasErrors()) {
            return "admin/product/update";
        }
        Product currentProduct = this.productService.getProductById(product.getId()).get();
        if (currentProduct != null) {
            // update new image
            if (!file.isEmpty()) {
                String img = this.uploadService.handleSaveUploadFile(file, "product");
                currentProduct.setImage(img);
            }

            currentProduct.setName(product.getName());
            currentProduct.setPrice(product.getPrice());
            currentProduct.setQuantity(product.getQuantity());
            currentProduct.setDetailDesc(product.getDetailDesc());
            currentProduct.setShortDesc(product.getShortDesc());
            currentProduct.setFactory(product.getFactory());
            currentProduct.setTarget(product.getTarget());

            this.productService.createProduct(currentProduct);
        }
        return "redirect:/admin/product";
    }

    @GetMapping("/admin/product/delete/{id}")
    public String getDeleteProduct(Model model, @PathVariable("id") long id) {
        model.addAttribute("id", id);
        model.addAttribute("product", new Product());
        return "/admin/product/delete";
    }

    @PostMapping("/admin/product/delete")
    public String postDeleteProduct(Model model, @ModelAttribute("product") Product product) {
        this.productService.deleteAProduct(product.getId());
        return "redirect:/admin/product";
    }

}
