package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long playerId;
    private String email;
    private String name;
    private String password;


    @OneToMany(mappedBy = "player", fetch=FetchType.EAGER)
    private Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<Score> scores = new HashSet<>();

    public Player() {}

    public Player(String email, String name, String password){
        this.email = email;
        this.name = name;
        this.password = password;
    }
    //Player Id
    public Long getPlayerId(){
        return playerId;
    }

    public void setPlayerId(Long id){
        this.playerId = id;
    }
    //Email
    public String getEmail() {
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    //Username - name
    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    //password
    public void setPassword(String password){
        this.password = password;
    }
    public String getPassword(){
        return password;
    }

    //Game player
    public void addGamePlayer(GamePlayer gamePlayer){
        gamePlayers.add(gamePlayer);
    }

    public Set<Game> getGames(){
        return gamePlayers.stream().map(sub -> sub.getGame()).collect(Collectors.toSet());
    }
    //Score
    public void addScore(Score score){
        scores.add(score);
    }

    public List<Double> getScores(){
        return scores.stream().map(score -> score.getScore()).collect(Collectors.toList());
    }

    public void setScores(Set<Score> scores){
        this.scores = scores;
    }

    public Score updateScore(Game game){
        return this.scores.stream().filter(score -> score.getGame().equals(game)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return "Player{" +
                "playerId=" + playerId +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", gamePlayers=" + gamePlayers +
                ", scores=" + scores +
                '}';
    }
}