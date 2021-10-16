package kernel.heiecab.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "\"user\"")
public class User {
    @Id
    private Integer id;

    @Column("firstname")
    private String firstName;

    @Column("lastname")
    private String lastName;
    private String patronymic;
    private String username;
    private String email;
    private String password;
    private String type;

    @Column("created_at")
    @CreatedDate
    private LocalDateTime createdAt;
}
