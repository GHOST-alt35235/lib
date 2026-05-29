package cn.edu.wynu.mapper;

import cn.edu.wynu.model.entity.OperationLog;
import org.apache.ibatis.annotations.Insert;

public interface OperationLogMapper {

    @Insert("INSERT INTO operation_log (user_id, username, operation, method, url, ip, params, result, status, error_msg, create_time) " +
            "VALUES (#{userId}, #{username}, #{operation}, #{method}, #{url}, #{ip}, #{params}, #{result}, #{status}, #{errorMsg}, #{createTime})")
    int insert(OperationLog log);
}
