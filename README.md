# Hotel UI Service

🏨 **UI микросервис для отельной системы управления с HTML+JS интерфейсом**

## 📋 Описание

Веб-интерфейс для системы управления отелями, который взаимодействует с микросервисом пользователей через Feign Client. Предоставляет удобный HTML интерфейс для регистрации, авторизации и управления пользователями.

## ⚡ Основной функционал

- 🔐 **Авторизация и регистрация** пользователей
- 👤 **Личный кабинет** с профилем пользователя  
- 👥 **Админская панель** для управления пользователями
- 🎫 **JWT аутентификация** с токенами в cookies
- 📱 **Responsive HTML формы** без фреймворков

## 🛠 Технологии

- **Backend**: Spring Boot 3.5, Spring Security, Thymeleaf
- **Frontend**: HTML5, Vanilla JavaScript
- **Integration**: OpenFeign Client для связи с User Service
- **Auth**: JWT токены, Cookie-based sessions
- **Build**: Maven, Java 17

## 🚀 Запуск

```bash
# Клонировать репозиторий
git clone https://github.com/your-username/hotel-ui-service.git

# Перейти в директорию
cd hotel-ui-service

# Запустить User Service на порту 1511
# (обязательно должен быть запущен)

# Запустить UI Service
mvn spring-boot:run
```

Приложение будет доступно по адресу: `http://localhost:3000`

## 🔗 Связанные проекты

- [Hotel User Service](https://github.com/your-username/hotel-user-service) - Микросервис управления пользователями

## 📱 Страницы

- `/` - Главная страница
- `/auth/login` - Вход в систему
- `/auth/register` - Регистрация
- `/dashboard` - Личный кабинет
- `/admin/users` - Управление пользователями (только для админов)

---

*Часть микросервисной архитектуры отельной системы управления*
