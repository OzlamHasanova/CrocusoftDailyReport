package az.crocusoft.CrocusoftDailyReport.controller;

import az.crocusoft.CrocusoftDailyReport.dto.ProjectDto;
import az.crocusoft.CrocusoftDailyReport.dto.response.ProjectResponse;
import az.crocusoft.CrocusoftDailyReport.dto.response.ProjectResponseForFilter;
import az.crocusoft.CrocusoftDailyReport.model.Project;
import az.crocusoft.CrocusoftDailyReport.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/api/projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getById(@PathVariable Long id) throws ChangeSetPersister.NotFoundException {
        ProjectResponse projectResponse = projectService.getById(id);
        return ResponseEntity.ok(projectResponse);
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectDto projectDto) {
        ProjectResponse createdProject = projectService.createProject(projectDto);
        return ResponseEntity.ok(createdProject);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id, @RequestBody ProjectDto projectDTO) {
        ProjectResponse updatedProject = projectService.updateProject(id, projectDTO);
        return ResponseEntity.ok(updatedProject);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ProjectResponseForFilter>> filterProjectsByName(@RequestParam("projectName") String projectName) {
        List<ProjectResponseForFilter> filteredProjects = projectService.filterProjectsByName(projectName);
        return ResponseEntity.ok(filteredProjects);
    }
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
//        projectService.deleteProject(id);
//        return ResponseEntity.noContent().build();
//    }
}