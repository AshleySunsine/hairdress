В этом чате будем разрабатывать проект 
"Система учёта клиентов для парикмахеров с использованием чат-бота телеграмм". 
Проект будет использовать Spring boot, postgres, java 17. А так же Telegram API
┌─────────────────────────────────────────────────────────────────┐
│                    HAIRDRESS CLIENT SYSTEM                      │
├─────────────────────────────────────────────────────────────────┤
│  Spring Boot 3.x | Java 17 | PostgreSQL | Telegram Bot API      │
└─────────────────────────────────────────────────────────────────┘

                               │
┌─────────────────────────────────────────────────────────────────┐
│                         СТРУКТУРА ПРОЕКТА                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ru.ash.hairdress/                                              │
│  ├── HairdressApplication.java          # Точка входа           │
│  ├── configuration/                                             │
│  │   └── TelegramBotConfig.java         # Конфиг бота           │
│  ├── controller/                                                │
│  │   ├── BotStatusController.java       # REST для статуса      │
│  │   └── UserController.java            # REST API для User     │
│  ├── model/                                                     │
│  │   └── User.java                     # JPA-сущность           │
│  ├── repository/                                                │
│  │   └── UserRepository.java           # Spring Data JPA        │
│  ├── service/                                                   │
│  │   └── UserService.java              # Бизнес-логика          │
│  └── bot/                                                       │
│      ├── UserListBot.java              # Основной класс бота    │
│      ├── sender/                                                │
│      │   └── MessageSender.java        # Отправка сообщений     │
│      └── dispatcher/                                            │
│          ├── service/                                           │
│          │   └── UserCreationDialogService # Диалог создания    │
│          ├── handlers/                                          │
│          │   ├── command/               # Текстовые команды     │
│          │   │   ├── CommandDispatcher  # Маршрутизатор команд  │
│          │   │   ├── CommandHandler     # Интерфейс             │
│          │   │   ├── StartCommandHandler                        │
│          │   │   ├── UsersCommandHandler                        │
│          │   │   ├── HelpCommandHandler                         │
│          │   │   └── UnknownCommandHandler                      │
│          │   └── callback/              # Inline-кнопки         │
│          │       ├── CallbackDispatcher # Маршрутизатор кнопок  │
│          │       ├── CallbackHandler    # Интерфейс             │
│          │       ├── CreateUserCallbackHandler                  │
│          │       ├── DeleteUserCallbackHandler                  │
│          │       └── UnknownCallbackHandler                     │
│          └── UserCreationStateService   # Хранение состояния    │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│                        ПОТОК ДАННЫХ                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. Telegram → UserListBot → CommandDispatcher → Handlers       │
│  2. Handlers → UserService → UserRepository → PostgreSQL        │
│  3. Callback → CallbackDispatcher → DialogService → UserService │
│  4. UserCreationStateService хранит состояние диалогов          │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│                     КОНФИГУРАЦИЯ                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  resources/                                                     │
│  ├── application.properties     # Основные настройки            │
│  ├── secrets.properties         # bot.token, bot.username       │
│  └── liquibase/changeset/       # Миграции БД                   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘

Общая архитектура диспетчеризации
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   UserListBot   │    │   Dispatchers   │    │    Handlers     │
│                 │    │                 │    │                 │
│  onUpdate()     │───▶│   dispatch()    │───▶│    handle()     │
│                 │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
│                         │                        │
│ Update                  │ Команда/               │ Бизнес-
│ (Message/               │ CallbackData           │ логика
│  CallbackQuery)         │                        │
▼                         ▼                        ▼

Компоненты:
UserListBot - принимает Update от Telegram;
CommandDispatcher - маршрутизирует текстовые команды;
CallbackDispatcher - маршрутизирует нажатия inline-кнопок;
Handlers - обрабатывают команды/callback (реализуют интерфейсы);
UserCreationDialogService - управляет многошаговыми диалогами;

2. Поток данных:
   Для текстовых команд:
   Telegram → UserListBot → CommandDispatcher → CommandHandler → UserService
   (onUpdate)      (dispatch)         (handle)         (бизнес-логика)
   Для callback-кнопок:
   Telegram → UserListBot → CallbackDispatcher → CallbackHandler → DialogService
   (CallbackQuery)  (dispatch)          (handle)         (управление диалогом)
3. Ключевые принципы:
   Автоматическая регистрация
   Обработчики - @Component, реализующие CommandHandler/CallbackHandler;
   Spring автоматически собирает их в List<> и передаёт в диспетчеры;
   Диспетчер создает Map<Команда, Обработчик> при инициализации;
Приоритет диалогов:
Сначала проверка: dialogService.isUserInDialog(chatId);
Если в диалоге - ввод обрабатывается как шаг диалога;
Иначе - как обычная команда;

Fallback-обработчики:
UnknownCommandHandler для нераспознанных команд;
UnknownCallbackHandler для нераспознанных callback;

4. Пример диалога создания пользователя:
   /start → StartCommandHandler (показывает кнопки)

Нажатие "Добавить" (callback="CREATE_USER") → CreateUserCallbackHandler;
UserCreationDialogService.startDialog() - начало диалога;
"Иван" → CommandDispatcher → dialogService.processInput() (сохраняет имя);
"email@test.ru" → CommandDispatcher → dialogService.processInput() (сохраняет email, создаёт пользователя);
