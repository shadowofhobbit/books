package julia.books.domain.accounts;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface AccountRepository extends CrudRepository<Account, Integer> {
    Optional<Account> findByUsername(String username);

}
