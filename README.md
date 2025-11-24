# Shop Warehouse Management API

A RESTful API for managing shop inventory with items, variants, and stock management.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## How to Run

1. **Build the project**:
   ```bash
   mvn clean install
   ```

2. **Run the application**:
   ```bash
   mvn spring-boot:run -pl app
   ```

3. **Verify it's running**:
   - API Base URL: `http://localhost:8080`
   - H2 Console: `http://localhost:8080/h2-console`
     - JDBC URL: `jdbc:h2:mem:warehousedb`
     - Username: `sa`
     - Password: (empty)

## API Contract Examples

### Items

#### Create Item
```http
POST /api/v1/items
Content-Type: application/json

{
  "itemName": "Cotton T-Shirt",
  "description": "100% Cotton T-Shirt",
  "category": "Clothing"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Cotton T-Shirt",
  "description": "100% Cotton T-Shirt",
  "category": "Clothing",
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

#### Get All Items
```http
GET /api/v1/items
```

#### Get Item by ID
```http
GET /api/v1/items/{id}
```

#### Update Item
```http
PUT /api/v1/items/{id}
Content-Type: application/json

{
  "itemName": "Premium T-Shirt",
  "description": "Premium Cotton T-Shirt",
  "category": "Clothing"
}
```

#### Delete Item
```http
DELETE /api/v1/items/{id}
```

### Variants

#### Create Variant
```http
POST /api/v1/items/{itemId}/variants
Content-Type: application/json

{
  "name": "Small - Blue",
  "sku": "TSHIRT-SM-BLUE-001",
  "price": 29.99
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Small - Blue",
  "sku": "TSHIRT-SM-BLUE-001",
  "price": 29.99,
  "createdAt": "2024-01-01T10:00:00"
}
```

#### Get Variants of Item
```http
GET /api/v1/items/{itemId}/variants
```

#### Update Variant
```http
PUT /api/v1/variants/{id}
Content-Type: application/json

{
  "name": "Small - Blue",
  "sku": "TSHIRT-SM-BLUE-001",
  "price": 34.99
}
```

#### Delete Variant
```http
DELETE /api/v1/variants/{id}
```

### Stock Management

#### Set Stock
```http
PUT /api/v1/variants/{variantId}/stock
Content-Type: application/json

{
  "quantity": 100
}
```

#### Increase Stock
```http
POST /api/v1/variants/{variantId}/stock/increase
Content-Type: application/json

{
  "quantity": 50
}
```

#### Decrease Stock
```http
POST /api/v1/variants/{variantId}/stock/decrease
Content-Type: application/json

{
  "quantity": 10
}
```

#### Reserve Stock
```http
POST /api/v1/variants/{variantId}/stock/reserve
Content-Type: application/json

{
  "quantity": 20
}
```

**Note**: Reserves stock for pending orders. Moves stock from available to allocated. Available stock = availableStock - allocatedStock.

#### Release Stock
```http
POST /api/v1/variants/{variantId}/stock/release
Content-Type: application/json

{
  "quantity": 20
}
```

**Note**: Releases previously reserved stock back to available. Moves stock from allocated back to available.

#### Commit Stock
```http
POST /api/v1/variants/{variantId}/stock/commit
Content-Type: application/json

{
  "quantity": 20
}
```

**Note**: Commits previously reserved stock. Decreases both availableStock and allocatedStock. Use this after an order is confirmed.

## Sample Data Workflow

```bash
# 1. Create an item
curl -X POST http://localhost:8080/api/v1/items \
  -H "Content-Type: application/json" \
  -d '{"itemName": "Cotton T-Shirt", "description": "100% Cotton", "category": "Clothing"}'

# 2. Create a variant (use item ID from step 1)
curl -X POST http://localhost:8080/api/v1/items/1/variants \
  -H "Content-Type: application/json" \
  -d '{"name": "Small - Blue", "sku": "TSHIRT-SM-BLUE-001", "price": 29.99}'

# 3. Set stock (use variant ID from step 2)
curl -X PUT http://localhost:8080/api/v1/variants/1/stock \
  -H "Content-Type: application/json" \
  -d '{"quantity": 100}'

# 4. Reserve stock for pending order
curl -X POST http://localhost:8080/api/v1/variants/1/stock/reserve \
  -H "Content-Type: application/json" \
  -d '{"quantity": 20}'

# 5. Commit reserved stock (order confirmed)
curl -X POST http://localhost:8080/api/v1/variants/1/stock/commit \
  -H "Content-Type: application/json" \
  -d '{"quantity": 15}'

# 6. Release reserved stock if order cancelled
curl -X POST http://localhost:8080/api/v1/variants/1/stock/release \
  -H "Content-Type: application/json" \
  -d '{"quantity": 5}'
