package julia.books.domain.accounts;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
@Service
public interface AccountMapper {
    Account toDto(AccountEntity accountEntity);

    @Mapping(target = "birthday", ignore = true)
    @Mapping(target = "description", ignore = true)
    Account toShortDto(AccountEntity accountEntity);
}
