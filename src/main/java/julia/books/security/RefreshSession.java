package julia.books.security;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refreshsessions")
@Getter
@Setter
public class RefreshSession {
    @Id
    @GeneratedValue
    private long id;
    private int userId;
    private UUID refreshToken;
    private long expiresIn;
    private Instant createdAt;
}
