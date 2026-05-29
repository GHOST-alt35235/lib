package cn.edu.wynu.mapper;

import cn.edu.wynu.model.entity.Book;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface BookMapper {

    @Select("SELECT b.*, c.category_name FROM book b " +
            "LEFT JOIN category c ON b.category_id = c.id " +
            "WHERE b.status = 1 ORDER BY b.create_time DESC")
    List<Book> selectAll();

    @Select("<script>" +
            "SELECT b.*, c.category_name FROM book b " +
            "LEFT JOIN category c ON b.category_id = c.id " +
            "WHERE 1=1 " +
            "<if test='bookName != null and bookName != \"\"'> AND b.book_name LIKE CONCAT('%', #{bookName}, '%') </if>" +
            "<if test='author != null and author != \"\"'> AND b.author LIKE CONCAT('%', #{author}, '%') </if>" +
            "<if test='categoryId != null'> AND b.category_id = #{categoryId} </if>" +
            "<if test='status != null'> AND b.status = #{status} </if>" +
            "ORDER BY b.create_time DESC" +
            "</script>")
    List<Book> selectByCondition(Book book);

    @Select("SELECT b.*, c.category_name FROM book b " +
            "LEFT JOIN category c ON b.category_id = c.id " +
            "WHERE b.id = #{id}")
    Book selectById(Integer id);

    @Select("SELECT * FROM book WHERE isbn = #{isbn}")
    Book selectByIsbn(String isbn);

    @Insert("INSERT INTO book (isbn, book_name, author, publisher, publish_date, category_id, " +
            "cover_url, description, total_count, available_count, price, location, status) " +
            "VALUES (#{isbn}, #{bookName}, #{author}, #{publisher}, #{publishDate}, #{categoryId}, " +
            "#{coverUrl}, #{description}, #{totalCount}, #{availableCount}, #{price}, #{location}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Book book);

    @Update("UPDATE book SET isbn=#{isbn}, book_name=#{bookName}, author=#{author}, " +
            "publisher=#{publisher}, publish_date=#{publishDate}, category_id=#{categoryId}, " +
            "cover_url=#{coverUrl}, description=#{description}, total_count=#{totalCount}, " +
            "available_count=#{availableCount}, price=#{price}, location=#{location}, status=#{status} " +
            "WHERE id=#{id}")
    int update(Book book);

    @Update("UPDATE book SET available_count = available_count + #{delta} WHERE id = #{id}")
    int updateAvailableCount(@Param("id") Integer id, @Param("delta") Integer delta);

    @Delete("DELETE FROM book WHERE id = #{id}")
    int deleteById(Integer id);

    @Select("SELECT COUNT(*) FROM book WHERE status = 1")
    long count();
}
