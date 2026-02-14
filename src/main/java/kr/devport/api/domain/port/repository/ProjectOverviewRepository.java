package kr.devport.api.domain.port.repository;

import kr.devport.api.domain.port.entity.ProjectOverview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectOverviewRepository extends JpaRepository<ProjectOverview, Long> {
    Optional<ProjectOverview> findByProject_ExternalId(String projectExternalId);
}
