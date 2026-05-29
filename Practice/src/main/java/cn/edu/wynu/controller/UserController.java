package cn.edu.wynu.controller;

import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.model.dto.UserDTO;
import cn.edu.wynu.model.entity.Role;
import cn.edu.wynu.service.UserService;
import cn.edu.wynu.service.RoleService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@SaCheckLogin
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    @SaCheckRole("SUPER_ADMIN")
    public AjaxResult getUserList() {
        return userService.getUserList();
    }

    @GetMapping("/{id}")
    @SaCheckRole("SUPER_ADMIN")
    public AjaxResult getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PostMapping("/add")
    @SaCheckRole("SUPER_ADMIN")
    public AjaxResult addUser(@Valid @RequestBody UserDTO userDTO) {
        return userService.addUser(userDTO);
    }

    @PutMapping("/update")
    @SaCheckRole("SUPER_ADMIN")
    public AjaxResult updateUser(@Valid @RequestBody UserDTO userDTO) {
        return userService.updateUser(userDTO);
    }

    @DeleteMapping("/{id}")
    @SaCheckRole("SUPER_ADMIN")
    public AjaxResult deleteUser(@PathVariable Integer id) {
        return userService.deleteUser(id);
    }

    @PutMapping("/roles/{userId}")
    @SaCheckRole("SUPER_ADMIN")
    public AjaxResult assignRoles(@PathVariable Integer userId, @RequestBody Integer[] roleIds) {
        return userService.assignRoles(userId, roleIds);
    }

    @PutMapping("/status/{userId}")
    @SaCheckRole({"SUPER_ADMIN", "LIBRARIAN"})
    public AjaxResult toggleStatus(@PathVariable Integer userId, @RequestParam Integer status) {
        return userService.toggleStatus(userId, status);
    }

    @PutMapping("/password/{userId}/reset")
    @SaCheckRole({"SUPER_ADMIN", "LIBRARIAN"})
    public AjaxResult resetPassword(@PathVariable Integer userId, @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        if (newPassword == null || newPassword.isEmpty()) {
            return AjaxResult.error("新密码不能为空！");
        }
        return userService.resetPassword(userId, newPassword);
    }

    @PutMapping("/borrow-limit/{userId}")
    @SaCheckRole({"SUPER_ADMIN", "LIBRARIAN"})
    public AjaxResult setBorrowLimit(@PathVariable Integer userId, @RequestParam Integer limit) {
        return userService.setBorrowLimit(userId, limit);
    }

    @GetMapping("/role/list")
    public AjaxResult getRoleList() {
        return roleService.getRoleList();
    }
}
