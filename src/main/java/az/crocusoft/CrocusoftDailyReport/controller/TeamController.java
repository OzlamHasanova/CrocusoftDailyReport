package az.crocusoft.CrocusoftDailyReport.controller;

import az.crocusoft.CrocusoftDailyReport.dto.TeamDto;
import az.crocusoft.CrocusoftDailyReport.dto.base.BaseResponseWithData;
import az.crocusoft.CrocusoftDailyReport.dto.request.TeamRequest;
import az.crocusoft.CrocusoftDailyReport.dto.response.TeamResponse;
import az.crocusoft.CrocusoftDailyReport.dto.response.TeamResponseForGet;
import az.crocusoft.CrocusoftDailyReport.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    @PostMapping
    public ResponseEntity<BaseResponseWithData<TeamRequest>> createTeam(@RequestBody TeamRequest teamRequest) {
        teamService.createTeam(teamRequest);
        return ResponseEntity.ok(new BaseResponseWithData<>("create is succeesfuly",teamRequest));
    }
    @GetMapping
    public ResponseEntity<List<TeamResponse>> getAllTeams() {
        List<TeamResponse> teams = teamService.getAllTeams();
        return ResponseEntity.ok(teams);
    }
    @GetMapping("/{id}")
    public ResponseEntity<TeamResponseForGet> getById(@PathVariable("id") Long id) {
        TeamResponseForGet teamResponse = teamService.getById(id);
        if (teamResponse != null) {
            return new ResponseEntity<>(teamResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<TeamResponse> updateTeam(@PathVariable("id") Long teamId, @RequestBody TeamRequest teamRequest) {
        TeamResponse updatedTeam = teamService.updateTeam(teamId, teamRequest);
        return ResponseEntity.ok(updatedTeam);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTeam(@PathVariable("id") Long teamId) {
        try {
            teamService.deleteTeam(teamId);
            return ResponseEntity.ok("Team deleted successfully.");
        }
        catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body("Cannot delete the team because it has associated employees.");
        }
    }


}
