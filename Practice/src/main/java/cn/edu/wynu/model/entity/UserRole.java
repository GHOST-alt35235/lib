package cn.edu.wynu.model.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserRole implements Serializable {
    private Integer id;
    private Integer userId;
    private Integer roleId;
    private LocalDateTime createTime;
}
