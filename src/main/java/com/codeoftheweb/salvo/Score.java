package com.codeoftheweb.salvo;

import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;

@Entity
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private Double score;
    private Date finishDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    public Score() {}
    public Score(Double score, Player player, Game game){
        this.score = score;
        this.player = player;
        this.game = game;
        player.addScore(this);
        game.addScore(this);
    }

    //Score Ids
    public Long getId() {
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }

    //Score
    public Double getScore(){
        return score;
    }
    public void setScore(double score){
        this.score = score;
    }


    @JsonIgnore
    public Player getPlayer(){
        return player;
    }
    public void setPlayer(Player player){
        this.player = player;
    }
    @JsonIgnore
    public Game getGame(){
        return game;
    }
    public void setGame(Game game){
        this.game = game;
    }
}