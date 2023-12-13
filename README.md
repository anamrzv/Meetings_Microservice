## Декомпозировать на микросервисы разработанную систему из лабораторной работы 1.

- Микросервисы должны регистрироваться в [Eureka](https://spring.io/projects/spring-cloud-netflix).
- Микросервисы должны подтягивать конфигурацию из [Config-Server](https://spring.io/projects/spring-cloud-config).
- Микросервисы должны быть доступны через [Spring Gateway](https://spring.io/projects/spring-cloud-gateway).
- Для взаимодействия между микросервисами использовать Feign Client.
- Внедрить [Circuit Breaker](https://cloud.spring.io/spring-cloud-netflix/multi/multi__circuit_breaker_hystrix_clients.html).
- Разрешить проблему с авторизацией между микросервисами и не забыть про тесты.
- Минимум один микросервис должен быть написан с использованием Reactor и R2DBC.
- Минимум один микросервис должен быть написан с использованием Reactor и Spring Data JPA.
