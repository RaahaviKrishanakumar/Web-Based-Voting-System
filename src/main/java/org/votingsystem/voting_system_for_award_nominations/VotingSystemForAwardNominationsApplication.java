package org.votingsystem.voting_system_for_award_nominations;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class VotingSystemForAwardNominationsApplication {

    public static void main(String[] args) {
        SpringApplication.run(VotingSystemForAwardNominationsApplication.class, args);
    }

     //Temporary runner to generate a BCrypt hash
    //@Bean
  //  public CommandLineRunner runner(PasswordEncoder passwordEncoder) {
   //   return args -> {
    //       System.out.println("Hashed password: " + passwordEncoder.encode("FM123"));
     //   };
  //  }
}
