package com.codeoftheweb.salvo;

import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;

@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long salvoId;
    private Integer turnNumber;

    @ElementCollection
    @Column(name = "salvo_location")
    private List<String> salvoLocations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;
    private Integer turn;

    public Salvo() {}

    public Salvo(GamePlayer gamePlayers, List<String> salvoLocations, Integer turn){
       this.gamePlayer = gamePlayers;
       this.salvoLocations = salvoLocations;
       this.turn = turn;
       gamePlayers.addSalvos(this);
    }
    //Salvo Id
    public Long getSalvoId() {
        return salvoId;
    }
    public void setSalvoId(Long salvoId) {
        this.salvoId = salvoId;
    }
    //turn number
    public Integer getTurn(){
        return turn;
    }
    public void setTurn(Integer turn){
        this.turn = turn;
    }
    //Salvo Location
    public List<String> getSalvoLocations() {
        return salvoLocations;
    }
    public void setSalvoLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }
    //Game Player
    public GamePlayer getGamePlayer(){
        return gamePlayer;
    }
    @JsonIgnore
    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }
}