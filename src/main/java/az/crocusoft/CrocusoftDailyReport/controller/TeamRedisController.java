package az.crocusoft.CrocusoftDailyReport.controller;

import az.crocusoft.CrocusoftDailyReport.model.TeamRedis;
import az.crocusoft.CrocusoftDailyReport.repository.TeamRedisRepository;
import az.crocusoft.CrocusoftDailyReport.service.TeamRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/api/team/redis")
@RequiredArgsConstructor
public class TeamRedisController {
    private final TeamRedisService service;
    @PostMapping
    public String create(String teamName){
        service.create(teamName);
        return "creat is success";
    }
    @GetMapping
    public List<TeamRedis> getTeam(){
        List<TeamRedis> team=service.getTeam();
        return team;
    }
}
