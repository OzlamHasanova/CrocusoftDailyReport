package az.crocusoft.CrocusoftDailyReport.controller;

import az.crocusoft.CrocusoftDailyReport.constant.PaginationConstants;
import az.crocusoft.CrocusoftDailyReport.dto.ProjectDto;
import az.crocusoft.CrocusoftDailyReport.dto.response.ProjectResponse;
import az.crocusoft.CrocusoftDailyReport.dto.response.ProjectResponseForFilter;
import az.crocusoft.CrocusoftDailyReport.dto.response.ProjectResponseForSearch;
import az.crocusoft.CrocusoftDailyReport.model.Project;
import az.crocusoft.CrocusoftDailyReport.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getById(@PathVariable Long id)  {
        ProjectResponse projectResponse = projectService.getById(id);
        return ResponseEntity.ok(projectResponse);
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectDto projectDto) {
        ProjectResponse createdProject = projectService.createProject(projectDto);
        return ResponseEntity.ok(createdProject);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id, @RequestBody ProjectDto projectDTO) {
        ProjectResponse updatedProject = projectService.updateProject(id, projectDTO);
        return ResponseEntity.ok(updatedProject);
    }

    @GetMapping("/search")
    public ResponseEntity<ProjectResponseForSearch> filterProjectsByName(@RequestParam(value = "projectName", required = false) String projectName,
                                                                         @RequestParam(name = "page", defaultValue = PaginationConstants.PAGE_NUMBER) Integer page,
                                                                         @RequestParam(name = "pageSize", defaultValue = PaginationConstants.PAGE_SIZE) Integer size) {
        return ResponseEntity.ok(projectService.filterProjectsByName(projectName,page,size));
    }

}