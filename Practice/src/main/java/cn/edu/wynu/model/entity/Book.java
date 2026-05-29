package cn.edu.wynu.model.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Book implements Serializable {
    private Integer id;
    private String isbn;
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
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private String categoryName;
}
