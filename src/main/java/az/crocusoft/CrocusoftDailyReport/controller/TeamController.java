package az.crocusoft.CrocusoftDailyReport.controller;

import az.crocusoft.CrocusoftDailyReport.dto.TeamDto;
import az.crocusoft.CrocusoftDailyReport.dto.UserDto;
import az.crocusoft.CrocusoftDailyReport.dto.base.BaseResponse;
import az.crocusoft.CrocusoftDailyReport.dto.response.TeamResponse;
import az.crocusoft.CrocusoftDailyReport.model.Team;
import az.crocusoft.CrocusoftDailyReport.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/api/team")
public class TeamController {
    @Autowired
    private TeamService teamService;
    @PostMapping
    public TeamResponse createTeam(@RequestBody TeamDto teamDto) {
        return teamService.createTeam(teamDto);
    }
    @GetMapping("/all")
    public ResponseEntity<List<TeamResponse>> getAllTeams() {
        List<TeamResponse> teams = teamService.getAllTeams();
        return ResponseEntity.ok(teams);
    }
    @GetMapping("/get")
    public ResponseEntity<TeamResponse> getById(@RequestParam Long id) {
        TeamResponse teamResponse = teamService.getById(id);
        if (teamResponse != null) {
            return new ResponseEntity<>(teamResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TeamResponse> updateTeam(@PathVariable("id") Long teamId, @RequestBody TeamDto teamDto) {
        TeamResponse updatedTeam = teamService.updateTeam(teamId, teamDto);
        return ResponseEntity.ok(updatedTeam);
    }
    @DeleteMapping("/{teamId}")
    public ResponseEntity<String> deleteTeam(@PathVariable Long teamId) {
        try {
            teamService.deleteTeam(teamId);
            return ResponseEntity.ok("Team deleted successfully.");
        }
        catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body("Cannot delete the team because it has associated employees.");
        }
    }


}
