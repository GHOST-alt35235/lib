package cn.edu.wynu.service;

import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.model.entity.Role;
import java.util.List;

public interface RoleService {
    AjaxResult getRoleList();
    AjaxResult getRoleById(Integer id);
    AjaxResult addRole(Role role);
    AjaxResult updateRole(Role role);
    AjaxResult deleteRole(Integer id);
}
