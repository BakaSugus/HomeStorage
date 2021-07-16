package cn.j.netstorage.Entity.DTO;

import cn.j.netstorage.Entity.User.Role;
import cn.j.netstorage.Entity.User.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Setter
@Getter
public class UserDTO {
    private long uid;

    private String name;
    private String email;
    private List<String> roleName;


    public UserDTO() {

    }

    public User convertUser() {
        return convertUser(this);
    }

    public User convertUser(UserDTO userDTO) {
        return convertUser(userDTO, new User());
    }

    public User convertUser(UserDTO userDTO, User user) {
        user.setUid(userDTO.getUid());
        user.setNickName(userDTO.getName());
        user.setEmailAccount(userDTO.getEmail());
        return user;
    }

    public List<User> convertUsers() {
        return Collections.singletonList(convertUser());
    }


    public UserDTO(User user) {
        System.out.println(user);
        this.uid = user.getUid();
        this.email = user.getEmailAccount();
        List<String> strings=new ArrayList<>();
        for (Role role:user.getRole()){
            strings.add(role.getName());
        }
        this.roleName=strings;
        this.name = user.getNickName();
    }

    public static List<UserDTO> userDTOS(List<User> users) {
        ArrayList<UserDTO> userDTOS = new ArrayList<>();
        users.forEach((value) -> {
            userDTOS.add(new UserDTO(value));
        });
        return userDTOS;
    }

}
