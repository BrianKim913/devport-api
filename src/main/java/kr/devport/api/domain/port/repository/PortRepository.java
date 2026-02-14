package kr.devport.api.domain.port.repository;

import kr.devport.api.domain.port.entity.Port;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortRepository extends JpaRepository<Port, Long> {
    Optional<Port> findBySlug(String slug);
    Optional<Port> findByExternalId(String externalId);
    Optional<Port> findByPortNumber(Integer portNumber);
}
