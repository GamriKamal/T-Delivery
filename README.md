# T-Delivery
Привет! Это описание моей курсовой работы для Финтех, Java, осень 2024. Тема курсовой работы `"Создание приложения для заказа еды с функциями доставки"`.

## Стек технологий:
- Java/Spring Boot
- Spring Cloud
- Kotlin/Ktor
- PostgreSQL
- MongoDB
- Docker
- Yandex Maps API
- The MealDB API

## Возможные сервисы:
В качестве архитектуры, будут использоваться микросервисы.
### `user_service`
  - Описание: Регистрация пользователей, авторизация, управление аккаунтами.
  - Стэк: Java, PostgreSQL, JWT и OAuth 2.0.

### `menu_service`
  - Описание: Подтягивание данных с API, вывод на странице сайте и сохранение в бд.
  - Стэк: Kotlin, MongoDB.
    
### `order_service`
  - Описание: Обработка заказов, отслеживание статуса, управление корзиной и история заказов.
  - Стэк: Java, PostgreSQL.
    
### `delivery_service`
  - Описание: Расчет времени доставки, отправка статуса заказа и генерирование ценообразования через ии в зависимости от загруженности города.
  - Стэк: Java, PostgreSQL, Kafka, TensorFlow и API Yandex Maps.

### `gateway_service`
  - Описание: Маршрутизатор для всех сервисов.
  - Стэк: Java, Spring Cloud.
