package cn.edu.wynu.model.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Category implements Serializable {
    private Integer id;
    private String categoryName;
    private Integer parentId;
    private Integer sortOrder;
    private String description;
    private Integer status;
    private LocalDateTime createTime;

    private List<Category> children;
}
