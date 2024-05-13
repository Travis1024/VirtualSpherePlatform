package org.travis.center.auth.service;

import org.travis.center.auth.pojo.dto.UserModifyPasswordDTO;
import org.travis.center.auth.pojo.dto.UserModifyRoleDTO;
import org.travis.center.auth.pojo.dto.UserRegisterDTO;
import org.travis.center.auth.pojo.dto.UserUpdateDTO;
import org.travis.center.common.entity.auth.User;
import com.baomidou.mybatisplus.extension.service.IService;
    /**
 * @ClassName UserService
 * @Description UserService
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/13
 */
public interface UserService extends IService<User>{
    void login(String username, String password);
    void register(UserRegisterDTO userRegisterDTO);
    User queryById(Long userId);
    boolean checkAdminUser(Long userId);
    void updateUserInfo(UserUpdateDTO userUpdateDTO);
    void updatePassword(UserModifyPasswordDTO userModifyPasswordDTO);
    void updateUserRole(UserModifyRoleDTO userModifyRoleDTO);
    void userDelete(Long userId);
}
