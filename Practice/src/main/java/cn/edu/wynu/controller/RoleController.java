package cn.edu.wynu.controller;

import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.model.entity.Role;
import cn.edu.wynu.service.RoleService;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role")
@SaCheckLogin
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    @SaCheckRole("SUPER_ADMIN")
    public AjaxResult getRoleList() {
        return roleService.getRoleList();
    }

    @GetMapping("/{id}")
    @SaCheckRole("SUPER_ADMIN")
    public AjaxResult getRoleById(@PathVariable Integer id) {
        return roleService.getRoleById(id);
    }

    @PostMapping("/add")
    @SaCheckRole("SUPER_ADMIN")
    public AjaxResult addRole(@RequestBody Role role) {
        return roleService.addRole(role);
    }

    @PutMapping("/update")
    @SaCheckRole("SUPER_ADMIN")
    public AjaxResult updateRole(@RequestBody Role role) {
        return roleService.updateRole(role);
    }

    @DeleteMapping("/{id}")
    @SaCheckRole("SUPER_ADMIN")
    public AjaxResult deleteRole(@PathVariable Integer id) {
        return roleService.deleteRole(id);
    }
}
