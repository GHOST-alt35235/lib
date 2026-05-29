package cn.edu.wynu.mapper;

import cn.edu.wynu.model.entity.DistributeRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface DistributeRecordMapper {

    @Select("SELECT dr.id, dr.book_id as bookId, dr.user_id as userId, dr.operator_id as operatorId, " +
            "dr.borrow_date as borrowDate, dr.due_date as dueDate, dr.return_date as returnDate, " +
            "dr.status, dr.remark, dr.renew_count as renewCount, dr.create_time as createTime, " +
            "b.book_name as bookName, u.username, o.username as operatorName " +
            "FROM distribute_record dr " +
            "LEFT JOIN book b ON dr.book_id = b.id " +
            "LEFT JOIN user u ON dr.user_id = u.id " +
            "LEFT JOIN user o ON dr.operator_id = o.id " +
            "ORDER BY dr.create_time DESC")
    List<DistributeRecord> selectAll();

    @Select("<script>" +
            "SELECT dr.id, dr.book_id as bookId, dr.user_id as userId, dr.operator_id as operatorId, " +
            "dr.borrow_date as borrowDate, dr.due_date as dueDate, dr.return_date as returnDate, " +
            "dr.status, dr.remark, dr.renew_count as renewCount, dr.create_time as createTime, " +
            "b.book_name as bookName, u.username, o.username as operatorName " +
            "FROM distribute_record dr " +
            "LEFT JOIN book b ON dr.book_id = b.id " +
            "LEFT JOIN user u ON dr.user_id = u.id " +
            "LEFT JOIN user o ON dr.operator_id = o.id " +
            "WHERE 1=1 " +
            "<if test='userId != null'> AND dr.user_id = #{userId} </if>" +
            "<if test='bookId != null'> AND dr.book_id = #{bookId} </if>" +
            "<if test='status != null and status != \"\"'> AND dr.status = #{status} </if>" +
            "ORDER BY dr.create_time DESC" +
            "</script>")
    List<DistributeRecord> selectByCondition(DistributeRecord record);

    @Select("SELECT dr.id, dr.book_id as bookId, dr.user_id as userId, dr.operator_id as operatorId, " +
            "dr.borrow_date as borrowDate, dr.due_date as dueDate, dr.return_date as returnDate, " +
            "dr.status, dr.remark, dr.renew_count as renewCount, dr.create_time as createTime, " +
            "b.book_name as bookName, u.username, o.username as operatorName " +
            "FROM distribute_record dr " +
            "LEFT JOIN book b ON dr.book_id = b.id " +
            "LEFT JOIN user u ON dr.user_id = u.id " +
            "LEFT JOIN user o ON dr.operator_id = o.id " +
            "WHERE dr.id = #{id}")
    DistributeRecord selectById(Integer id);

    @Insert("INSERT INTO distribute_record (book_id, user_id, operator_id, due_date, status, remark) " +
            "VALUES (#{bookId}, #{userId}, #{operatorId}, #{dueDate}, #{status}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DistributeRecord record);

    @Update("UPDATE distribute_record SET return_date=#{returnDate}, status=#{status} WHERE id=#{id}")
    int update(DistributeRecord record);

    @Update("UPDATE distribute_record SET status=#{status}, remark=#{remark} WHERE id=#{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") String status, @Param("remark") String remark);

    @Update("<script>" +
            "UPDATE distribute_record SET " +
            "<if test='bookId != null'> book_id=#{bookId}, </if>" +
            "<if test='userId != null'> user_id=#{userId}, </if>" +
            "<if test='operatorId != null'> operator_id=#{operatorId}, </if>" +
            "<if test='dueDate != null'> due_date=#{dueDate}, </if>" +
            "<if test='remark != null'> remark=#{remark}, </if>" +
            "<if test='status != null'> status=#{status}, </if>" +
            "<if test='returnDate != null'> return_date=#{returnDate}, </if>" +
            "<if test='renewCount != null'> renew_count=#{renewCount} </if>" +
            "WHERE id=#{id}" +
            "</script>")
    int updateRecord(DistributeRecord record);

    @Delete("DELETE FROM distribute_record WHERE id = #{id}")
    int deleteById(Integer id);

    @Select("SELECT COUNT(*) FROM distribute_record WHERE book_id = #{bookId} AND status = 'BORROWING'")
    int countBorrowingByBookId(Integer bookId);

    @Select("SELECT COUNT(*) FROM distribute_record WHERE status = 'BORROWING'")
    long countBorrowing();

    @Select("SELECT COUNT(*) FROM distribute_record WHERE status = #{status}")
    int countByStatus(String status);

    @Select("SELECT MONTH(create_time) as month, COUNT(*) as count " +
            "FROM distribute_record WHERE YEAR(create_time) = YEAR(NOW()) " +
            "GROUP BY MONTH(create_time) ORDER BY month")
    List<java.util.Map<String, Object>> getMonthlyBorrowData();

    default int[] getMonthlyBorrowCounts() {
        int[] counts = new int[12];
        List<java.util.Map<String, Object>> data = getMonthlyBorrowData();
        for (java.util.Map<String, Object> item : data) {
            int month = (int) item.get("month");
            int count = ((Number) item.get("count")).intValue();
            counts[month - 1] = count;
        }
        return counts;
    }

    @Select("SELECT b.book_name, b.author, COUNT(dr.id) as borrow_count " +
            "FROM distribute_record dr " +
            "LEFT JOIN book b ON dr.book_id = b.id " +
            "GROUP BY dr.book_id, b.book_name, b.author " +
            "ORDER BY borrow_count DESC LIMIT #{limit}")
    List<java.util.Map<String, Object>> getTopBorrowedBooks(int limit);
}
