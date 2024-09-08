package vn.hoidanit.laptopshop.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.UploadService;
import vn.hoidanit.laptopshop.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    // DI: dependency injection
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UploadService uploadService;

    public UserController(UserService userService, UploadService uploadService,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.uploadService = uploadService;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping("/")
    public String getHomePage(Model model) {
        List<User> users = this.userService.getAllUsersByEmail("abc@gmail.com");
        System.out.println(users);
        model.addAttribute("eric", "test");
        return "hello";
    }

    @RequestMapping("/admin/user")
    public String getUserPage(Model model) {
        List<User> users = this.userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/user/show";
    }

    @RequestMapping("/admin/user/{userId}")
    public String getUserDetailPage(Model model, @PathVariable("userId") long userId) {
        User userDetail = this.userService.getUserById(userId);
        model.addAttribute("user", userDetail);
        model.addAttribute("id", userId);
        return "admin/user/detail";
    }

    @RequestMapping(value = "/admin/user/create", method = RequestMethod.GET)
    public String createUserPage(Model model) {
        model.addAttribute("newUser", new User());
        return "admin/user/create";
    }

    @PostMapping(value = "/admin/user/create")
    public String createUserPageSave(Model model, @ModelAttribute("newUser") User newUser,
            @RequestParam("avatarFile") MultipartFile file) {
        String avatar = this.uploadService.handleSaveUploadFile(file, "avatar");
        String hashPassword = this.passwordEncoder.encode(newUser.getPassword());
        newUser.setAvatar(avatar);
        newUser.setPassword(hashPassword);
        newUser.setRole(this.userService.getRoleByName(newUser.getRole().getName()));
        // save
        this.userService.handleSaveUser(newUser);
        return "redirect:/admin/user";
    }

    @RequestMapping(value = "/admin/user/update/{id}", method = RequestMethod.GET)
    public String updateUserPage(Model model, @PathVariable("id") long id) {
        User user = this.userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("id", id);
        return "admin/user/update";
    }

    @PostMapping("/admin/user/update")
    public String updateUserPageSave(Model model, @ModelAttribute("user") User user) {
        // this.userService.handleSaveUser(user);
        User currentUser = this.userService.getUserById(user.getId());
        if (currentUser != null) {
            currentUser.setAddress(user.getAddress());
            currentUser.setFullName(user.getFullName());
            currentUser.setPhone(user.getPhone());
            this.userService.handleSaveUser(currentUser);
        }
        return "redirect:/admin/user";
    }

    @GetMapping("/admin/user/delete/{id}")
    public String getDeleteUserPage(Model model, @PathVariable("id") long id) {
        // User user = new User();
        // user.setId(id);
        model.addAttribute("id", id);
        model.addAttribute("user", new User());
        return "/admin/user/delete";
    }

    @PostMapping("/admin/user/delete")
    public String postDeleteUser(Model model, @ModelAttribute("user") User user) {
        this.userService.deleteAUser(user.getId());
        return "redirect:/admin/user";
    }

}