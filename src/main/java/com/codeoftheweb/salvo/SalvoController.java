package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController

public class SalvoController {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private ShipRepository shipRepository;
    @Autowired
    private SalvoRepository salvoRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping(value = "/api/games", method = RequestMethod.GET)
    public Map<String, Object> getGames(Authentication authentication) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("games", gameRepository.findAll().stream().map(game -> makeGameDTO(game)).collect(toList()));
        if (authentication == null) {
            dto.put("player", null);
        } else {
            dto.put("player", makePlayerDTO(logInPlayer(authentication)));
        }
        return dto;
    }

    private Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", game.getGameId());
        dto.put("date", game.getDate());
        dto.put("gameplayers", game.getGamePlayers().stream().map(gamePlayer -> makeGamePlayerDTO(gamePlayer)));
        return dto;
    }

    private Map<String, Object> makeGamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", gamePlayer.getGamePlayerId());
        dto.put("player", makePlayerDTO(gamePlayer.getPlayer()));
        return dto;
    }

    private Map<String, Object> makePlayerDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", player.getPlayerId());
        dto.put("username", player.getName());
        dto.put("email", player.getEmail());

        return dto;
    }


    @RequestMapping(value = "/api/game_view/{Id}", method = RequestMethod.GET)
    public Map<String, Object> getOneGame(@PathVariable("Id") Long Id) {
        Map<String, Object> dto = new LinkedHashMap<>();
        GamePlayer gamePlayer = gamePlayerRepository.getOne(Id);

        dto.put("Game", makeGameDTO(gamePlayer.getGame()));
        dto.put("Ship", gamePlayer.getShips().stream().map(this::makeShipDto));
        dto.put("mySalvo", gamePlayer.getSalvos().stream().map(this::makeSalvoDto));
        dto.put("State", makeLogicDTO(gamePlayer));

        if(opponentPlayer(gamePlayer) != null) {
            dto.put("Turn", gamePlayer.getSalvos().stream().sorted(Comparator.comparing(Salvo::getTurn)).map(this::makeTurnDTO));
            dto.put("sunkShips", makeSunkDTO(gamePlayer.getSalvos(), gamePlayer));
            dto.put("oppSalvo", opponentPlayer(gamePlayer).getSalvos().stream().map(this::makeSalvoDto));
        }
        return dto;
    }

    private Map<String, Object> makeShipDto(Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("Type", ship.getShipType());
        dto.put("Locations", ship.getShipLocations());
        return dto;
    }

    private Map<String, Object> makeSalvoDto(Salvo salvo) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("Turn", salvo.getTurn());
        dto.put("Location", salvo.getSalvoLocations());
        return dto;
    }

    @RequestMapping(value = "/api/leaderBoard", method = RequestMethod.GET)
    public List<Map<String, Object>> getLeaderBoard() {
        return playerRepository.findAll().stream().map(player -> getPlayerDetailDTO(player)).collect(toList());
    }
    private Map<String, Object> getPlayerDetailDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("Player", makePlayerDTO(player));
        dto.put("Score", player.getScores());
        return dto;
    }
    @RequestMapping(value = "/api/register", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getRegister(@RequestBody Player player) {
        if (player.getEmail().isEmpty() || player.getName().isEmpty() || player.getPassword().isEmpty()) {
            return new ResponseEntity<>(createMap("Error", "Data is empty"), HttpStatus.FORBIDDEN);
        }
        if (playerRepository.findByEmail(player.getEmail()) != null) {
            return new ResponseEntity<>(createMap("Error", "Name is occupied"), HttpStatus.FORBIDDEN);
        }
        player.setPassword(passwordEncoder.encode(player.getPassword()));
        Player newPlayer = playerRepository.save(player);
        return new ResponseEntity<>(createMap("Id", newPlayer.getPlayerId()), HttpStatus.CREATED);
    }



    private GamePlayer opponentPlayer(GamePlayer gamePlayer) {
        return gamePlayer.getGame()
                .getGamePlayers()
                .stream()
                .filter(gamePlayer1 -> !gamePlayer1.getGamePlayerId().equals(gamePlayer.getGamePlayerId()))
                .findFirst()
                .orElse(null);
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    private Player logInPlayer(Authentication authentication) {
        if (authentication != null) {
            return playerRepository.findByEmail(authentication.getName());
        } else {
            return null;
        }
    }


    @RequestMapping(path = "/api/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        if (authentication != null) {
            //if there is a logged in player create a new game and save it into the game repository
            Game game1 = new Game(new Date());
            gameRepository.save(game1);

            //if there is a logged in player get the logged player info no need to save it to repository as this player already exists
            Player player1 = logInPlayer(authentication);
            if (player1 == null) {
                return new ResponseEntity<>(createMap("error", "You need to put in some info"), HttpStatus.UNAUTHORIZED);
            }
            //gamePlayer saves both game and player into the gamePlayer repository which includes info for both games and players
            GamePlayer gamePlayer = new GamePlayer(new Date(), game1, player1);
            gamePlayerRepository.save(gamePlayer);
            return new ResponseEntity<>(createMap("gpid", gamePlayer.getGamePlayerId()), HttpStatus.CREATED);

        } else {
            return new ResponseEntity<>(createMap("error", "Please Log-In to be able to create games."), HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/api/games/players/{gamePlayerId}/salvos", method = RequestMethod.POST)
    public ResponseEntity <Map<String, Object>> postSalvos(@PathVariable ("gamePlayerId") Long id, Authentication authentication, @RequestBody List<String> salvoLoc){
        Player player = logInPlayer(authentication);
        if (authentication == null){
            return new ResponseEntity<>(createMap("Error", "You have to log in!"), HttpStatus.UNAUTHORIZED);
        }
        if(player == null){
            return new ResponseEntity<>(createMap("Error", "You have to log in!"), HttpStatus.FORBIDDEN);
        }
        GamePlayer gamePlayer = gamePlayerRepository.getOne(id);

        if (gamePlayer == null){
            return new ResponseEntity<>(createMap("Error", "You can't join this game, because it doesn't exist"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getShips().size() < 5){
            return new ResponseEntity<>(createMap("Error", "Can't shoot if you don't have any ships placed"), HttpStatus.FORBIDDEN);
        }
        if ( opponentPlayer(gamePlayer).getShips().size() < 5){
            return new ResponseEntity<>(createMap("Error", "Wait for opponent to place the ships before shooting"), HttpStatus.FORBIDDEN);
        }
        if (((makeSunkDTO(gamePlayer.getSalvos(), gamePlayer).get("sunk").size()==5)
         && gamePlayer.getSalvos().size() == opponentPlayer(gamePlayer).getSalvos().size()) ||
                ((makeSunkDTO(opponentPlayer(gamePlayer).getSalvos(), opponentPlayer(gamePlayer)).get("sunk").size() == 5)
                &&gamePlayer.getSalvos().size() ==opponentPlayer(gamePlayer).getSalvos().size()) ||
                ((makeSunkDTO(gamePlayer.getSalvos(), gamePlayer).get("sunk").size()==5)
                && (makeSunkDTO(opponentPlayer(gamePlayer).getSalvos(), opponentPlayer(gamePlayer)).get("sunk").size() == 5)
                && gamePlayer.getSalvos().size() == opponentPlayer(gamePlayer).getSalvos().size())){
            return new ResponseEntity<>(createMap("error", "Game over"), HttpStatus.FORBIDDEN);
        }
        if (salvoLoc.size() < 5){
            return new ResponseEntity<>(createMap("Error", "You have to fire 5 shots!"), HttpStatus.FORBIDDEN);
        }
        if ((opponentPlayer(gamePlayer).getSalvos().size() < gamePlayer.getSalvos().size())){
            return new ResponseEntity<>(createMap("Error", "Wait for the opponent player to shoot!"), HttpStatus.FORBIDDEN);
        }

        Salvo salvo = new Salvo(gamePlayer, salvoLoc, gamePlayer.getSalvos().size()+1);
        salvoRepository.save(salvo);

        return new ResponseEntity<>(createMap("Placed", "Salvos shot"), HttpStatus.CREATED);
    }

    @RequestMapping(path = "/api/game/{gameID}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameID, Authentication authentication) {

        if (authentication == null) {
            return new ResponseEntity<>(createMap("error", "You need to be logged in to be able to join games"), HttpStatus.UNAUTHORIZED);
        } else {
            Player player1 = logInPlayer(authentication);
            if (player1 == null) {
                return new ResponseEntity<>(createMap("error", "You need to put in some info"), HttpStatus.UNAUTHORIZED);
            }
            Game game1 = gameRepository.getOne(gameID);

            if (game1 == null) {
                return new ResponseEntity<>(createMap("error", "Sorry, this game doesn't exist"), HttpStatus.FORBIDDEN);
            }

            if (game1.getGamePlayers().size() == 2) {
                return new ResponseEntity<>(createMap("error", "Game is full"), HttpStatus.FORBIDDEN);
            }

            GamePlayer gamePlayer = new GamePlayer(new Date(), game1, player1);
            gamePlayerRepository.save(gamePlayer);

            return new ResponseEntity<>(createMap("gpid", gamePlayer.getGamePlayerId()), HttpStatus.CREATED);
        }
    }

    @RequestMapping(path = "/api/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String name, @RequestParam String email, @RequestParam String password) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        if (playerRepository.findByEmail(email) != null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }
        playerRepository.save(new Player(name, email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @RequestMapping(value = "/api/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addShips(@PathVariable Long gamePlayerId, @RequestBody Set<Ship> ships, Authentication authentication){
        GamePlayer gamePlayer = gamePlayerRepository.getOne(gamePlayerId);
        Player player = getAuthPlayer(authentication);

        if (isGuest(authentication) || gamePlayer == null || gamePlayerRepository.getOne(gamePlayerId).getPlayer().getPlayerId() != player.getPlayerId()){
            return new ResponseEntity<>(createMap("Error", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayerRepository.getOne(gamePlayerId).getShips().size() > 1){
            return new ResponseEntity<>(createMap("Error", "You already placed the ships"), HttpStatus.FORBIDDEN);
        }
        for (Ship ship : ships) {
            ship.setGamePlayer(gamePlayer);
            shipRepository.save(ship);
        }
        return new ResponseEntity<>(createMap("Success", "Successfully placed"), HttpStatus.CREATED);
    }

    private Map<String, Object> createMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    public Player getAuthPlayer(Authentication authentication) {
        return playerRepository.findByEmail(authentication.getName());
    }

    private Map<String, Object> makeScoreDTO(Score score) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("player", score.getPlayer().getName());
        dto.put("score", score.getScore());
        return dto;
    }

    private Map<Integer, List<String>> makeTurnDTO (Salvo salvo){
        Map<Integer, List<String>> dto = new LinkedHashMap<>();
        List<String> shotsHit = new ArrayList<>();
        for (Ship ship : opponentPlayer(salvo.getGamePlayer()).getShips()) {
            for (String loc : ship.getShipLocations()) {
                if (salvo.getSalvoLocations().contains(loc)) {
                    shotsHit.add(loc);
                }
            }
        }
        dto.put(salvo.getTurn(), shotsHit);
        return dto;
    }

    private Map<String, List<String>> makeSunkDTO(Set<Salvo> salvos, GamePlayer gamePlayer){
        Map<String, List<String>> dto = new LinkedHashMap<>();
        List<String> sunkShip = new ArrayList<>();
        for ( Ship ship : opponentPlayer(gamePlayer).getShips()) {
            int shipSize = ship.getShipLocations().size();
            for (Salvo salvo : salvos.stream().sorted(Comparator.comparing(Salvo::getTurn)).collect(Collectors.toList())){
                for (String loc : ship.getShipLocations()) {
                    if (salvo.getSalvoLocations().contains(loc)){
                        shipSize = shipSize -1;
                        if(shipSize == 0){
                            sunkShip.add(ship.getShipType());
                        }
                    }
                }
            }
        }
        dto.put("sunk",  sunkShip);
        return dto;
    }


    private Map<String, Object> makeLogicDTO(GamePlayer gamePlayer){
        Map<String, Object> dto = new LinkedHashMap<>();
        if (opponentPlayer(gamePlayer) == null){
            dto.put("Logic", "Waiting for opponent to join...");
        } else{
            dto.put("Logic", "place the ships");
            if (opponentPlayer(gamePlayer).getShips().size() == 0){
                dto.put("Logic", "Waiting for opponent to place ships...");
            } else {
                if(gamePlayer.getShips().size() == opponentPlayer(gamePlayer).getShips().size()){
                    dto.put("Logic", "It is your turn to shoot!");
                }
                if (gamePlayer.getSalvos().size() > opponentPlayer(gamePlayer).getSalvos().size()){
                    dto.put("Logic", "Waiting for opponent to shoot...");
                }
                if ((makeSunkDTO(gamePlayer.getSalvos(), gamePlayer).get("sunk").size() == 5)
                        && gamePlayer.getSalvos().size() == opponentPlayer(gamePlayer).getSalvos().size()){
                    dto.put("Logic", "You won!");
                    if(gamePlayer.getPlayer().updateScore(gamePlayer.getGame())== null) {
                        Score newScore = new Score(1.0, gamePlayer.getPlayer(), gamePlayer.getGame());
                        scoreRepository.save(newScore);
                    }
                }
                if ((makeSunkDTO(opponentPlayer(gamePlayer).getSalvos(), opponentPlayer(gamePlayer)).get("sunk").size() == 5)
                        && gamePlayer.getSalvos().size() == opponentPlayer(gamePlayer).getSalvos().size()){
                    dto.put("Logic", "You lost");
                    if(gamePlayer.getPlayer().updateScore(gamePlayer.getGame())== null) {
                        Score newScore = new Score(0.0, gamePlayer.getPlayer(), gamePlayer.getGame());
                        scoreRepository.save(newScore);
                    }
                }
                if ((makeSunkDTO(gamePlayer.getSalvos(), gamePlayer).get("sunk").size() == 5)
                        && (makeSunkDTO(opponentPlayer(gamePlayer).getSalvos(), opponentPlayer(gamePlayer)).get("sunk").size() == 5)
                        && gamePlayer.getSalvos().size() == opponentPlayer(gamePlayer).getSalvos().size()){
                    dto.put("Logic", "Its a draw");
                    if(gamePlayer.getPlayer().updateScore(gamePlayer.getGame())== null){
                        Score newScore = new Score(0.5, gamePlayer.getPlayer(), gamePlayer.getGame());
                        scoreRepository.save(newScore);
                        Score oppScore = new Score(0.5, opponentPlayer(gamePlayer).getPlayer(), opponentPlayer(gamePlayer).getGame());
                        scoreRepository.save(oppScore);
                    }
                }

            }
        }
        return dto;
    }
}