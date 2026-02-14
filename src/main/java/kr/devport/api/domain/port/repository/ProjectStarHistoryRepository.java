package kr.devport.api.domain.port.repository;

import kr.devport.api.domain.port.entity.ProjectStarHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjectStarHistoryRepository extends JpaRepository<ProjectStarHistory, Long> {
    List<ProjectStarHistory> findByProject_ExternalIdAndDateBetweenOrderByDateAsc(
        String projectExternalId, LocalDate from, LocalDate to
    );
}
