package az.crocusoft.CrocusoftDailyReport.service;

import az.crocusoft.CrocusoftDailyReport.dto.ProjectDto;
import az.crocusoft.CrocusoftDailyReport.dto.TeamDto;
import az.crocusoft.CrocusoftDailyReport.dto.response.*;
import az.crocusoft.CrocusoftDailyReport.exception.EmployeeNotFoundException;
import az.crocusoft.CrocusoftDailyReport.exception.ProjectAlreadyExistException;
import az.crocusoft.CrocusoftDailyReport.exception.ProjectNotFoundException;
import az.crocusoft.CrocusoftDailyReport.exception.TeamAlreadyExistException;
import az.crocusoft.CrocusoftDailyReport.model.Project;
import az.crocusoft.CrocusoftDailyReport.model.Team;
import az.crocusoft.CrocusoftDailyReport.model.UserEntity;
import az.crocusoft.CrocusoftDailyReport.model.enums.RoleEnum;
import az.crocusoft.CrocusoftDailyReport.model.enums.Status;
import az.crocusoft.CrocusoftDailyReport.repository.ProjectRepository;
import az.crocusoft.CrocusoftDailyReport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    public ProjectResponse getById(Long id) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        Project project = optionalProject.orElseThrow(() -> {
            logger.warn("Project not found with id: {}", id);
            return new ProjectNotFoundException("Project not found");
        });
        List<UserEntity> employees = project.getUsers();

        List<UserResponse> userResponses = employees.stream()
                .map(user -> new UserResponse(user.getId(),
                        user.getName(),
                        user.getSurname(),
                        (getTeamName(user))))
                .collect(Collectors.toList());

        logger.info("Retrieved project by id: {}", id);
        return new ProjectResponse(project.getName(), userResponses);
    }
    private String getTeamName(UserEntity user) {
        if (user.getTeam() != null) {
            return user.getTeam().getName();
        } else {
            return null;
        }
    }

    public ProjectResponse createProject(ProjectDto projectRequest) {
        Project project = new Project();
        boolean existSameNameProject=projectRepository.existsProjectByName(projectRequest.getName());
        if(existSameNameProject){
            throw new ProjectAlreadyExistException("Project already created with the same name");
        }
        project.setName(projectRequest.getName());
        project.setCreateDate(LocalDateTime.now());

        List<UserEntity> employees = new ArrayList<>();
        for (Long employeeId : projectRequest.getEmployeeIds()) {
            Optional<UserEntity> optionalUser = userRepository.findByIdAndIsDeletedAndStatus(employeeId, false,Status.ACTIVE);
            if (optionalUser.isPresent()) {
                UserEntity user = optionalUser.get();
                if (user.getRole().getRoleEnum() == RoleEnum.SUPERADMIN ||user.getRole().getRoleEnum() == RoleEnum.HEAD || user.getRole().getRoleEnum() == RoleEnum.ADMIN) {
                    throw new EmployeeNotFoundException("Employee with id  has a role of SUPERADMIN, HEAD or ADMIN. Skipping adding to the project.");
                }
                employees.add(user);
            } else {
                logger.warn("Employee not found with id: {}", employeeId);
                throw new EmployeeNotFoundException("Employee not found");
            }
        }
        project.setUsers(employees);

        Project savedProject = projectRepository.save(project);

        List<UserResponse> employeeResponses = savedProject.getUsers().stream()
                .map(user -> new UserResponse(user.getId(),user.getName(), user.getSurname(),Optional.ofNullable(user.getTeam())
                        .map(team -> team.getName())
                        .orElse(null)))
                .collect(Collectors.toList());

        logger.info("Created project with name: {}", savedProject.getName());
        return new ProjectResponse(savedProject.getName(), employeeResponses);
    }

    public ProjectResponse updateProject(Long id, ProjectDto projectDto) {
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Project not found with id: {}", id);
                    return new ProjectNotFoundException("Project not found with id: " + id);
                });

            boolean existSameNameProject=projectRepository.existsProjectByName(projectDto.getName());
            boolean existSameNameAndSameId=projectRepository.existsByIdAndName(id,projectDto.getName());
            if(existSameNameProject && !existSameNameAndSameId){
                throw new ProjectAlreadyExistException("Project already created with the same name");
            }


        existingProject.setName(projectDto.getName());

        List<UserEntity> employees = new ArrayList<>();
        for (Long employeeId : projectDto.getEmployeeIds()) {
            Optional<UserEntity> optionalUser = userRepository.findById(employeeId);
            if (optionalUser.isEmpty()) {
                logger.warn("Employee not found with id: {}", employeeId);
                throw new EmployeeNotFoundException("Employee not found");
            }
            optionalUser.ifPresent(employees::add);
        }
        existingProject.setUsers(employees);

        List<UserEntity> usersToRemove = new ArrayList<>();
        for (UserEntity user : existingProject.getUsers()) {
            if (!projectDto.getEmployeeIds().contains(user.getId())) {
                usersToRemove.add(user);
            }
        }
        existingProject.getUsers().removeAll(usersToRemove);

        Project savedProject = projectRepository.save(existingProject);

        List<UserResponse> employeeResponses = savedProject.getUsers().stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getSurname(),
                        Optional.ofNullable(user.getTeam())
                                .map(team -> team.getName())
                                .orElse(null)
                ))
                .collect(Collectors.toList());
        logger.info("Updated project with id: {}", id);
        return new ProjectResponse(savedProject.getName(), employeeResponses);
    }

    public ProjectResponseForSearch filterProjectsByName(String projectName, int page, int pageSize) throws ProjectNotFoundException {
        Page<Project> projects;
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by("createDate").descending());
        UserEntity user= authenticationService.getSignedInUser();
        if (user.getRoleEnum() == RoleEnum.EMPLOYEE) {
             projects=projectRepository.findByNameContainingIgnoreCaseAndUserIdOrderByCreationDateDesc(projectName,user.getId(),pageable);
            List<ProjectResponseForFilter> filteredProjectResponses = projects.stream()
                    .map(project -> new ProjectResponseForFilter(project.getId(), project.getName(), convertUserEntitiesToResponses(project.getUsers())))
                    .collect(Collectors.toList());

            if (filteredProjectResponses.isEmpty()) {
                logger.warn("Project not found with name: {}", projectName);
                throw new ProjectNotFoundException("Project not found");
            }
            return new ProjectResponseForSearch(filteredProjectResponses, projects.getTotalPages(), projects.getTotalElements(),projects.hasNext());

        }

        if (projectName == null) {
            projects = projectRepository.findAll(pageable);
        } else {
            projects = projectRepository.findByNameContainingIgnoreCaseOrderByCreationDateDesc(projectName,pageable);
        }

        List<ProjectResponseForFilter> filteredProjectResponses = projects.stream()
                .map(project -> new ProjectResponseForFilter(project.getId(), project.getName(), convertUserEntitiesToResponses(project.getUsers())))
                .collect(Collectors.toList());

        if (filteredProjectResponses.isEmpty()) {
            logger.warn("Project not found with name: {}", projectName);
            throw new ProjectNotFoundException("Project not found");
        }
        return new ProjectResponseForSearch(filteredProjectResponses, projects.getTotalPages(), projects.getTotalElements(),projects.hasNext());

    }
    public List<UserResponse> convertUserEntitiesToResponses(List<UserEntity> userEntities) {
        return userEntities.stream()
                .map(this::convertUserEntityToResponse)
                .collect(Collectors.toList());
    }

    private UserResponse convertUserEntityToResponse(UserEntity userEntity) {

        UserResponse userResponse = new UserResponse();
        if (userEntity.getTeam() != null) {
            userResponse.setTeamName(userEntity.getTeam().getName());
        } else {
            userResponse.setTeamName(null);
        }
        userResponse.setId(userEntity.getId());
        userResponse.setName(userEntity.getName());
        userResponse.setSurname(userEntity.getSurname());

        return userResponse;
    }
}