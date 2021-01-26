package julia.books.security;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefreshTokensRepository extends CrudRepository<RefreshToken, UUID> {

}
