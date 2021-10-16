package kernel.heiecab.dto.mapper;

import kernel.heiecab.domain.User;
import kernel.heiecab.dto.request.RegisterRequest;
import kernel.heiecab.dto.response.RegisterResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper()
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    User fromDto(final RegisterRequest request);

    RegisterResponse toDTO(final User user);
}
