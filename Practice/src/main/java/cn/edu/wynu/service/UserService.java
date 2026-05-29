package cn.edu.wynu.service;

import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.model.dto.LoginDTO;
import cn.edu.wynu.model.dto.RegisterDTO;
import cn.edu.wynu.model.dto.UserDTO;

import java.util.List;

public interface UserService {
    AjaxResult login(LoginDTO loginDTO);
    AjaxResult register(RegisterDTO registerDTO);
    AjaxResult getUserInfo();
    AjaxResult logout();

    AjaxResult getUserList();
    AjaxResult getUserById(Integer id);
    AjaxResult addUser(UserDTO userDTO);
    AjaxResult updateUser(UserDTO userDTO);
    AjaxResult deleteUser(Integer id);
    AjaxResult assignRoles(Integer userId, Integer[] roleIds);
    
    AjaxResult toggleStatus(Integer userId, Integer status);
    AjaxResult resetPassword(Integer userId, String newPassword);
    AjaxResult setBorrowLimit(Integer userId, Integer limit);
}
