package project.carsharing.service.api;

import java.util.Optional;
import java.util.regex.Pattern;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import project.carsharing.model.User;
import project.carsharing.repository.UserRepository;
import project.carsharing.util.PatternUtil;

@Log4j2
@Component
public class TelegramBotApi extends TelegramLongPollingBot {
    @Value("${telegram.bot.name}")
    private String botName;
    private boolean hasStarted = false;
    private final UserRepository userRepository;
    
    @Autowired
    public TelegramBotApi(@Value("${telegram.bot.token}") String botToken,
                          UserRepository userRepository) {
        super(botToken);
        this.userRepository = userRepository;
    }
    
    @Override
    public String getBotUsername() {
        return botName;
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            String text = update.getMessage().getText();
            if (text.equals("/start")) {
                hasStarted = sendStartMessage(update);
            } else if (hasStarted) {
                hasStarted = checkEmailAndSaveChatId(update);
            }
        }
    }
    
    private boolean sendStartMessage(Update update) {
        Long chatId = update.getMessage().getChatId();
        Optional<User> user = userRepository.findByTelegramChatId(chatId);
        String text = user.isPresent()
                              ? "You are already receiving notifications from this bot!"
                              : "Please enter your email";
        sendMessage(chatId, text);
        return user.isEmpty();
    }
    
    private boolean checkEmailAndSaveChatId(Update update) {
        String email = update.getMessage().getText();
        Optional<User> user = userRepository.findByEmail(email);
        String text = !Pattern.compile(PatternUtil.EMAIL_PATTERN).matcher(email).matches()
                        ? "Invalid email format"
                        : user.isEmpty()
                                  ? "There is no user with this email"
                                  : user.get().getRole().equals(User.Role.CUSTOMER)
                        ? "You don't have permission to receive notifications from this bot"
                        : user.get().getTelegramChatId() != null
                                  ? "A user with such an email receives "
                                            + "a notification on another profile"
                                  : saveChatId(user.get(), update.getMessage().getChatId(), email);
        sendMessage(update.getMessage().getChatId(), text);
        return !text.startsWith("Hello");
    }
    
    private String saveChatId(User user, Long chatId, String email) {
        userRepository.save(user.setTelegramChatId(chatId));
        log.info("User with email {} will now be able "
                          + "to receive notifications from the Telegram bot", email);
        return "Hello, " + user.getFirstName() + "! You are already receiving "
                       + "notifications from this bot!";
    }
    
    public void sendMessage(long chatId, String text) {
        SendMessage message = SendMessage.builder()
                                      .chatId(chatId)
                                      .text(text)
                                      .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.warn(e.getMessage() + " Chat id is {}", chatId);
        }
    }
}
