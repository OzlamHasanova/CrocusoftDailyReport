package az.crocusoft.CrocusoftDailyReport.service;

import az.crocusoft.CrocusoftDailyReport.model.TeamRedis;
import az.crocusoft.CrocusoftDailyReport.repository.TeamRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamRedisService {
    private final TeamRedisRepository teamRedisRepository;
    public void create(String teamName){
        TeamRedis team=new TeamRedis();
        team.setName(teamName);
        teamRedisRepository.save(team);
    }

    public List<TeamRedis> getTeam() {
        List<TeamRedis> teams= (List<TeamRedis>) teamRedisRepository.findAll();
        return teams;
    }
}
