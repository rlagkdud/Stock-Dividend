package com.dayone.persist;

import com.dayone.persist.entity.DividendEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DividendRepository extends JpaRepository<DividendEntity, Long> {
    List<DividendEntity> findByCompanyId(Long companyId);

    boolean existsByCompanyIdAndDate(Long companyId, LocalDateTime date);
}
