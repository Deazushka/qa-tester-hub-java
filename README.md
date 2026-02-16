# QA Tester Hub - Java/Spring Boot Version

Платформа для тренировки навыков тестирования. Переписана с Flask на Spring Boot.

## Требования

- **Java 17+**
- **Maven 3.8+**

## Локальный запуск

```bash
# Сборка проекта
mvn clean package -DskipTests

# Запуск
java -jar target/qa-tester-hub-1.0.0.jar

# Или через Maven
mvn spring-boot:run
```

Приложение будет доступно на: **http://localhost:8080**

---

## ☁️ Деплой (бесплатные хостинги)

### Вариант 1: Railway (рекомендуется)

**Самый простой способ!**

1. Создайте GitHub репозиторий:
```bash
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/YOUR_USERNAME/qa-tester-hub.git
git push -u origin main
```

2. Зайдите на **https://railway.app**
3. Нажмите **New Project** → **Deploy from GitHub repo**
4. Выберите репозиторий
5. Railway автоматически определит Java и задеплоит
6. Готово! 🎉

**Управление:** Dashboard → Project → Settings → **Sleep** / **Wake**

---

### Вариант 2: Render (через Docker)

Render не поддерживает Java напрямую, но работает с Docker:

1. Создайте GitHub репозиторий (см. выше)

2. Зайдите на **https://render.com**
3. Нажмите **New** → **Web Service**
4. Подключите GitHub репозиторий
5. Настройки:
   - **Environment**: Docker
   - **Region**: Frankfurt (или ближайший)
   - **Instance Type**: Free
6. Нажмите **Deploy Web Service**

**Управление:** Dashboard → Service → **Suspend** / **Resume**

---

### Вариант 3: Fly.io

1. Установите CLI: `curl -L https://fly.io/install.sh | sh`

2. Авторизуйтесь:
```bash
fly auth login
```

3. Создайте приложение:
```bash
fly apps create qa-tester-hub
```

4. Задеплойте:
```bash
fly deploy
```

**Управление:**
```bash
# Остановить
fly apps stop qa-tester-hub

# Запустить
fly apps start qa-tester-hub
```

---

### Вариант 4: Koyeb

1. Зайдите на **https://www.koyeb.com**
2. Нажмите **Create App**
3. Выберите **GitHub** и подключите репозиторий
4. Koyeb автоматически определит Java
5. Нажмите **Deploy**

---

## 📊 Сравнение хостингов

| Хостинг | Sleep режим | Предел | Сложность |
|---------|-------------|--------|-----------|
| **Railway** | ✅ Да | $5/мес | ⭐ Простой |
| **Render** | ✅ Да | 750 ч/мес | ⭐⭐ Средний (Docker) |
| **Fly.io** | ✅ Да | 3 VM | ⭐⭐ Средний |
| **Koyeb** | ✅ Да | 1 app | ⭐ Простой |

---

## Структура проекта

```
qa-tester-hub-java/
├── Dockerfile              # Для Docker деплоя
├── pom.xml                 # Maven конфигурация
├── render.yaml             # Render конфигурация
├── Procfile                # Heroku/Render
├── system.properties       # Java версия
├── README.md
└── src/main/
    ├── java/com/qatester/hub/
    │   ├── QaTesterHubApplication.java
    │   ├── config/           # WebSocket config
    │   ├── controller/       # REST контроллеры
    │   └── graphql/          # GraphQL сервисы
    └── resources/
        ├── application.yml
        ├── templates/        # HTML шаблоны
        └── static/images/    # Изображения
```

---

## Функционал

| Task | Описание | Эндпоинты |
|------|----------|-----------|
| Task 0 | JSON с ошибками | `/task0` |
| Task 1 | Форма с багами | `/task1` |
| Task 2 | WebSocket тестирование | `/task2`, `/ws` |
| Task 3 | GraphQL Schema | `/task3`, `/task3/graphql` |
| Task 4 | Оценка изображений | `/task4`, `/images/*` |
| Task 5 | WebSocket Echo | `/task5` |
| Task 6 | Bet-Builder чек-лист | `/task6` |
| Task 7 | Duplicate Bet | `/task7`, `/api/v2/coupon/brand/{id}/bet/place` |
| Task 8 | Async Order API | `/task8`, `/api/task8` |
| Task 9 | Frontend/Backend | `/task9`, `/api/task9` |
| Task 10 | GraphQL Error | `/task10`, `/api/task10/graphql` |

---

## Переменные окружения

| Переменная | По умолчанию | Описание |
|------------|--------------|----------|
| `PORT` | 8080 | Порт сервера |

---

Первоначальный проект: https://github.com/Deazushka/forQA (Flask)
# qa-tester-hub-java
