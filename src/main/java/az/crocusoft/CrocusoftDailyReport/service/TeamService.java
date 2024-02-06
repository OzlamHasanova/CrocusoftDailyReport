package az.crocusoft.CrocusoftDailyReport.service;

import az.crocusoft.CrocusoftDailyReport.dto.TeamDto;
import az.crocusoft.CrocusoftDailyReport.dto.TeamMemberDto;
import az.crocusoft.CrocusoftDailyReport.dto.response.TeamResponse;
import az.crocusoft.CrocusoftDailyReport.exception.TeamHasAssociatedEmployeesException;
import az.crocusoft.CrocusoftDailyReport.exception.TeamNotFoundException;
import az.crocusoft.CrocusoftDailyReport.exception.UpdateTimeException;
import az.crocusoft.CrocusoftDailyReport.model.Team;
import az.crocusoft.CrocusoftDailyReport.model.UserEntity;
import az.crocusoft.CrocusoftDailyReport.repository.TeamRepository;
import az.crocusoft.CrocusoftDailyReport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {
    private static final Logger logger = LoggerFactory.getLogger(TeamService.class);

    private final TeamRepository teamRepository;

    private final AuthenticationService authenticationService;

    public TeamResponse createTeam(TeamDto teamDto) {
        logger.info("Creating team");

        Team team = new Team();
        team.setName(teamDto.getName());
        teamRepository.save(team);

        TeamResponse teamResponse = mapToTeamResponse(team);

        logger.info("Team created successfully");
        return teamResponse;
    }

    public List<TeamResponse> getAllTeams() {
        logger.info("Getting all teams");

        List<Team> teamList = teamRepository.findAll();
        List<TeamResponse> teamResponseList = mapTeamsToResponses(teamList);

        logger.info("All teams retrieved successfully");
        return teamResponseList;
    }

    public TeamResponse updateTeam(Long teamId, TeamDto teamDto) {
        logger.info("Updating team with id: {}", teamId);

        Team existingTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new UpdateTimeException("Team not found with id: " + teamId));
        existingTeam.setName(teamDto.getName());
        teamRepository.save(existingTeam);

        TeamResponse teamResponse = mapToTeamResponse(existingTeam);

        logger.info("Team updated successfully");
        return teamResponse;
    }

    public TeamResponse getById(Long id) {
        logger.info("Getting team by id: {}", id);

        Optional<Team> teamOptional = teamRepository.findById(id);
        authenticationService.getSignedInUser();

        if (teamOptional.isPresent()) {
            Team team = teamOptional.get();
            TeamResponse teamDto = new TeamResponse();
            teamDto.setName(team.getName());

            List<TeamMemberDto> memberDtos = new ArrayList<>();
            for (UserEntity member : team.getMembers()) {
                TeamMemberDto memberDto = new TeamMemberDto();
                memberDto.setName(member.getName());
                memberDto.setSurname(member.getSurname());
                memberDto.setMail(member.getEmail());
                memberDtos.add(memberDto);
            }
            teamDto.setMembers(memberDtos);

            logger.info("Team retrieved successfully");
            return teamDto;
        }

        logger.warn("Team not found with id: {}", id);
        return null;
    }

    public void deleteTeam(Long teamId) {
        logger.info("Deleting team with id: {}", teamId);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team not found with id: " + teamId));

        List<UserEntity> teamMembers = team.getMembers();
        if (teamMembers.isEmpty()) {
            teamRepository.delete(team);
            logger.info("Team deleted successfully");
        } else {
            logger.error("Cannot delete the team with id: {} because it has associated employees", teamId);
            throw new TeamHasAssociatedEmployeesException("Cannot delete the team because it has associated employees.");
        }
    }

    public TeamResponse mapToTeamResponse(Team team) {
        TeamResponse response = new TeamResponse();
        response.setName(team.getName());

        List<TeamMemberDto> memberDtos = new ArrayList<>();
        for (UserEntity member : team.getMembers()) {
            TeamMemberDto memberDto = new TeamMemberDto();
            memberDto.setName(member.getName());
            memberDto.setSurname(member.getSurname());
            memberDto.setMail(member.getEmail());
            memberDtos.add(memberDto);
        }
        response.setMembers(memberDtos);

        return response;
    }

    public List<TeamResponse> mapTeamsToResponses(List<Team> teams) {
        return teams.stream()
                .map(team -> {
                    TeamResponse teamResponse = new TeamResponse();
                    teamResponse.setName(team.getName());
                    teamResponse.setMembers(mapMembersToDtos(team.getMembers()));
                    return teamResponse;
                })
                .collect(Collectors.toList());
    }

    private List<TeamMemberDto> mapMembersToDtos(List<UserEntity> members) {
        return members.stream()
                .map(member -> {
                    TeamMemberDto memberDto = new TeamMemberDto();
                    memberDto.setName(member.getName());
                    memberDto.setSurname(memberDto.getSurname());
                    memberDto.setMail(memberDto.getMail());
                    return memberDto;
                })
                .collect(Collectors.toList());
    }
}