package project.carsharing;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import project.carsharing.service.api.TelegramBotApi;

@SpringBootApplication
@RequiredArgsConstructor
@EnableScheduling
@Log4j2
public class CarSharingApplication {
    private final TelegramBotApi telegramBotApi;
    
    public static void main(String[] args) {
        SpringApplication.run(CarSharingApplication.class, args);
    }
    
    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBotApi);
            log.info("Telegram bot is started");
        };
    }
}
