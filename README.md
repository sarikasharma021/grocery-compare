# GroceryCompare

A real-time grocery price aggregation platform that compares prices, availability, and delivery time across Blinkit, Zepto, Instamart, BigBasket, Flipkart Minutes, and Amazon Fresh.

## Architecture
    aggregator-service (port 8080)
├── CompareController        → REST API layer
├── AggregatorService        → Concurrent fan-out orchestration
├── ProviderService          → Strategy pattern interface
├── Provider Implementations → Blinkit, Zepto, Instamart, BigBasket, Flipkart, Amazon
├── Redis Cache              → 60 second TTL, cache-aside pattern
├── Resilience4j             → Circuit breaker per provider
└── Kafka Producer           → Publishes SearchEvent on every search
alert-service (port 8081)
└── Kafka Consumer           → Consumes SearchEvent, logs and tracks searches

## Key Technical Decisions

### Why Kafka over direct REST calls between services?
If alert-service is down, Kafka holds events and delivers when it recovers — zero data loss. aggregator-service has no knowledge of alert-service — loose coupling. New consumers (analytics, recommendations) can be added without touching aggregator-service.

### Why Redis for caching?
Same search within 60 seconds returns cached result — skips all six provider calls. Redis survives application restarts unlike in-memory cache. Can be shared across multiple aggregator-service instances. TTL of 60 seconds balances freshness vs performance.

### Why CompletableFuture for provider calls?
Sequential calls: 6 providers × 100ms = 600ms total response time. Concurrent fan-out: all six called simultaneously = ~100ms total. Each provider has 3 second timeout isolation — one slow provider cannot affect others.

### Why Strategy Pattern for providers?
Adding a 7th provider requires creating one new class — zero changes to existing code. This is the Open/Closed Principle — open for extension, closed for modification.

### Why Resilience4j Circuit Breaker?
If a provider fails repeatedly, circuit breaker opens — stops calling that provider for 10 seconds. Prevents cascading failures. Other five providers continue responding normally.

### Why Couchbase over MySQL? (future)
Grocery product data is document-oriented — varying attributes per product category. Document model suits this better than rigid relational schema.

## Tech Stack

| Technology | Purpose |
|---|---|
| Spring Boot 3.5 | Core framework |
| Apache Kafka | Event streaming between services |
| Redis | Caching with TTL |
| Resilience4j | Circuit breaker pattern |
| CompletableFuture | Concurrent provider calls |
| Docker + Docker Compose | Containerization |
| Kubernetes (Minikube) | Container orchestration |
| Lombok | Boilerplate elimination |
| Spring Actuator | Health checks and monitoring |

## Running Locally

### With Docker Compose
```bash
docker-compose up
```

### With Kubernetes
```bash
minikube start --driver=docker
kubectl apply -f k8s/
minikube service aggregator-service --url
```

## API Reference

### Compare Prices
GET /api/v1/compare?city={city}&item={item}&sortBy={sortBy}&onlyAvailable={boolean}

| Parameter | Required | Default | Options |
|---|---|---|---|
| city | Yes | - | Any city name |
| item | Yes | - | Any grocery item |
| sortBy | No | price | price, delivery, availability |
| onlyAvailable | No | false | true, false |

### Example Request
GET /api/v1/compare?city=Mumbai&item=milk&sortBy=price&onlyAvailable=true

### Example Response
```json
[
  {
    "provider": "BigBasket",
    "item": "milk",
    "city": "Mumbai",
    "price": 60.0,
    "available": true,
    "deliveryMinutes": 30
  }
]
```

### Error Response
```json
{
  "status": 400,
  "message": "City cannot be empty",
  "timestamp": "2026-05-22T10:00:00"
}
```

## Services

| Service | Port | Responsibility |
|---|---|---|
| aggregator-service | 8080 | Price aggregation, caching, circuit breaking |
| alert-service | 8081 | Search event consumption and tracking |
| Redis | 6379 | Cache store |
| Kafka | 9092 | Event streaming |
