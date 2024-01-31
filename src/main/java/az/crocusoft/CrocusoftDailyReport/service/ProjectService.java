package az.crocusoft.CrocusoftDailyReport.service;

import az.crocusoft.CrocusoftDailyReport.dto.ProjectDto;
import az.crocusoft.CrocusoftDailyReport.dto.response.ProjectResponse;
import az.crocusoft.CrocusoftDailyReport.dto.response.ProjectResponseForFilter;
import az.crocusoft.CrocusoftDailyReport.dto.response.UserResponse;
import az.crocusoft.CrocusoftDailyReport.model.Project;
import az.crocusoft.CrocusoftDailyReport.model.UserEntity;
import az.crocusoft.CrocusoftDailyReport.repository.ProjectRepository;
import az.crocusoft.CrocusoftDailyReport.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;

    public ProjectResponse getById(Long id) throws ChangeSetPersister.NotFoundException {
        Optional<Project> optionalProject = projectRepository.findById(id);
        if (optionalProject.isEmpty()) {
            throw new ChangeSetPersister.NotFoundException();
        }

        Project project = optionalProject.get();
        List<UserEntity> employees = project.getUsers();

        // UserEntity listesini UserResponse listesine dönüştür
        List<UserResponse> userResponses = employees.stream()
                .map(user -> new UserResponse(user.getName(),user.getSurname(), user.getTeam().getName()))
                .collect(Collectors.toList());

        return new ProjectResponse(project.getName(), userResponses);
    }


    public ProjectResponse createProject(ProjectDto projectRequest) {
        Project project = new Project();
        project.setName(projectRequest.getName());

        List<UserEntity> employees = new ArrayList<>();
        for (Long employeeId : projectRequest.getEmployeeIds()) {
            Optional<UserEntity> optionalUser = userRepository.findById(employeeId);
            optionalUser.ifPresent(employees::add);
        }
        project.setUsers(employees);

        Project savedProject = projectRepository.save(project);

        List<UserResponse> employeeResponses = savedProject.getUsers().stream()
                .map(user -> new UserResponse(user.getName(),user.getSurname(), user.getTeam().getName()))
                .collect(Collectors.toList());

        return new ProjectResponse(savedProject.getName(), employeeResponses);
    }
    public ProjectResponse updateProject(Long id, ProjectDto projectDto) {
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));

        existingProject.setName(projectDto.getName());

        List<UserEntity> employees = new ArrayList<>();
        for (Long employeeId : projectDto.getEmployeeIds()) {
            Optional<UserEntity> optionalUser = userRepository.findById(employeeId);
            optionalUser.ifPresent(employees::add);
        }
        existingProject.setUsers(employees);

        Project savedProject = projectRepository.save(existingProject);

        List<UserResponse> employeeResponses = savedProject.getUsers().stream()
                .map(user -> new UserResponse(user.getName(),user.getSurname(), user.getTeam().getName()))
                .collect(Collectors.toList());

        return new ProjectResponse(savedProject.getName(), employeeResponses);
    }

    public List<ProjectResponseForFilter> filterProjectsByName(String projectName) {
        List<Project> projects = projectRepository.findByNameContainingIgnoreCase(projectName);
        List<ProjectResponseForFilter> filteredProjectResponses = projects.stream()
                .filter(project -> project.getName().toLowerCase().contains(projectName.toLowerCase()))
                .map(project -> new ProjectResponseForFilter(project.getName()))
                .collect(Collectors.toList());

        return filteredProjectResponses;

    }






//    public void deleteProject(Long id) {
//        Project project = projectRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));
//
//        project.setIsDeleted(true);
//        projectRepository.save(project);
//    }
}
