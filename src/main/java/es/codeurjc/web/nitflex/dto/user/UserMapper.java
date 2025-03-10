package es.codeurjc.web.nitflex.dto.user;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import es.codeurjc.web.nitflex.model.User;

@Mapper(componentModel = "spring")
public interface  UserMapper {

    UserDTO toDTO(User user);

    List<UserDTO> toDTOs(Collection<User> users);
    
    User toDomain(UserDTO userDTO);
}
