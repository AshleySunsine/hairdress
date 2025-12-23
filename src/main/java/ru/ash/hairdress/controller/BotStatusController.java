package ru.ash.hairdress.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bot")
public class BotStatusController {

    @GetMapping("/status")
    public String getBotStatus() {
        return """
            {
                "status": "active",
                "service": "Telegram User List Bot",
                "description": "Бот для отображения списка пользователей из базы данных",
                "endpoints": {
                    "telegram": "Команды /start, /users, /help",
                    "api": "/api/users"
                }
            }
            """;
    }
}
