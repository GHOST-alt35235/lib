package cn.edu.wynu.mapper;

import cn.edu.wynu.model.entity.BookStockLog;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface BookStockLogMapper {

    @Insert("INSERT INTO book_stock_log (book_id, operator_id, change_type, change_count, before_count, after_count, remark) " +
            "VALUES (#{bookId}, #{operatorId}, #{changeType}, #{changeCount}, #{beforeCount}, #{afterCount}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BookStockLog log);

    @Select("SELECT bsl.*, b.book_name, u.username as operatorName FROM book_stock_log bsl " +
            "LEFT JOIN book b ON bsl.book_id = b.id " +
            "LEFT JOIN user u ON bsl.operator_id = u.id " +
            "WHERE bsl.book_id = #{bookId} ORDER BY bsl.create_time DESC")
    List<BookStockLog> selectByBookId(Integer bookId);

    @Select("<script>" +
            "SELECT bsl.*, b.book_name, u.username as operatorName FROM book_stock_log bsl " +
            "LEFT JOIN book b ON bsl.book_id = b.id " +
            "LEFT JOIN user u ON bsl.operator_id = u.id " +
            "WHERE 1=1 " +
            "<if test='bookId != null'> AND bsl.book_id = #{bookId} </if>" +
            "<if test='changeType != null'> AND bsl.change_type = #{changeType} </if>" +
            "ORDER BY bsl.create_time DESC" +
            "</script>")
    List<BookStockLog> selectByCondition(BookStockLog log);
}
