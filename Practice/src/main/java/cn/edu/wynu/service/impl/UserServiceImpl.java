package cn.edu.wynu.service.impl;

import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.common.HttpStatus;
import cn.edu.wynu.mapper.UserMapper;
import cn.edu.wynu.mapper.RoleMapper;
import cn.edu.wynu.model.dto.LoginDTO;
import cn.edu.wynu.model.dto.RegisterDTO;
import cn.edu.wynu.model.dto.UserDTO;
import cn.edu.wynu.model.entity.User;
import cn.edu.wynu.model.entity.Role;
import cn.edu.wynu.service.UserService;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public AjaxResult login(LoginDTO loginDTO) {
        if (loginDTO == null || loginDTO.getUsername() == null || loginDTO.getPassword() == null) {
            return AjaxResult.error("用户名和密码不能为空！");
        }

        User queryUser = userMapper.selectByUsername(loginDTO.getUsername());
        if (queryUser == null) {
            return AjaxResult.error("用户名不存在！");
        }

        if (queryUser.getStatus() == 0) {
            return AjaxResult.error("账号已被禁用！");
        }

        if (!loginDTO.getPassword().equals(queryUser.getPassword())) {
            return AjaxResult.error("密码错误！");
        }

        StpUtil.login(queryUser.getUsername());

        List<Role> roles = userMapper.selectRolesByUserId(queryUser.getId());
        List<String> roleCodes = new ArrayList<>();
        for (Role role : roles) {
            roleCodes.add(role.getRoleCode());
        }
        StpUtil.getSession().set("roles", roleCodes);
        
        queryUser.setRoles(roles);
        queryUser.setPassword(null);
        return AjaxResult.success("登录成功", queryUser);
    }

    @Override
    @Transactional
    public AjaxResult register(RegisterDTO registerDTO) {
        if (registerDTO == null || registerDTO.getUsername() == null || registerDTO.getPassword() == null) {
            return AjaxResult.error("用户名和密码不能为空！");
        }

        User existUser = userMapper.selectByUsername(registerDTO.getUsername());
        if (existUser != null) {
            return AjaxResult.error("用户名已存在！");
        }

        User newUser = new User();
        newUser.setUsername(registerDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        newUser.setRealName(registerDTO.getRealName());
        newUser.setEmail(registerDTO.getEmail());
        newUser.setPhone(registerDTO.getPhone());
        newUser.setStatus(1);

        int result = userMapper.insert(newUser);
        if (result > 0) {
            return new AjaxResult(HttpStatus.CREATED, "注册成功", newUser.getId());
        }
        return AjaxResult.error("注册失败！");
    }

    @Override
    public AjaxResult getUserInfo() {
        if (!StpUtil.isLogin()) {
            return AjaxResult.error("用户还未登录！");
        }

        String username = StpUtil.getLoginIdAsString();
        User user = userMapper.selectByUsername(username);
        if (user != null) {
            List<Role> roles = userMapper.selectRolesByUserId(user.getId());
            user.setRoles(roles);
            user.setPassword(null);
            return AjaxResult.success("查询成功", user);
        }
        return AjaxResult.error("用户信息不存在！");
    }

    @Override
    public AjaxResult logout() {
        StpUtil.logout();
        return AjaxResult.success("退出登录成功！");
    }

    @Override
    public AjaxResult getUserList() {
        List<User> users = userMapper.selectAll();
        for (User user : users) {
            List<Role> roles = userMapper.selectRolesByUserId(user.getId());
            user.setRoles(roles);
            user.setPassword(null);
        }
        return AjaxResult.success("查询成功", users);
    }

    @Override
    public AjaxResult getUserById(Integer id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return AjaxResult.error("用户不存在！");
        }
        List<Role> roles = userMapper.selectRolesByUserId(user.getId());
        user.setRoles(roles);
        user.setPassword(null);
        return AjaxResult.success("查询成功", user);
    }

    @Override
    @Transactional
    public AjaxResult addUser(UserDTO userDTO) {
        if (userDTO.getUsername() == null || userDTO.getPassword() == null) {
            return AjaxResult.error("用户名和密码不能为空！");
        }

        User existUser = userMapper.selectByUsername(userDTO.getUsername());
        if (existUser != null) {
            return AjaxResult.error("用户名已存在！");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRealName(userDTO.getRealName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setAvatar(userDTO.getAvatar());
        user.setStatus(userDTO.getStatus() != null ? userDTO.getStatus() : 1);

        int result = userMapper.insert(user);
        if (result > 0) {
            if (userDTO.getRoleIds() != null && userDTO.getRoleIds().length > 0) {
                List<Integer> roleIdList = new ArrayList<>();
                for (Integer roleId : userDTO.getRoleIds()) {
                    roleIdList.add(roleId);
                }
                userMapper.insertUserRoles(user.getId(), roleIdList);
            }
            return new AjaxResult(HttpStatus.CREATED, "添加成功", user.getId());
        }
        return AjaxResult.error("添加失败！");
    }

    @Override
    @Transactional
    public AjaxResult updateUser(UserDTO userDTO) {
        if (userDTO.getId() == null) {
            return AjaxResult.error("用户ID不能为空！");
        }

        User existUser = userMapper.selectById(userDTO.getId());
        if (existUser == null) {
            return AjaxResult.error("用户不存在！");
        }

        if (userMapper.countByUsername(userDTO.getUsername(), userDTO.getId()) > 0) {
            return AjaxResult.error("用户名已存在！");
        }

        User user = new User();
        user.setId(userDTO.getId());
        user.setUsername(userDTO.getUsername());
        user.setRealName(userDTO.getRealName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setAvatar(userDTO.getAvatar());
        user.setStatus(userDTO.getStatus() != null ? userDTO.getStatus() : 1);

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        int result = userMapper.update(user);
        if (result > 0) {
            if (userDTO.getRoleIds() != null) {
                userMapper.deleteUserRoles(userDTO.getId());
                List<Integer> roleIdList = new ArrayList<>();
                for (Integer roleId : userDTO.getRoleIds()) {
                    roleIdList.add(roleId);
                }
                if (!roleIdList.isEmpty()) {
                    userMapper.insertUserRoles(userDTO.getId(), roleIdList);
                }
            }
            return AjaxResult.success("更新成功");
        }
        return AjaxResult.error("更新失败！");
    }

    @Override
    @Transactional
    public AjaxResult deleteUser(Integer id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return AjaxResult.error("用户不存在！");
        }

        if ("admin".equals(user.getUsername())) {
            return AjaxResult.error("不能删除管理员账号！");
        }

        userMapper.deleteUserRoles(id);
        int result = userMapper.deleteById(id);
        if (result > 0) {
            return AjaxResult.success("删除成功");
        }
        return AjaxResult.error("删除失败！");
    }

    @Override
    @Transactional
    public AjaxResult assignRoles(Integer userId, Integer[] roleIds) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return AjaxResult.error("用户不存在！");
        }

        userMapper.deleteUserRoles(userId);
        if (roleIds != null && roleIds.length > 0) {
            List<Integer> roleIdList = new ArrayList<>();
            for (Integer roleId : roleIds) {
                roleIdList.add(roleId);
            }
            userMapper.insertUserRoles(userId, roleIdList);
        }
        return AjaxResult.success("分配角色成功");
    }

    @Override
    @Transactional
    public AjaxResult toggleStatus(Integer userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return AjaxResult.error("用户不存在！");
        }

        if ("admin".equals(user.getUsername())) {
            return AjaxResult.error("不能禁用管理员账号！");
        }

        user.setStatus(status);
        int result = userMapper.update(user);
        if (result > 0) {
            String action = status == 1 ? "启用" : "禁用";
            return AjaxResult.success("账号" + action + "成功");
        }
        return AjaxResult.error("操作失败！");
    }

    @Override
    @Transactional
    public AjaxResult resetPassword(Integer userId, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return AjaxResult.error("用户不存在！");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        int result = userMapper.update(user);
        if (result > 0) {
            return AjaxResult.success("密码重置成功");
        }
        return AjaxResult.error("密码重置失败！");
    }

    @Override
    @Transactional
    public AjaxResult setBorrowLimit(Integer userId, Integer limit) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return AjaxResult.error("用户不存在！");
        }

        if (limit != null && limit < 0) {
            return AjaxResult.error("借阅数量不能为负数！");
        }

        user.setBorrowLimit(limit);
        int result = userMapper.update(user);
        if (result > 0) {
            return AjaxResult.success("借阅限制设置成功");
        }
        return AjaxResult.error("设置失败！");
    }
}
