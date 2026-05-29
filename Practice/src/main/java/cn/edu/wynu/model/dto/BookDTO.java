package cn.edu.wynu.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookDTO {
    private Integer id;

    private String isbn;

    @NotBlank(message = "书名不能为空")
    private String bookName;

    private String author;

    private String publisher;

    private LocalDate publishDate;

    private Integer categoryId;

    private String coverUrl;

    private String description;

    private Integer totalCount;

    private Integer availableCount;

    private BigDecimal price;

    private String location;

    private Integer status;
}
