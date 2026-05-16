# Cache Engine

A high-performance in-memory key-value cache engine supporting LRU, LFU, and TTL eviction policies with thread-safe concurrent access, exposed via REST API and deployed on Render.

---

## Features

- Three pluggable eviction policies — LRU, LFU, TTL
- O(1) get and put operations for LRU and LFU
- Thread-safe concurrent access via ReentrantReadWriteLock
- Multiple readers allowed simultaneously, exclusive writes
- TTL-based expiry with lazy and eager cleanup
- Switch eviction policy at runtime without restarting
- Live metrics — hit rate, miss rate, eviction count
- Containerized with Docker
- Deployed at (https://cache-engine.onrender.com/)

---

## Tech Stack

- Java 21
- Spring Boot 3.2
- Maven
- Docker

---

## Architecture

```
HTTP Request
     ↓
CacheController        REST layer — routes HTTP to service
     ↓
CacheService           Applies ReentrantReadWriteLock, delegates to policy
     ↓
EvictionPolicy         Interface — pluggable at runtime
  ├── LRUCache         Doubly linked list + HashMap — O(1)
  ├── LFUCache         Frequency bucket map + keyMap — O(1)
  └── TTLCache         Expiry timestamp map + ScheduledExecutor
     ↓
MetricsCollector       Tracks hit/miss/eviction via AtomicLong
```

---

## Design Decisions

**Why ReentrantReadWriteLock over synchronized?**
Cache workloads are read-heavy. ReentrantReadWriteLock allows multiple concurrent readers while giving exclusive access to writers — much higher throughput than synchronized which blocks all threads.

**Why doubly linked list + HashMap for LRU?**
HashMap gives O(1) key lookup. Doubly linked list gives O(1) node insertion and removal without traversal. Together they achieve O(1) for both get and put.

**Why frequency buckets for LFU?**
Each frequency maps to a LinkedList of nodes at that frequency. A minFreq pointer always tracks the lowest frequency bucket — making eviction O(1) without scanning all entries.

**Why EvictionPolicy interface?**
Decouples CacheService from specific implementations. Switching from LRU to LFU at runtime is just swapping the active policy — no other code changes.

**Why AtomicLong for metrics?**
Multiple threads increment counters simultaneously. AtomicLong provides thread-safe increment in a single atomic operation without locking overhead.

---

## API Endpoints

### Store a value
```
PUT /cache/{key}
Body: value (plain text)
Response: "stored"
```

### Retrieve a value
```
GET /cache/{key}
Response: value or "null"
```

### Delete a value
```
DELETE /cache/{key}
Response: "deleted"
```

### Get metrics
```
GET /cache/stats/metrics
Response: "Hits: 42, Misses: 8, Evictions: 3, HitRate: 84.00%"
```

### Switch eviction policy
```
POST /cache/config?policy=LFU&capacity=100
Response: "switched to LFU"

Supported policies: LRU, LFU, TTL
```

---

## Running Locally

**Prerequisites:** Java 21, Maven, Docker

### Run with Java
```bash
mvn clean package -DskipTests
java -jar target/cacheengine-0.0.1-SNAPSHOT.jar
```

### Run with Docker
```bash
docker build -t cache-engine .
docker run -p 8080:8080 cache-engine
```

API available at `http://localhost:8080`

---

## Live Demo

Base URL: `https://cache-engine.onrender.com/`

```bash
# Store a value
curl -X PUT https://cache-engine.onrender.com/cache/user1 \
  -H "Content-Type: text/plain" -d "Alice"

# Retrieve it
curl https://cache-engine.onrender.com/cache/user1

# Check metrics
curl https://cache-engine.onrender.com/cache/stats/metrics

# Switch to LFU
curl -X POST "https://cache-engine.onrender.com/cache/config?policy=LFU&capacity=100"
```

---




