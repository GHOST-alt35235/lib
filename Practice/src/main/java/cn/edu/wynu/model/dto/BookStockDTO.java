package cn.edu.wynu.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;

/**
 * 图书库存调整DTO
 */
@Data
public class BookStockDTO implements Serializable {
    @NotNull(message = "图书ID不能为空")
    private Integer bookId;

    @NotNull(message = "调整数量不能为空")
    private Integer adjustCount;

    private String remark;
}
