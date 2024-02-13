package az.crocusoft.CrocusoftDailyReport.controller;

import az.crocusoft.CrocusoftDailyReport.dto.TeamDto;
import az.crocusoft.CrocusoftDailyReport.dto.base.BaseResponseWithData;
import az.crocusoft.CrocusoftDailyReport.dto.response.TeamResponse;
import az.crocusoft.CrocusoftDailyReport.dto.response.TeamResponseForGet;
import az.crocusoft.CrocusoftDailyReport.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/api/teams")
public class TeamController {
    @Autowired
    private TeamService teamService;
    @PostMapping
    public ResponseEntity<BaseResponseWithData<String>> createTeam(@RequestParam String teamName) {
        teamService.createTeam(teamName);
        return ResponseEntity.ok(new BaseResponseWithData<>("create is succeesfuly",teamName));
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
    public ResponseEntity<TeamResponse> updateTeam(@PathVariable("id") Long teamId, @RequestBody TeamDto teamDto) {
        TeamResponse updatedTeam = teamService.updateTeam(teamId, teamDto);
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
