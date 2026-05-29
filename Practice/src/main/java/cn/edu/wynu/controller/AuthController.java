package cn.edu.wynu.controller;

import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.model.dto.LoginDTO;
import cn.edu.wynu.model.dto.RegisterDTO;
import cn.edu.wynu.model.dto.UserDTO;
import cn.edu.wynu.model.entity.Role;
import cn.edu.wynu.service.UserService;
import cn.edu.wynu.service.RoleService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @PostMapping("/login")
    public AjaxResult login(@Valid @RequestBody LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    @PostMapping("/register")
    public AjaxResult register(@Valid @RequestBody RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }

    @GetMapping("/user-info")
    @SaCheckLogin
    public AjaxResult getUserInfo() {
        return userService.getUserInfo();
    }

    @PostMapping("/logout")
    @SaCheckLogin
    public AjaxResult logout() {
        return userService.logout();
    }

    @GetMapping("/role/list")
    @SaCheckLogin
    public AjaxResult getRoleList() {
        return roleService.getRoleList();
    }
}
