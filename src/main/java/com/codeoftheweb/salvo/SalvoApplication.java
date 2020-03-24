package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;

@SpringBootApplication
public class SalvoApplication {
	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
		return (args) -> {
			//Game creation
			Game game1 = new Game(new Date()); //Create a new game with the actual date
			Game game2 = new Game(Date.from(game1.getDate().toInstant().plusSeconds(3600))); //create a new game with date 1h later.
			Game game3 = new Game(Date.from(game1.getDate().toInstant().plusSeconds(7200)));
			Game game4 = new Game(Date.from(game1.getDate().toInstant().plusSeconds(10800)));
			Game game5 = new Game(Date.from(game1.getDate().toInstant().plusSeconds(14400)));
			Game game6 = new Game(Date.from(game1.getDate().toInstant().plusSeconds(18000)));
			Game game7 = new Game(Date.from(game1.getDate().toInstant().plusSeconds(21600)));
			Game game8 = new Game(Date.from(game1.getDate().toInstant().plusSeconds(25200)));
			gameRepository.save(game1);
			gameRepository.save(game2);
			gameRepository.save(game3);
			gameRepository.save(game4);
			gameRepository.save(game5);
			gameRepository.save(game6);
			gameRepository.save(game7);
			gameRepository.save(game8);

			//players
			Player player1 = new Player("j.bauer@ctu.gov", "Jack Bauer", passwordEncoder().encode("24"));
			Player player2 = new Player("c.obrian@ctu.gov", "Chloe O'Brian", passwordEncoder().encode("42"));
			Player player3 = new Player("kim_bauer@gmail.com", "Kim Bauer", passwordEncoder().encode("kb"));
			Player player4 = new Player("t.almeida@ctu.gov", "Tony Almeida", passwordEncoder().encode("mole"));
			playerRepository.save(player1);
			playerRepository.save(player2);
			playerRepository.save(player3);
			playerRepository.save(player4);

			//game 1
			GamePlayer gamePlayer = new GamePlayer(new Date(), game1, player1);
			GamePlayer gamePlayer1 = new GamePlayer(new Date(), game1, player2);
			gamePlayerRepository.save(gamePlayer);
			gamePlayerRepository.save(gamePlayer1);

			//game 2
			GamePlayer gamePlayer2 = new GamePlayer(new Date(), game2, player1);
			GamePlayer gamePlayer3 = new GamePlayer(new Date(), game2, player2);
			gamePlayerRepository.save(gamePlayer2);
			gamePlayerRepository.save(gamePlayer3);

			//game 3
			GamePlayer gamePlayer4 = new GamePlayer(new Date(), game3, player2);
			GamePlayer gamePlayer5 = new GamePlayer(new Date(), game3, player4);
			gamePlayerRepository.save(gamePlayer4);
			gamePlayerRepository.save(gamePlayer5);

			//game 4
			GamePlayer gamePlayer6 = new GamePlayer(new Date(), game4, player2);
			GamePlayer gamePlayer7 = new GamePlayer(new Date(), game4, player1);
			gamePlayerRepository.save(gamePlayer6);
			gamePlayerRepository.save(gamePlayer7);

			//game5
			GamePlayer gamePlayer8 = new GamePlayer(new Date(), game5, player4);
			GamePlayer gamePlayer9 = new GamePlayer(new Date(), game5, player1);
			gamePlayerRepository.save(gamePlayer8);
			gamePlayerRepository.save(gamePlayer9);

			//game6
			GamePlayer gamePlayer10 = new GamePlayer(new Date(), game6, player3);
			gamePlayerRepository.save(gamePlayer10);

			//game7
			GamePlayer gamePlayer12 = new GamePlayer(new Date(), game7, player4);
			GamePlayer gamePlayer13 = new GamePlayer(new Date(), game7, player3);
			gamePlayerRepository.save(gamePlayer12);
			gamePlayerRepository.save(gamePlayer13);

			//game8
			GamePlayer gamePlayer14 = new GamePlayer(new Date(), game8, player3);
			GamePlayer gamePlayer15 = new GamePlayer(new Date(), game8, player4);
			gamePlayerRepository.save(gamePlayer14);
			gamePlayerRepository.save(gamePlayer15);

			//ships
			Ship ship1 = new Ship("Destroyer", Arrays.asList("H2", "H3", "H4"), gamePlayer);
			Ship ship2 = new Ship("Submarine", Arrays.asList("E1", "F1", "G1"), gamePlayer);
			Ship ship3 = new Ship("Patrol Boat", Arrays.asList("B4", "B5"), gamePlayer);
			Ship ship4 = new Ship("Destroyer", Arrays.asList("B5", "C5", "D5"), gamePlayer1);
			Ship ship5 = new Ship("Patrol Boat", Arrays.asList("F1", "F2"), gamePlayer1);
			Ship ship6 = new Ship("Destroyer", Arrays.asList("B5", "C5", "D5"), gamePlayer2);
			Ship ship7 = new Ship("Patrol Boat", Arrays.asList("C6", "C7"), gamePlayer2);
			Ship ship8 = new Ship("Submarine", Arrays.asList("A2", "A3", "A4"), gamePlayer3);
			Ship ship9 = new Ship("Patrol Boat", Arrays.asList("G6", "H6"), gamePlayer3);
			Ship ship10 = new Ship("Destroyer", Arrays.asList("B5", "C5", "D5"), gamePlayer4);
			Ship ship11 = new Ship("Patrol Boat", Arrays.asList("C6", "C7"), gamePlayer4);
			Ship ship12 = new Ship("Submarine", Arrays.asList("A2", "A3", "A4"), gamePlayer5);
			Ship ship13 = new Ship("Patrol Boat", Arrays.asList("G6", "H6"), gamePlayer5);
			Ship ship14 = new Ship("Destroyer", Arrays.asList("B5", "C5", "D5"), gamePlayer6);
			Ship ship15 = new Ship("Patrol Boat", Arrays.asList("C6", "C7"), gamePlayer6);
			Ship ship16 = new Ship("Submarine", Arrays.asList("A2", "A3", "A4"), gamePlayer7);
			Ship ship17 = new Ship("Patrol Boat", Arrays.asList("G6", "H6"), gamePlayer7);
			Ship ship18 = new Ship("Destroyer", Arrays.asList("B5", "C5", "D5"), gamePlayer8);
			Ship ship19 = new Ship("Patrol Boat", Arrays.asList("C6", "C7"), gamePlayer8);
			Ship ship20 = new Ship("Submarine", Arrays.asList("A2", "A3", "A4"), gamePlayer9);
			Ship ship21 = new Ship("Patrol Boat", Arrays.asList("G6", "H6"), gamePlayer9);
			Ship ship22 = new Ship("Destroyer", Arrays.asList("B5", "C5", "D5"), gamePlayer10);
			Ship ship23 = new Ship("Patrol Boat", Arrays.asList("C6", "C7"), gamePlayer10);
			Ship ship24 = new Ship("Destroyer", Arrays.asList("B5", "C5", "D5"), gamePlayer14);
			Ship ship25 = new Ship("Patrol Boat", Arrays.asList("C6", "C7"), gamePlayer14);
			Ship ship26 = new Ship("Submarine", Arrays.asList("A2", "A3", "A4"), gamePlayer15);
			Ship ship27 = new Ship("Patrol Boat", Arrays.asList("G6", "H6"), gamePlayer15);
			shipRepository.save(ship1);
			shipRepository.save(ship2);
			shipRepository.save(ship3);
			shipRepository.save(ship4);
			shipRepository.save(ship5);
			shipRepository.save(ship6);
			shipRepository.save(ship7);
			shipRepository.save(ship8);
			shipRepository.save(ship9);
			shipRepository.save(ship10);
			shipRepository.save(ship11);
			shipRepository.save(ship12);
			shipRepository.save(ship13);
			shipRepository.save(ship14);
			shipRepository.save(ship15);
			shipRepository.save(ship16);
			shipRepository.save(ship17);
			shipRepository.save(ship18);
			shipRepository.save(ship19);
			shipRepository.save(ship20);
			shipRepository.save(ship21);
			shipRepository.save(ship22);
			shipRepository.save(ship23);
			shipRepository.save(ship24);
			shipRepository.save(ship25);
			shipRepository.save(ship26);
			shipRepository.save(ship27);

			//salvo
			//TURN 1  in GAME 1
			Salvo salvo = new Salvo(gamePlayer, Arrays.asList("B5", "C5", "F1"), 1);
			Salvo salvo1 = new Salvo(gamePlayer1, Arrays.asList("B4", "B5", "B6"), 1);

			//TURN 2 in GAME 1
			Salvo salvo2 = new Salvo(gamePlayer, Arrays.asList("F2", "D5"), 2);
			Salvo salvo3 = new Salvo(gamePlayer1, Arrays.asList("E1", "H3", "A2"), 2);

			//TURN 1 in GAME 2
			Salvo salvo4 = new Salvo(gamePlayer2, Arrays.asList("A2", "A4", "G6"), 1);
			Salvo salvo5 = new Salvo(gamePlayer3, Arrays.asList("B5", "D5", "C7"), 1);

			//TURN 2 in GAME 2
			Salvo salvo6 = new Salvo(gamePlayer2, Arrays.asList("A3", "H6"), 2);
			Salvo salvo7 = new Salvo(gamePlayer3, Arrays.asList("C5", "C6"), 2);

			//TURN 1 in GAME 3
			Salvo salvo8 = new Salvo(gamePlayer4, Arrays.asList("G6", "H6", "A4"), 1);
			Salvo salvo9 = new Salvo(gamePlayer5, Arrays.asList("H1", "H2", "H3"), 1);

			//TURN 2 in GAME 3
			Salvo salvo10 = new Salvo(gamePlayer4, Arrays.asList("A2", "A3", "D8"), 2);
			Salvo salvo11 = new Salvo(gamePlayer5, Arrays.asList("E1", "F2", "G3"), 2);

			//TURN 1 in GAME 4
			Salvo salvo12 = new Salvo(gamePlayer6, Arrays.asList("A3", "A4", "F7"), 1);
			Salvo salvo13 = new Salvo(gamePlayer7, Arrays.asList("B5", "C6", "H1"), 1);

			//TURN 2 in GAME 4
			Salvo salvo14 = new Salvo(gamePlayer6, Arrays.asList("A2", "G6", "H6"), 2);
			Salvo salvo15 = new Salvo(gamePlayer7, Arrays.asList("C5", "C7", "D5"), 2);

			//TURN 1 in GAME 5
			Salvo salvo16 = new Salvo(gamePlayer8, Arrays.asList("A1", "A2", "A3"), 1);
			Salvo salvo17 = new Salvo(gamePlayer9, Arrays.asList("B5", "B6", "C7"), 1);

			//TURN 2 in GAME 5
			Salvo salvo18 = new Salvo(gamePlayer8, Arrays.asList("G6", "G7", "G8"), 2);
			Salvo salvo19 = new Salvo(gamePlayer9, Arrays.asList("C6", "D6", "E6"), 2);

			//TURN 3 in GAME 5
			Salvo salvo20 = new Salvo(gamePlayer9, Arrays.asList("H1", "H8"), 3);

			salvoRepository.save(salvo);
			salvoRepository.save(salvo1);
			salvoRepository.save(salvo2);
			salvoRepository.save(salvo3);
			salvoRepository.save(salvo4);
			salvoRepository.save(salvo5);
			salvoRepository.save(salvo6);
			salvoRepository.save(salvo7);
			salvoRepository.save(salvo8);
			salvoRepository.save(salvo9);
			salvoRepository.save(salvo10);
			salvoRepository.save(salvo11);
			salvoRepository.save(salvo12);
			salvoRepository.save(salvo13);
			salvoRepository.save(salvo14);
			salvoRepository.save(salvo15);
			salvoRepository.save(salvo16);
			salvoRepository.save(salvo17);
			salvoRepository.save(salvo18);
			salvoRepository.save(salvo19);
			salvoRepository.save(salvo20);


			//SCORE
			//winner: game 1
			Score score = new Score(2.00, player1, game1);
			Score score1 = new Score(0.00, player2, game1);

			//tie: game 2
			Score score2 = new Score(0.5, player1, game2);
			Score score3 = new Score(0.5, player2, game2);

			//winner: game 3
			Score score4 = new Score(1.00, player2, game3);
			Score score5 = new Score(0.00, player4, game3);

			//tie: game 4
			Score score6 = new Score(0.5, player1, game4);
			Score score7 = new Score(0.5, player2, game4);

			scoreRepository.save(score);
			scoreRepository.save(score1);
			scoreRepository.save(score2);
			scoreRepository.save(score3);
			scoreRepository.save(score4);
			scoreRepository.save(score5);
			scoreRepository.save(score6);
			scoreRepository.save(score7);
		};
	}
}
	@Configuration
	class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
		@Autowired
		PlayerRepository playerRepository;

		@Override
		public void init(AuthenticationManagerBuilder auth) throws Exception{
			auth.userDetailsService(email-> {
				Player player = playerRepository.findByEmail(email);
				if (player != null) {
					return new User(player.getEmail(), player.getPassword(),
							AuthorityUtils.createAuthorityList("USER"));
				} else {
					throw new UsernameNotFoundException("Unknown user: " + email);
				}
			});
		}
	}

	@Configuration
	@EnableWebSecurity
	class WebSecurityConfig extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception{
			http.authorizeRequests()
					.antMatchers("/api/games").permitAll()
					.antMatchers("/api/leaderBoard").permitAll()
					.antMatchers("/api/players").permitAll()
					.antMatchers("/api/register").permitAll()
					.antMatchers("/h2-console/**").permitAll()
					.antMatchers("/api/game_view/{Id}").hasAuthority("USER")
					.antMatchers("/rest/*").hasAuthority("ADMIN")
					.anyRequest().fullyAuthenticated()
					.and()
					.formLogin()
					.usernameParameter("email")
					.passwordParameter("password")
					.loginPage("/api/login");
				http.logout().logoutUrl("/api/logout");

				http.csrf().disable();
				http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED));
				http.formLogin().successHandler((request, response, authentication) -> clearAuthenticationAttribute(request));
				http.formLogin().failureHandler((request, response, exception) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED));
				http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
			http.headers().frameOptions().disable();


		}

		private void clearAuthenticationAttribute(HttpServletRequest request){
			HttpSession session =request.getSession(false);
			if(session != null){
				session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
			}
		}
	}
