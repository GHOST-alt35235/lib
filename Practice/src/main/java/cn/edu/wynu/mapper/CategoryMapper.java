package cn.edu.wynu.mapper;

import cn.edu.wynu.model.entity.Category;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CategoryMapper {

    @Select("SELECT * FROM category WHERE status = 1 ORDER BY sort_order")
    List<Category> selectAll();

    @Select("SELECT * FROM category WHERE parent_id = #{parentId} AND status = 1 ORDER BY sort_order")
    List<Category> selectByParentId(Integer parentId);

    @Select("SELECT * FROM category WHERE id = #{id}")
    Category selectById(Integer id);

    @Insert("INSERT INTO category (category_name, parent_id, sort_order, description, status) " +
            "VALUES (#{categoryName}, #{parentId}, #{sortOrder}, #{description}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Category category);

    @Update("UPDATE category SET category_name=#{categoryName}, parent_id=#{parentId}, " +
            "sort_order=#{sortOrder}, description=#{description}, status=#{status} WHERE id=#{id}")
    int update(Category category);

    @Delete("DELETE FROM category WHERE id = #{id}")
    int deleteById(Integer id);

    @Select("SELECT c.category_name, COUNT(b.id) as book_count " +
            "FROM category c LEFT JOIN book b ON c.id = b.category_id " +
            "WHERE c.status = 1 AND c.parent_id = 0 " +
            "GROUP BY c.id, c.category_name ORDER BY book_count DESC")
    List<java.util.Map<String, Object>> getCategoryBookStats();
}
