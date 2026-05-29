package cn.edu.wynu.model.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class UserDTO {
    private Integer id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 32, message = "用户名长度3-32位")
    private String username;

    private String password;

    private String realName;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String phone;

    private String avatar;

    private Integer status;

    private Integer[] roleIds;
}
