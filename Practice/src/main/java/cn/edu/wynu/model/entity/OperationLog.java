package cn.edu.wynu.model.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OperationLog implements Serializable {
    private Integer id;
    private Integer userId;
    private String username;
    private String operation;
    private String method;
    private String url;
    private String ip;
    private String params;
    private String result;
    private Integer status;
    private String errorMsg;
    private LocalDateTime createTime;
}
