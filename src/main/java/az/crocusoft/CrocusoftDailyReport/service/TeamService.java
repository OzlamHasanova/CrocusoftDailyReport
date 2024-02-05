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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamService {
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationService authenticationService;


    public TeamResponse createTeam(TeamDto teamDto) {
        Team team = new Team();
        team.setName(teamDto.getName());

//        List<UserEntity> members = new ArrayList<>();
//        for (Long memberId : teamDto.getMemberIds()) {
//            UserEntity member = userRepository.findById(memberId).orElse(null);
//            if (member != null) {
//                members.add(member);
//            }
//            member.setTeam(team);
//        }
//        team.setMembers(members);
        teamRepository.save(team);
        TeamResponse teamResponse=mapToTeamResponse(team);

        return teamResponse;
    }

    public List<TeamResponse> getAllTeams() {
        List<Team> teamList=teamRepository.findAll();
        List<TeamResponse> teamResponseList=mapTeamsToResponses(teamList);
        return teamResponseList;
    }
    public TeamResponse updateTeam(Long teamId,TeamDto teamDto) {
        Team existingTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new UpdateTimeException("Team not found with id: " + teamId));
        existingTeam.setName(teamDto.getName());
        teamRepository.save(existingTeam);
        TeamResponse teamResponse=mapToTeamResponse(existingTeam);

        return teamResponse;
    }

    public TeamResponse getById(Long id) {
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

            return teamDto;
        }
        return null;
    }
    public void deleteTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team not found with id: " + teamId));

        List<UserEntity> teamMembers = team.getMembers();
        if (teamMembers.isEmpty()) {
            teamRepository.delete(team);
        } else {
            throw new TeamHasAssociatedEmployeesException("Cannot delete the team because it has associated employees.");
        }
    }
    public static TeamResponse mapToTeamResponse(Team team) {
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

    public static List<TeamResponse> mapTeamsToResponses(List<Team> teams) {
        return teams.stream()
                .map(team -> {
                    TeamResponse teamResponse = new TeamResponse();
                    teamResponse.setName(team.getName());
                    teamResponse.setMembers(mapMembersToDtos(team.getMembers()));
                    return teamResponse;
                })
                .collect(Collectors.toList());
    }

    private static List<TeamMemberDto> mapMembersToDtos(List<UserEntity> members) {
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