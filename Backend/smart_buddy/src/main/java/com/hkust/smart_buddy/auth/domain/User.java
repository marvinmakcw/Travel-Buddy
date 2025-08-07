package com.hkust.smart_buddy.auth.domain;

import com.hkust.smart_buddy.common.constants.DatabaseConstants;
import com.hkust.smart_buddy.common.domain.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serial;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = DatabaseConstants.USER_TABLE)
public class User extends AbstractEntity {
    @Serial
    private static final long serialVersionUID = 3544909864440647692L;

    @NotNull
    @Size(max = 36)
    @Column(name = DatabaseConstants.USER_ID, nullable = false, length = 36)
    private String userId;

    @Size(max = 255)
    @NotNull
    @Column(name = DatabaseConstants.USERNAME, nullable = false)
    private String username;

    @Size(max = 255)
    @NotNull
    @Column(name = DatabaseConstants.PASSWORD, nullable = false)
    private String password;

    @Size(max = 255)
    @NotNull
    @Column(name = DatabaseConstants.EMAIL, nullable = false)
    private String email;
}