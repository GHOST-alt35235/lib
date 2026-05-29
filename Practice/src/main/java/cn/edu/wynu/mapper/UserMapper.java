package cn.edu.wynu.mapper;

import cn.edu.wynu.model.entity.User;
import cn.edu.wynu.model.entity.Role;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(String username);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(Integer id);

    @Select("SELECT * FROM user WHERE status = 1")
    List<User> selectAll();

    @Insert("INSERT INTO user (username, password, real_name, email, phone, avatar, status) " +
            "VALUES (#{username}, #{password}, #{realName}, #{email}, #{phone}, #{avatar}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE user SET username=#{username}, real_name=#{realName}, email=#{email}, " +
            "phone=#{phone}, avatar=#{avatar}, status=#{status} WHERE id=#{id}")
    int update(User user);

    @Update("UPDATE user SET password=#{password} WHERE id=#{id}")
    int updatePassword(@Param("id") Integer id, @Param("password") String password);

    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(Integer id);

    @Select("SELECT r.* FROM role r " +
            "INNER JOIN user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<Role> selectRolesByUserId(Integer userId);

    @Insert("<script>" +
            "INSERT INTO user_role (user_id, role_id) VALUES " +
            "<foreach collection='roleIds' item='roleId' separator='),('>" +
            "(#{userId}, #{roleId})" +
            "</foreach>" +
            "</script>")
    int insertUserRoles(@Param("userId") Integer userId, @Param("roleIds") List<Integer> roleIds);

    @Delete("DELETE FROM user_role WHERE user_id = #{userId}")
    int deleteUserRoles(Integer userId);

    @Select("SELECT COUNT(*) FROM user WHERE username = #{username} AND id != #{excludeId}")
    int countByUsername(@Param("username") String username, @Param("excludeId") Integer excludeId);
}
