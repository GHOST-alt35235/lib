package cn.edu.wynu.mapper;

import cn.edu.wynu.model.entity.Role;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface RoleMapper {

    @Select("SELECT * FROM role WHERE status = 1")
    List<Role> selectAll();

    @Select("SELECT * FROM role WHERE id = #{id}")
    Role selectById(Integer id);

    @Select("SELECT * FROM role WHERE role_code = #{roleCode}")
    Role selectByCode(String roleCode);

    @Insert("INSERT INTO role (role_name, role_code, description, status) " +
            "VALUES (#{roleName}, #{roleCode}, #{description}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Role role);

    @Update("UPDATE role SET role_name=#{roleName}, description=#{description}, status=#{status} " +
            "WHERE id=#{id}")
    int update(Role role);

    @Delete("DELETE FROM role WHERE id = #{id}")
    int deleteById(Integer id);
}
