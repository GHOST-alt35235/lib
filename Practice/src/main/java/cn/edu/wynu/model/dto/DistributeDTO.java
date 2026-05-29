package cn.edu.wynu.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Data
public class DistributeDTO {
    private Integer id;

    @NotNull(message = "图书ID不能为空")
    private Integer bookId;

    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueDate;

    private String remark;
}
