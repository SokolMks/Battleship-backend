package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long shipId;
    private String type;

    @ElementCollection
    @Column(name = "ship_location")
    private List<String> locations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    private Integer damage = 0;
    @ElementCollection
    @Column(name = "ship_hit")
    private Set<String> hits = new HashSet<>();

    public Ship(){}
    //Ship
    public Ship(String type, List<String> locations, GamePlayer gamePlayer){
        this.type = type;
        this.locations = locations;
        this.gamePlayer = gamePlayer;
        gamePlayer.addShipTypes(this);
    }
    //Ship Id
    public Long getShipId(){
        return shipId;
    }

    public void setShipId(Long shipId){
        this.shipId = shipId;
    }
    //ShipType
    public String getShipType(){
        return type;
    }

    public void setShipType(String shipType){
        this.type = shipType;
    }
    //gamePlayer
    public GamePlayer getGamePlayer(){
        return gamePlayer;
    }
    //Ship Location
    public List<String> getShipLocations(){
        return locations;
    }

    public void setShipLocations(List<String> shipLocations){
        this.locations = shipLocations;
    }
    //Game Player
    public void setGamePlayer(GamePlayer gamePlayer){
        this.gamePlayer = gamePlayer;
        gamePlayer.addShipTypes(this);
    }
    //Damage
    public Integer getDamage(){
        return damage;
    }

    public void setDamage(Integer damage){
        this.damage = damage;
    }
    //Hits
    public Set<String> getHits(){
        return hits;
    }

    public void setHits(Set<String> hits){
        this.hits = hits;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    @Override
    public String toString() {
        return "Ship{" +
                "shipId=" + shipId +
                ", type='" + type + '\'' +
                ", locations=" + locations +
                ", gamePlayer=" + gamePlayer +
                ", damage=" + damage +
                ", hits=" + hits +
                '}';
    }
}