package julia.books.domain.accounts;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
@Service
public interface AccountMapper {
    Account toDto(AccountEntity accountEntity);
}
