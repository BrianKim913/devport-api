package kr.devport.api.domain.port.service;

import kr.devport.api.domain.port.dto.response.HotReleaseResponse;
import kr.devport.api.domain.port.dto.response.PortDetailResponse;
import kr.devport.api.domain.port.dto.response.PortResponse;
import kr.devport.api.domain.port.dto.response.ProjectSummaryResponse;
import kr.devport.api.domain.port.entity.Port;
import kr.devport.api.domain.port.entity.Project;
import kr.devport.api.domain.port.entity.ProjectEvent;
import kr.devport.api.domain.port.repository.PortRepository;
import kr.devport.api.domain.port.repository.ProjectEventRepository;
import kr.devport.api.domain.port.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortService {

    private final PortRepository portRepository;
    private final ProjectRepository projectRepository;
    private final ProjectEventRepository projectEventRepository;

    @Transactional(readOnly = true)
    public List<PortResponse> getAllPorts() {
        List<Port> ports = portRepository.findAll(Sort.by(Sort.Direction.ASC, "portNumber"));

        return ports.stream()
            .map(this::toPortResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PortDetailResponse getPortBySlug(String slug) {
        Port port = portRepository.findBySlug(slug)
            .orElseThrow(() -> new IllegalArgumentException("Port not found with slug: " + slug));

        // Get all projects in this port, sorted by stars
        List<Project> projects = projectRepository.findByPort_Slug(
            slug,
            Sort.by(Sort.Direction.DESC, "stars")
        );

        // Get hot releases (high-impact events, top 10)
        List<ProjectEvent> hotEvents = projectEventRepository
            .findTop10ByProject_Port_SlugAndImpactScoreGreaterThanEqualOrderByReleasedAtDesc(slug, 70);

        return PortDetailResponse.builder()
            .port(toPortResponse(port))
            .projects(projects.stream().map(this::toProjectSummary).collect(Collectors.toList()))
            .hotReleases(hotEvents.stream().map(this::toHotRelease).collect(Collectors.toList()))
            .build();
    }

    private PortResponse toPortResponse(Port port) {
        // Count projects and recent releases
        long projectCount = projectRepository.count();
        // Note: recentReleaseCount calculation would need a more complex query
        // For now, leaving as 0 or you can implement a custom query

        return PortResponse.builder()
            .id(port.getExternalId())
            .slug(port.getSlug())
            .portNumber(port.getPortNumber())
            .name(port.getName())
            .description(port.getDescription())
            .accentColor(port.getAccentColor())
            .projectCount((int) projectCount)
            .recentReleaseCount(0) // TODO: Implement if needed
            .build();
    }

    private ProjectSummaryResponse toProjectSummary(Project project) {
        return ProjectSummaryResponse.builder()
            .id(project.getExternalId())
            .name(project.getName())
            .fullName(project.getFullName())
            .stars(project.getStars())
            .starsWeekDelta(project.getStarsWeekDelta())
            .language(project.getLanguage())
            .languageColor(project.getLanguageColor())
            .releases30d(project.getReleases30d())
            .sparklineData(List.of()) // TODO: Implement sparkline data if needed
            .build();
    }

    private HotReleaseResponse toHotRelease(ProjectEvent event) {
        return HotReleaseResponse.builder()
            .id(event.getExternalId())
            .projectName(event.getProject().getName())
            .version(event.getVersion())
            .releasedAt(event.getReleasedAt())
            .eventTypes(event.getEventTypes())
            .summary(event.getSummary())
            .impactScore(event.getImpactScore())
            .isSecurity(event.getIsSecurity())
            .isBreaking(event.getIsBreaking())
            .build();
    }
}
