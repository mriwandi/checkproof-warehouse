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
