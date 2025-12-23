package ru.ash.hairdress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class HairdressApplication {

	public static void main(String[] args) {
		SpringApplication.run(HairdressApplication.class, args);
	}

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println("🚀 Spring Boot приложение запущено!");
        System.out.println("🤖 Telegram бот инициализирован");
        System.out.println("📊 База данных подключена");
        System.out.println("🌐 Сервер доступен по адресу: http://localhost:8080");
    }

}
