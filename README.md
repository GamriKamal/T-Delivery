# T-Delivery - приложение для заказов еды с функциями доставки.

<div align="center">
    <img src="assets/t-delivery.png" alt="Логотип" width="200"/>
</div>

## Обзор
T-Delivery — это приложение на основе микросервисов для платформы 
доставки. Проект включает различные сервисы, 
такие как аутентификация, управление курьерами, 
обработка заказов и управление пользователями. 
В архитектуре используются Docker, PostgreSQL, MongoDB, PostGIS, 
а также интеграция с внешним сервисом, таким как Google Maps API 
для маршрутизации.

## Описание сервисов

### 1. `auth-service`
- **Описание**: Прокси-сервис для генерации JWT токенов, взаимодействующий с `user-service`.

### 2. `courier-service`
- **Описание**: Сервис для курьеров. С помощью этого сервиса курьеры получают заказы в зависимости от их координат и радиуса доставки.
- **Роль**: Для доступа необходима роль `COURIER`.
- **База данных**: Нет (используется только кэш для сохранения оптимальных заказов в рамках курсовой работы).

### 3. `discovery-service`
- **Описание**: Eureka сервер для управления сервисами в распределенной системе.

### 4. `gateway-service`
- **Описание**: Сервис для маршрутизации между сервисами и проверки JWT токенов с ролью пользователя.

### 5. `menu-service`
- **Описание**: Сервис для предоставления меню пользователям с пагинацией.
- **Роль**: Пользователи с ролью `ADMIN` могут загружать меню в формате CSV.
- **База данных**: MongoDB.

### 6. `message-service`
- **Описание**: Сервис для отправки сообщений на Gmail почту.

### 7. `order-service`
- **Описание**: Сервис для обработки заказов. Включает взаимодействие с PostGIS для поиска заказов и маршрутизации. Также имеет HTML-страницу с веб-сокетом для отображения состояния заказа.
- **База данных**: PostgreSQL с расширением PostGIS для работы с геоданными.

### 8. `route-service`
- **Описание**: Сервис для расчета маршрутов с использованием Google Maps API. Определяет расстояние и выбирает ближайший ресторан для пользователя. Также находит ближайшие заказы для курьеров.
- **База данных**: PostgreSQL (для хранения координат ресторанов).

### 9. `user-service`
- **Описание**: Сервис для хранения данных о пользователях. Поддерживает роли `USER`, `ADMIN`, `COURIER`.
- **База данных**: PostgreSQL.

## Структура проекта

Проект использует микросервисную архитектуру и включает следующие компоненты:
- **Docker** для контейнеризации сервисов.
- **PostgreSQL**, **MongoDB**, **PostGIS** для работы с данными.
- **Google Maps API** для маршрутизации.

Вот диаграмма, которая описывает общую структуру проекта:

![Структура проекта](/assets/project-diagram.png)

## Как запустить проект

1. **Клонируйте репозиторий**:
    ```bash
    git clone https://github.com/GamriKamal/T-Delivery.git
    cd T-Delivery
    ```

2. **Соберите сервисы с помощью Makefile**:

    - Для сборки конкретного сервиса:
      ```bash
      make build <service-name>
      ```

    - Для сборки всех сервисов:
      ```bash
      make build-all
      ```

3. **Запустите проект с помощью Docker**:
    - Для поднятия отдельных сервисов просто используйте 
    ```bash
    docker-compose up <service-name>
   ```
    - Для поднятия всех сервисов:
    ```bash
    make up docker-up
    ```

## Тесты и покрытия

Каждый сервис включает тесты (Unit & Integration), а также генерируются репорты JaCoCo для анализа покрытия тестами. 
Они доступны в папках ./build/jacocoHtml/index.html (gradle) или ./target/site/index.html (maven)

