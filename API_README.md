# Yoga Studio API Documentation

## 🌐 Base URL
- **Production**: `https://alexeykochetov.com/api`
- **Development**: `http://alexeykochetov.com:18080/api`

## 🔐 Authentication
Все API endpoints требуют аутентификации через JWT токен в заголовке `Authorization: Bearer <token>`

---

## 📋 API Endpoints

### 🔑 Authentication (`/api/auth`)

#### POST `/api/auth/register`
Регистрация нового пользователя
```json
{
  "username": "string",
  "email": "string",
  "password": "string"
}
```
**Response**: `AuthResponse` с JWT токеном

#### POST `/api/auth/login`
Вход в систему
```json
{
  "username": "string",
  "password": "string"
}
```
**Response**: `AuthResponse` с JWT токеном

---

### 👥 Clients Management (`/api/clients`)

#### GET `/api/clients`
Получить всех клиентов (с пагинацией)
**Query Parameters**:
- `page` (int, default: 0) - номер страницы
- `size` (int, default: 20) - размер страницы
- `sort` (string) - сортировка (например: `name,asc`)

#### GET `/api/clients/search`
Поиск клиентов
**Query Parameters**:
- `name` (string, optional) - поиск по имени
- `phone` (string, optional) - поиск по телефону
- `email` (string, optional) - поиск по email
- `page`, `size`, `sort` - параметры пагинации

#### GET `/api/clients/{id}`
Получить клиента по ID

#### POST `/api/clients`
Создать нового клиента
```json
{
  "name": "string",
  "email": "string",
  "phone": "string",
  "dateOfBirth": "2023-01-01",
  "address": "string"
}
```

#### PUT `/api/clients/{id}`
Обновить клиента
```json
{
  "name": "string",
  "email": "string",
  "phone": "string",
  "dateOfBirth": "2023-01-01",
  "address": "string"
}
```

#### DELETE `/api/clients/{id}`
Удалить клиента

---

### 📅 Schedule Management (`/api/schedule`)

#### Classes (Занятия)

##### GET `/api/schedule/classes`
Получить все занятия (с пагинацией)

##### GET `/api/schedule/classes/range`
Получить занятия в диапазоне дат
**Query Parameters**:
- `startDate` (ISO DateTime) - начальная дата
- `endDate` (ISO DateTime) - конечная дата

##### GET `/api/schedule/classes/{id}`
Получить занятие по ID

##### POST `/api/schedule/classes`
Создать новое занятие
```json
{
  "name": "string",
  "description": "string",
  "startTime": "2023-12-01T10:00:00",
  "endTime": "2023-12-01T11:00:00",
  "instructorId": 1,
  "maxParticipants": 20,
  "price": 1000.0
}
```

##### PUT `/api/schedule/classes/{id}`
Обновить занятие

##### DELETE `/api/schedule/classes/{id}`
Удалить занятие

#### Attendance (Посещаемость)

##### GET `/api/schedule/classes/{classId}/attendances`
Получить посещаемость занятия

##### GET `/api/schedule/clients/{clientId}/attendances`
Получить посещаемость клиента (с пагинацией)

##### POST `/api/schedule/attendances`
Создать запись о посещении
```json
{
  "clientId": 1,
  "classId": 1,
  "status": "PRESENT",
  "notes": "string"
}
```

##### PUT `/api/schedule/attendances/{id}`
Обновить запись о посещении

##### DELETE `/api/schedule/attendances/{id}`
Удалить запись о посещении

---

### 💳 Subscriptions Management (`/api/subscriptions`)

#### Subscription Types (Типы абонементов)

##### GET `/api/subscriptions/types`
Получить все типы абонементов

##### GET `/api/subscriptions/types/{id}`
Получить тип абонемента по ID

##### POST `/api/subscriptions/types`
Создать новый тип абонемента
```json
{
  "name": "string",
  "description": "string",
  "price": 5000.0,
  "durationDays": 30,
  "classLimit": 10
}
```

##### PUT `/api/subscriptions/types/{id}`
Обновить тип абонемента

##### DELETE `/api/subscriptions/types/{id}`
Удалить тип абонемента

#### Subscriptions (Абонементы)

##### GET `/api/subscriptions`
Получить все абонементы (с пагинацией)

##### GET `/api/subscriptions/client/{clientId}`
Получить абонементы клиента (с пагинацией)

##### GET `/api/subscriptions/{id}`
Получить абонемент по ID

##### POST `/api/subscriptions`
Создать новый абонемент
```json
{
  "clientId": 1,
  "subscriptionTypeId": 1,
  "startDate": "2023-12-01",
  "endDate": "2023-12-31"
}
```

##### DELETE `/api/subscriptions/{id}`
Деактивировать абонемент

---

### 📊 Reports (`/api/reports`)

#### GET `/api/reports/attendance`
Отчет по посещаемости
**Query Parameters**:
- `startDate` (ISO DateTime) - начальная дата
- `endDate` (ISO DateTime) - конечная дата
- `clientId` (Long, optional) - ID клиента для фильтрации

#### GET `/api/reports/revenue`
Отчет по доходам
**Query Parameters**:
- `startDate` (ISO DateTime) - начальная дата
- `endDate` (ISO DateTime) - конечная дата

#### GET `/api/reports/clients`
Отчет по клиентам
**Query Parameters**:
- `startDate` (ISO DateTime) - начальная дата
- `endDate` (ISO DateTime) - конечная дата

#### GET `/api/reports/classes`
Отчет по занятиям
**Query Parameters**:
- `startDate` (ISO DateTime) - начальная дата
- `endDate` (ISO DateTime) - конечная дата

#### GET `/api/reports/dashboard`
Сводный отчет для дашборда
**Query Parameters**:
- `startDate` (ISO DateTime) - начальная дата
- `endDate` (ISO DateTime) - конечная дата

**Response**: Объект содержащий все отчеты:
```json
{
  "attendance": { /* AttendanceReportDto */ },
  "revenue": { /* RevenueReportDto */ },
  "clients": { /* ClientReportDto */ },
  "classes": { /* ClassReportDto */ }
}
```

---

## 🔧 Technical Details

### HTTP Status Codes
- `200 OK` - Успешный запрос
- `201 Created` - Ресурс создан
- `204 No Content` - Успешное удаление
- `400 Bad Request` - Некорректные данные
- `401 Unauthorized` - Требуется аутентификация
- `403 Forbidden` - Доступ запрещен
- `404 Not Found` - Ресурс не найден
- `500 Internal Server Error` - Внутренняя ошибка сервера

### CORS
API поддерживает CORS для всех origins (`*`)

### Pagination
Для endpoints с пагинацией используются стандартные Spring Data параметры:
- `page` - номер страницы (начиная с 0)
- `size` - размер страницы
- `sort` - сортировка в формате `field,direction` (например: `name,asc`)

### Date Format
Все даты передаются в ISO 8601 формате: `2023-12-01T10:00:00`

---

## 🚀 Quick Start

### 1. Регистрация
```bash
curl -X POST https://alexeykochetov.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@example.com",
    "password": "password123"
  }'
```

### 2. Вход
```bash
curl -X POST https://alexeykochetov.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }'
```

### 3. Использование с токеном
```bash
curl -X GET https://alexeykochetov.com/api/clients \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 📝 Notes

- Все endpoints требуют аутентификации кроме `/api/auth/*`
- JWT токен должен передаваться в заголовке `Authorization: Bearer <token>`
- API поддерживает пагинацию для списковых endpoints
- Все даты в ISO 8601 формате
- CORS настроен для всех origins
- API работает как через домен (без порта), так и напрямую через порт 18080
