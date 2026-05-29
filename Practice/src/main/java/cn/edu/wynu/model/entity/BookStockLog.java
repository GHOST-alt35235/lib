package cn.edu.wynu.model.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 图书库存变更日志实体
 */
@Data
public class BookStockLog implements Serializable {
    private Integer id;
    private Integer bookId;
    private Integer operatorId;
    private Integer changeType; // 1-入库 2-出库 3-损坏 4-丢失
    private Integer changeCount;
    private Integer beforeCount;
    private Integer afterCount;
    private String remark;
    private LocalDateTime createTime;

    private String bookName;
    private String operatorName;
}