```

## Design Decisions

### Multi-Module Maven Architecture
The project is structured as a multi-module Maven project with clear separation of concerns:
- **entity**: JPA entities and domain models
- **repository**: Data access layer (Spring Data JPA repositories)
- **service**: Service interfaces (contracts)
- **service-impl**: Service implementations
- **service-model**: DTOs for service layer communication
- **web**: REST controllers and web layer
- **web-model**: DTOs for API requests/responses
- **helper**: Utility classes and converters (MapStruct mappers)
- **app**: Spring Boot application entry point

**Why**: This modular structure provides clear boundaries between layers, improves maintainability, enables independent testing, and allows for better dependency management. Each module has a single responsibility, making the codebase easier to understand and modify.

### Separation of DTOs (Web-Model vs Service-Model)
The application uses separate DTO layers:
- **web-model**: Request/Response DTOs for REST API (e.g., `ItemRequest`, `ItemResponse`)
- **service-model**: DTOs for service layer communication (e.g., `ItemSpec`, `ItemResult`)

**Why**: This separation decouples the web layer from the service layer, allowing the service layer to be reused by different clients (REST, GraphQL, messaging, etc.) without exposing web-specific concerns. It also provides flexibility to change API contracts without affecting business logic.

### MapStruct for Object Mapping
MapStruct is used for converting between entities, DTOs, and service models.

**Why**: MapStruct generates mapping code at compile-time, providing type-safe conversions with zero runtime overhead. It reduces boilerplate code and eliminates manual mapping errors. The generated code is efficient and easy to debug.

### Stock Management Design
Stock is managed using two separate fields:
- **availableStock**: Total physical stock available
- **allocatedStock**: Stock reserved for pending orders

Available quantity is calculated as `availableStock - allocatedStock`.

**Why**: This design supports the order lifecycle (reserve → commit/release) without losing track of physical inventory. It allows the system to:
- Reserve stock for pending orders without immediately reducing physical stock
- Track how much stock is committed vs. available
- Release reserved stock if orders are cancelled
- Commit stock only when orders are confirmed

### Optimistic Locking
Both `ItemVariant` and `ItemVariantStock` entities use `@Version` annotation for optimistic locking.

**Why**: Stock operations are critical and can have concurrent access. Optimistic locking prevents lost updates in concurrent scenarios by detecting when an entity has been modified by another transaction. This is more scalable than pessimistic locking and fits well with read-heavy workloads.

### BaseEntity with JPA Auditing
All entities extend `BaseEntity` which provides:
- Auto-generated ID
- `createdAt` and `updatedAt` timestamps (automatically managed)
- `createdBy` and `updatedBy` fields (for future audit trail)

**Why**: Centralized audit fields reduce code duplication and ensure consistent tracking of entity lifecycle. JPA auditing automatically populates timestamps, reducing manual maintenance and potential errors.

### One-to-One Relationship for Stock
`ItemVariantStock` has a one-to-one relationship with `ItemVariant` rather than embedding stock fields in the variant.

**Why**: This separation allows for:
- Better data normalization
- Easier to extend stock management features independently
- Clearer domain model (stock is a separate concern)
- Potential for lazy loading if needed

### Automatic Stock Creation
When a variant is created, a corresponding `ItemVariantStock` record is automatically created with zero stock.

**Why**: This ensures every variant always has a stock record, preventing null pointer exceptions and simplifying stock operations. It maintains data consistency and eliminates the need for null checks.

### Cascade Delete for Variants
Items use `CascadeType.ALL` and `orphanRemoval = true` for variants, meaning deleting an item automatically deletes all its variants.

**Why**: This maintains referential integrity and prevents orphaned variant records. It simplifies cleanup operations and ensures data consistency.

### Transactional Service Methods
All write operations in service implementations are annotated with `@Transactional`.

**Why**: This ensures data consistency by grouping related database operations into atomic transactions. If any operation fails, the entire transaction rolls back, preventing partial updates that could lead to inconsistent state.

### Global Exception Handler
A centralized `GlobalExceptionHandler` handles all exceptions and converts them to appropriate HTTP responses.

**Why**: This provides consistent error responses across the API, reduces code duplication in controllers, and makes it easier to add logging, monitoring, or error tracking. It also separates error handling concerns from business logic.

### H2 In-Memory Database
The application uses H2 in-memory database for development.

**Why**: H2 provides a lightweight, zero-configuration database that's perfect for development and testing. It eliminates the need for external database setup while still using standard SQL and JPA, making it easy to switch to production databases (PostgreSQL, MySQL, etc.) later.

## Assumptions

### Database and Environment
- **H2 in-memory database is sufficient** for development and testing purposes. Production deployments would require a persistent database (PostgreSQL, MySQL, etc.) with appropriate connection pooling and configuration.
- **No authentication/authorization** is required. The API is assumed to be used in a trusted environment or behind an API gateway that handles security.
- **Single application instance** or low-concurrency environment. While optimistic locking is implemented, high-concurrency scenarios may require additional retry logic or distributed locking mechanisms.

### Business Logic
- **Stock can be set to any value** including zero or negative values. The `setManualStock` operation doesn't validate against business rules (e.g., minimum stock levels).
- **SKU uniqueness** is enforced at the database level. The application assumes SKUs are globally unique across all variants.
- **Item description uniqueness** is enforced. Each item must have a unique description.
- **No soft delete** - entities are permanently deleted from the database. There's no audit trail of deleted items or variants.
- **Stock operations are synchronous** - all stock operations complete immediately. There's no support for asynchronous stock updates or eventual consistency.

### API Design
- **No pagination** for list endpoints (`GET /api/v1/items`, `GET /api/v1/items/{itemId}/variants`). These endpoints return all records, which may be problematic for large datasets.
- **No filtering or sorting** capabilities on list endpoints. Clients receive all data in the default order.
- **No bulk operations** - all operations work on single entities. Bulk create/update/delete operations are not supported.
- **No versioning beyond v1** - the API uses `/api/v1/` prefix but there's no mechanism for handling multiple API versions simultaneously.

### Data Model
- **One variant belongs to one item** - variants cannot be shared across multiple items.
- **One stock record per variant** - each variant has exactly one stock record (enforced by one-to-one relationship).
- **Price is stored as Double** - assumes currency precision is sufficient with Double. For financial applications, BigDecimal might be more appropriate.
- **No stock history or audit trail** - stock changes are not logged. Only current stock levels are maintained.

### Error Handling
- **Optimistic locking exceptions** are not explicitly handled. In high-concurrency scenarios, `OptimisticLockException` would need to be caught and retried.
- **Database constraint violations** (e.g., unique constraint on SKU) result in generic exceptions that may not provide clear error messages to clients.
