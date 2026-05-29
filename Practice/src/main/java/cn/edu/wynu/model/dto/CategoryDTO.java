package cn.edu.wynu.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class CategoryDTO {
    private Integer id;

    @NotBlank(message = "分类名称不能为空")
    private String categoryName;

    private Integer parentId;

    private Integer sortOrder;

    private String description;

    private Integer status;
}
