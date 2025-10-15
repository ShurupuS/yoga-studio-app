# Yoga Studio Management App

Приложение для управления йога-студией с функциями учета посещаемости, абонементов и отчетности.

## 🏗️ Архитектура

- **Backend**: Kotlin + Spring Boot 3.2
- **Database**: PostgreSQL 15
- **Cache**: Redis 7
- **Containerization**: Docker + Docker Compose
- **iOS**: Swift (планируется)

## 🚀 Быстрый старт

### Предварительные требования

- Docker и Docker Compose
- Java 17+ (для локальной разработки)
- Gradle 7+ (для локальной разработки)

### Запуск через Docker

1. Клонируйте репозиторий:
```bash
git clone <repository-url>
cd ClassesRoom
```

2. Запустите все сервисы:
```bash
docker-compose up -d
```

3. Приложение будет доступно по адресу: http://localhost:8080

### Локальная разработка

1. Запустите PostgreSQL и Redis:
```bash
docker-compose up postgres redis -d
```

2. Запустите приложение:
```bash
cd backend
./gradlew bootRun
```

## 📋 Функциональность

### Для тренера:

#### 👥 Управление клиентами
- Просмотр списка клиентов
- Поиск клиентов по имени, телефону, email
- Добавление новых клиентов
- Редактирование информации о клиентах
- Деактивация клиентов

#### 💳 Управление абонементами
- Просмотр типов абонементов
- Создание новых типов абонементов
- Редактирование типов абонементов
- Просмотр абонементов клиентов
- Создание новых абонементов
- Деактивация абонементов

#### 📅 Управление расписанием
- Просмотр занятий
- Создание новых занятий
- Редактирование занятий
- Отмена занятий
- Управление посещаемостью
- Отметка присутствия/отсутствия

#### 📊 Отчетность
- Отчет по посещаемости
- Отчет по доходам
- Отчет по клиентам
- Отчет по занятиям
- Общая панель управления

## 🔐 Аутентификация

### Регистрация тренера
```bash
POST /api/auth/register
{
  "email": "trainer@yoga.com",
  "password": "password123",
  "name": "Yoga Trainer"
}
```

### Вход в систему
```bash
POST /api/auth/login
{
  "email": "trainer@yoga.com",
  "password": "password123"
}
```

### Использование токена
Добавьте заголовок в запросы:
```
Authorization: Bearer <your-jwt-token>
```

## 📚 API Endpoints

### Клиенты
- `GET /api/clients` - Список клиентов
- `GET /api/clients/search` - Поиск клиентов
- `GET /api/clients/{id}` - Получить клиента
- `POST /api/clients` - Создать клиента
- `PUT /api/clients/{id}` - Обновить клиента
- `DELETE /api/clients/{id}` - Удалить клиента

### Абонементы
- `GET /api/subscriptions/types` - Типы абонементов
- `POST /api/subscriptions/types` - Создать тип абонемента
- `GET /api/subscriptions` - Список абонементов
- `POST /api/subscriptions` - Создать абонемент

### Расписание
- `GET /api/schedule/classes` - Список занятий
- `POST /api/schedule/classes` - Создать занятие
- `GET /api/schedule/classes/{id}/attendances` - Посещаемость занятия
- `POST /api/schedule/attendances` - Отметить посещение

### Отчеты
- `GET /api/reports/attendance` - Отчет по посещаемости
- `GET /api/reports/revenue` - Отчет по доходам
- `GET /api/reports/clients` - Отчет по клиентам
- `GET /api/reports/dashboard` - Общая панель

## 🗄️ База данных

### Основные таблицы:
- `users` - Пользователи (тренеры)
- `clients` - Клиенты студии
- `subscription_types` - Типы абонементов
- `subscriptions` - Абонементы клиентов
- `classes` - Занятия
- `attendances` - Посещаемость

## 🐳 Docker

### Сервисы:
- `app` - Spring Boot приложение (порт 8080)
- `postgres` - PostgreSQL база данных (порт 5432)
- `redis` - Redis кэш (порт 6379)

### Команды:
```bash
# Запуск всех сервисов
docker-compose up -d

# Просмотр логов
docker-compose logs -f app

# Остановка сервисов
docker-compose down

# Пересборка приложения
docker-compose up --build app
```

## 🔧 Конфигурация

### Переменные окружения:
- `SPRING_DATASOURCE_URL` - URL базы данных
- `SPRING_DATASOURCE_USERNAME` - Имя пользователя БД
- `SPRING_DATASOURCE_PASSWORD` - Пароль БД
- `SPRING_DATA_REDIS_HOST` - Хост Redis
- `JWT_SECRET` - Секретный ключ для JWT

### Профили:
- `default` - Локальная разработка
- `docker` - Docker окружение

## 📱 iOS приложение

Планируется создание iOS приложения с функциями:
- Вход в систему тренера
- Управление клиентами
- Просмотр и создание занятий
- Отметка посещаемости
- Просмотр отчетов

## 🚀 Развертывание на VPS

1. Склонируйте репозиторий на сервер
2. Настройте переменные окружения
3. Запустите через Docker Compose:
```bash
docker-compose up -d
```

4. Настройте reverse proxy (nginx) для доступа к API

## 📝 Лицензия

MIT License


