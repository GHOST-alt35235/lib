package cn.edu.wynu.model.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DistributeRecord implements Serializable {
    private Integer id;
    private Integer bookId;
    private Integer userId;
    private Integer operatorId;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private String status;
    private String remark;
    private Integer renewCount;
    private BigDecimal fine;
    private Integer overdueDays;
    private LocalDateTime createTime;

    private String bookName;
    private String username;
    private String operatorName;
}
