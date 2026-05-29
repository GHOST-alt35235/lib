package cn.edu.wynu.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;

/**
 * 续借DTO
 */
@Data
public class RenewDTO implements Serializable {
    @NotNull(message = "借阅记录ID不能为空")
    private Integer recordId;

    private Integer extendDays;
}
