package cn.edu.wynu.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 图书批量导入DTO
 */
@Data
public class BookImportDTO implements Serializable {
    @NotBlank(message = "ISBN不能为空")
    private String isbn;

    @NotBlank(message = "书名不能为空")
    private String bookName;

    @NotBlank(message = "作者不能为空")
    private String author;

    private String publisher;
    private String publishDate;
    private Integer categoryId;
    private String description;
    private Integer totalCount = 1;
    private BigDecimal price;
    private String location;
}
