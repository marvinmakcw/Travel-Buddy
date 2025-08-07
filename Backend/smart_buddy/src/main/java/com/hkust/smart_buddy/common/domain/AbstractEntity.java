package com.hkust.smart_buddy.common.domain;

import com.hkust.smart_buddy.common.constants.DatabaseConstants;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Size;

@SuperBuilder
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@MappedSuperclass
public class AbstractEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 202507051234567890L;

    @Id
    @Column(name = DatabaseConstants.RECORD_ID, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @CreatedBy
    @Size(max = 50)
    @Column(name = DatabaseConstants.CREATED_BY, length = 50)
    private String createdBy;

    @CreatedDate
    @Column(name = DatabaseConstants.CREATED_DT)
    private LocalDateTime createdDate;

    @LastModifiedBy
    @Size(max = 50)
    @Column(name = DatabaseConstants.LAST_MODIFY_BY, length = 50)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = DatabaseConstants.LAST_MODIFY_DT)
    private LocalDateTime lastModifiedDate;

    @Version
    @ColumnDefault("0")
    @Column(name = DatabaseConstants.RECORD_VERSION)
    private Long recordVersion;

}