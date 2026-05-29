package cn.edu.wynu.model.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class User implements Serializable {
    private Integer id;
    private String username;
    private String password;
    private String realName;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
    private Integer borrowLimit;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private List<Role> roles;
}
