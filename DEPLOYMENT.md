# 🚀 GitHub Actions Deployment Guide

Полное руководство по настройке автоматического деплоя с помощью GitHub Actions.

## 📋 Предварительные требования

### На VPS:
- Ubuntu/Debian сервер
- Docker и Docker Compose
- SSH доступ
- Минимум 1GB RAM, 2GB свободного места

### На GitHub:
- Репозиторий с кодом
- Доступ к настройкам репозитория

## 🔧 Шаг 1: Подготовка VPS

### 1.1 Подключитесь к серверу
```bash
ssh root@your-server-ip
```

### 1.2 Запустите скрипт настройки
```bash
# Скачайте и запустите скрипт
curl -O https://raw.githubusercontent.com/ShurupuS/yoga-studio-app/main/setup-vps.sh
chmod +x setup-vps.sh
./setup-vps.sh
```

### 1.3 Настройте переменные окружения
```bash
nano /opt/yoga-studio/.env
```

**Содержимое .env:**
```bash
POSTGRES_PASSWORD=your_very_secure_password_123
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production-123456789
```

## 🔑 Шаг 2: Настройка SSH ключей

### 2.1 Сгенерируйте SSH ключ для GitHub Actions
```bash
# На VPS
ssh-keygen -t rsa -b 4096 -C "github-actions" -f /home/github-actions/.ssh/id_rsa

# Добавьте публичный ключ в authorized_keys
cat /home/github-actions/.ssh/id_rsa.pub >> /home/github-actions/.ssh/authorized_keys
chmod 600 /home/github-actions/.ssh/authorized_keys
chown -R github-actions:github-actions /home/github-actions/.ssh
```

### 2.2 Получите приватный ключ
```bash
# На VPS - скопируйте этот ключ
cat /home/github-actions/.ssh/id_rsa
```

## 🔐 Шаг 3: Настройка GitHub Secrets

Перейдите в настройки репозитория: `Settings` → `Secrets and variables` → `Actions`

### Добавьте следующие секреты:

| Secret Name | Value | Описание |
|-------------|-------|----------|
| `VPS_HOST` | `your-server-ip` | IP адрес вашего VPS |
| `VPS_USERNAME` | `github-actions` | Пользователь для SSH |
| `VPS_SSH_KEY` | `-----BEGIN OPENSSH PRIVATE KEY-----...` | Приватный SSH ключ |
| `VPS_PORT` | `22` | SSH порт (обычно 22) |

## 🚀 Шаг 4: Тестирование деплоя

### 4.1 Сделайте коммит и пуш
```bash
git add .
git commit -m "Add GitHub Actions deployment"
git push origin main
```

### 4.2 Проверьте статус деплоя
1. Перейдите в `Actions` вкладку на GitHub
2. Найдите workflow "Deploy to VPS"
3. Проверьте логи выполнения

### 4.3 Проверьте работу приложения
```bash
# На VPS
curl http://localhost:18080/actuator/health

# Снаружи
curl http://your-server-ip:18080/actuator/health
```

## 📊 Шаг 5: Мониторинг и логи

### 5.1 Просмотр логов приложения
```bash
# На VPS
docker-compose logs -f app
```

### 5.2 Мониторинг здоровья
```bash
# На VPS - автоматическая проверка каждые 5 минут
tail -f /opt/yoga-studio/logs/health.log
```

### 5.3 Статус сервисов
```bash
# На VPS
docker-compose ps
```

## 🔄 Workflow файлы

### `deploy.yml` - Основной деплой
- Запускается при пуше в `main`
- Собирает приложение
- Деплоит на VPS
- Проверяет здоровье

### `test.yml` - Тестирование
- Запускается при PR и пуше
- Запускает тесты
- Собирает приложение

### `backup.yml` - Бэкапы
- Ежедневные бэкапы БД в 2:00 AM
- Хранит 7 дней локально
- Загружает в GitHub Artifacts

## 🛠️ Управление деплоем

### Ручной запуск деплоя
1. Перейдите в `Actions` → `Deploy to VPS`
2. Нажмите `Run workflow`
3. Выберите ветку и запустите

### Откат к предыдущей версии
```bash
# На VPS
cd /opt/yoga-studio
git log --oneline  # Найдите нужный коммит
git checkout <commit-hash>
docker-compose up -d --build
```

### Остановка сервисов
```bash
# На VPS
docker-compose down
```

## 🚨 Устранение проблем

### Проблема: Деплой не запускается
- Проверьте GitHub Secrets
- Проверьте SSH подключение
- Проверьте права доступа

### Проблема: Приложение не запускается
- Проверьте логи: `docker-compose logs app`
- Проверьте память: `docker stats`
- Проверьте порты: `netstat -tulpn`

### Проблема: Медленная работа
- Увеличьте память VPS
- Оптимизируйте JVM настройки
- Проверьте использование ресурсов

## 📈 Рекомендации

1. **Мониторинг**: Настройте уведомления о падении сервиса
2. **Бэкапы**: Регулярно проверяйте бэкапы БД
3. **Обновления**: Следите за обновлениями зависимостей
4. **Безопасность**: Регулярно обновляйте пароли и ключи

## 🎯 Результат

После настройки у вас будет:
- ✅ Автоматический деплой при каждом пуше в main
- ✅ Автоматическое тестирование
- ✅ Ежедневные бэкапы БД
- ✅ Мониторинг здоровья приложения
- ✅ Простой откат к предыдущим версиям

Ваше приложение будет доступно по адресу: `http://your-server-ip:18080`
