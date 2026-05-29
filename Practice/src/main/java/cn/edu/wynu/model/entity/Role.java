package cn.edu.wynu.model.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Role implements Serializable {
    private Integer id;
    private String roleName;
    private String roleCode;
    private String description;
    private Integer status;
    private LocalDateTime createTime;
}
