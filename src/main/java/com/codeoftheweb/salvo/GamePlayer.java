package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long gamePlayerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;


    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private Set<Ship> ships = new HashSet<>();
    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private Set<Salvo> salvos = new HashSet<>();
    private Date date;

    public GamePlayer(){}
    public GamePlayer(Date date, Game game, Player player){
        player.addGamePlayer(this);
        game.addGamePlayer(this);
        this.date = date;
        this.game = game;
        this.player = player;
    }


    public Long getGamePlayerId() {
        return gamePlayerId;
    }

    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }


    public Date getDate() {
        return date;
    }


    public void addShipTypes(Ship ship){
        ships.add(ship);
    }

    public Set<Ship> getShips(){
        return ships;
    }

    public void setShipTypes(Set<Ship> ships){
        this.ships = ships;
    }

    public void addSalvos(Salvo salvo){
        salvos.add(salvo);
    }

    public Set<Salvo> getSalvos(){
        return salvos;
    }

    public void setSalvos(Set<Salvo> salvos){
        this.salvos = salvos;
    }
}