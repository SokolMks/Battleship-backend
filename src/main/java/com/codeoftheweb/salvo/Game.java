package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long gameId;
    private Date date;
    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<Score> scores = new HashSet<>();

    public Game() { }

    public Game(Date date){

        this.date = date;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long id){
        this.gameId = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void addGamePlayer(GamePlayer gamePlayer){
        gamePlayers.add(gamePlayer);
    }

    public Set<GamePlayer> getGamePlayers(){
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public List<Player> getPlayers(){
        return gamePlayers.stream().map(gamePlayer -> gamePlayer.getPlayer()).collect(Collectors.toList());
    }

    public void addScore(Score score){
        scores.add(score);
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores){
        this.scores = scores;
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameId=" + gameId +
                ", date=" + date +
                ", gamePlayers=" + gamePlayers +
                ", scores=" + scores +
                '}';
    }
}