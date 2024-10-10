package com.auth.AuthImpl.ctp.seeder;
import com.auth.AuthImpl.ctp.nenity.GameInstance;
import com.auth.AuthImpl.ctp.nenity.GameTemplate;
import com.auth.AuthImpl.ctp.repository.GameInstanceRepository;
import com.auth.AuthImpl.ctp.repository.GameTemplateRepository;
import com.auth.AuthImpl.registraion.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class GameInstanceSeeder implements CommandLineRunner {

    @Autowired
    private GameInstanceRepository gameInstanceRepository;

    @Autowired
    private GameTemplateRepository gameTemplateRepository;

    @Override
    public void run(String... args) throws Exception {
//        seedGameInstances();
    }

    private void seedGameInstances() {
        // Fetch all GameTemplates
        List<GameTemplate> gameTemplates = gameTemplateRepository.findAll();

        // Log the fetched templates
        System.out.println("Fetched GameTemplates: " + gameTemplates.size());

        for (GameTemplate gameTemplate : gameTemplates) {
            int initialActiveCount = gameTemplate.getInitialActiveTableCount();
            System.out.println("Seeding " + initialActiveCount + " GameInstances for template: " + gameTemplate.getId());

            // Loop to create game instances as per initial_active_table_count
            for (int i = 0; i < initialActiveCount; i++) {
                GameInstance gameInstance = new GameInstance();

                // Set all required fields for GameInstance
                gameInstance.setGameTemplate(gameTemplate); // Reference to GameTemplate (foreign key)
                gameInstance.setMaxBetLimit(gameTemplate.getMaxBetLimit()); // Set max bet limit from the template
                gameInstance.setCurrentBet(BigDecimal.ZERO); // Initialize current bet as 0
                gameInstance.setTotalJoinedPlayers(0); // Initialize players to 0
                gameInstance.setTotalPlayingPlayers(0); // No players playing initially
                gameInstance.setTotalWaitingPlayers(0); // No waiting players initially
                gameInstance.setCurrentPlayerIndex(0); // Initialize player index to 0
                gameInstance.setPrevPlayerIndex(-1); // Set previous player index to a default (e.g., -1 for none)
                gameInstance.setNextPlayerIndex(-1); // Set next player index to a default (e.g., -1 for none)
                gameInstance.setStartTime(LocalDateTime.now()); // Set the game start time as now
                gameInstance.setEndTime(null); // Set end time to null as the game has not ended yet

                // Set status for the instance
                gameInstance.setStatus(Status.LIVE); // Using the correct enum for status
                gameInstance.setGameStatus(GameInstance.GameStatus.active); // Set game status

                gameInstance.setCreatedBy("ADMIN"); // Set 'created_by' field
                gameInstance.setUpdatedBy("ADMIN"); // Set 'updated_by' field
                gameInstance.setCreatedAt(LocalDateTime.now()); // Set the creation time
                gameInstance.setUpdatedAt(LocalDateTime.now()); // Set the updated time

                // Save the game instance to the repository
                gameInstanceRepository.save(gameInstance);
                System.out.println("Saved GameInstance ID: " + gameInstance.getId());
            }
        }
    }

}
