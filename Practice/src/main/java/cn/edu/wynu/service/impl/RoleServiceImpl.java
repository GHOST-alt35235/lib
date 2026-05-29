package cn.edu.wynu.service.impl;

import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.common.HttpStatus;
import cn.edu.wynu.mapper.RoleMapper;
import cn.edu.wynu.model.entity.Role;
import cn.edu.wynu.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public AjaxResult getRoleList() {
        List<Role> roles = roleMapper.selectAll();
        return AjaxResult.success("查询成功", roles);
    }

    @Override
    public AjaxResult getRoleById(Integer id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            return AjaxResult.error("角色不存在！");
        }
        return AjaxResult.success("查询成功", role);
    }

    @Override
    public AjaxResult addRole(Role role) {
        if (role.getRoleName() == null || role.getRoleCode() == null) {
            return AjaxResult.error("角色名称和角色编码不能为空！");
        }

        Role existRole = roleMapper.selectByCode(role.getRoleCode());
        if (existRole != null) {
            return AjaxResult.error("角色编码已存在！");
        }

        role.setStatus(1);
        int result = roleMapper.insert(role);
        if (result > 0) {
            return new AjaxResult(HttpStatus.CREATED, "添加成功", role.getId());
        }
        return AjaxResult.error("添加失败！");
    }

    @Override
    public AjaxResult updateRole(Role role) {
        if (role.getId() == null) {
            return AjaxResult.error("角色ID不能为空！");
        }

        Role existRole = roleMapper.selectById(role.getId());
        if (existRole == null) {
            return AjaxResult.error("角色不存在！");
        }

        int result = roleMapper.update(role);
        if (result > 0) {
            return AjaxResult.success("更新成功");
        }
        return AjaxResult.error("更新失败！");
    }

    @Override
    public AjaxResult deleteRole(Integer id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            return AjaxResult.error("角色不存在！");
        }

        if ("SUPER_ADMIN".equals(role.getRoleCode()) || "USER".equals(role.getRoleCode())) {
            return AjaxResult.error("系统内置角色不能删除！");
        }

        int result = roleMapper.deleteById(id);
        if (result > 0) {
            return AjaxResult.success("删除成功");
        }
        return AjaxResult.error("删除失败！");
    }
}
