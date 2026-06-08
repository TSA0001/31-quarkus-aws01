# Quark01 雨量監視API

AWS Lambdaから雨量データを受信し、PostgreSQLに保存するREST APIサーバー。

## 機能

- **POST /api/rainfall**: 雨量データ受信・保存
- **GET /api/rainfall**: 全雨量データ取得（location, limitパラメータ対応）
- **GET /api/rainfall/latest**: 最新10件取得
- **GET /api/rainfall/alerts**: 警報レベル（50mm以上）のデータ取得

## セットアップ

### 1. PostgreSQLの起動

```bash
cd docker
docker-compose up -d
```

### 2. アプリケーションのビルド

```bash
./mvnw clean package
```

### 3. アプリケーションの起動

開発モード:
```bash
./mvnw quarkus:dev
```

本番モード:
```bash
java -jar target/quarkus-app/quarkus-run.jar
```

## API使用例

### 雨量データ送信

```bash
curl -X POST http://localhost:8080/api/rainfall \
  -H "Content-Type: application/json" \
  -d '{
    "location": "東京",
    "hourlyRainfall": 25,
    "total24h": 150,
    "forecast1h": 30,
    "timestamp": "2026-06-02T18:00:00"
  }'
```

### 最新データ取得

```bash
curl http://localhost:8080/api/rainfall/latest
```

### 警報データ取得

```bash
curl http://localhost:8080/api/rainfall/alerts
```

### 拠点別データ取得

```bash
curl "http://localhost:8080/api/rainfall?location=東京&limit=50"
```

## デプロイ

### quark01サーバーへのデプロイ

1. ビルド:
```bash
./mvnw clean package
```

2. サーバーに転送:
```bash
scp target/quarkus-app/quarkus-run.jar tsano@192.168.200.155:~/
scp -r target/quarkus-app/lib tsano@192.168.200.155:~/quarkus-app/
```

3. サーバーで実行:
```bash
ssh tsano@192.168.200.155
java -jar quarkus-run.jar
```

### Systemdサービス化

```bash
sudo cat > /etc/systemd/system/rainfall-api.service << 'EOFS'
[Unit]
Description=Rainfall API Service
After=network.target postgresql.service

[Service]
Type=simple
User=tsano
WorkingDirectory=/home/tsano/rainfall-api
ExecStart=/usr/bin/java -jar /home/tsano/rainfall-api/quarkus-run.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOFS

sudo systemctl daemon-reload
sudo systemctl enable rainfall-api
sudo systemctl start rainfall-api
```

## データベーススキーマ

```sql
CREATE TABLE rainfall_records (
    id BIGSERIAL PRIMARY KEY,
    location VARCHAR(255),
    hourly_rainfall INTEGER,
    total24h INTEGER,
    forecast1h INTEGER,
    timestamp TIMESTAMP,
    created_at TIMESTAMP
);

CREATE INDEX idx_location ON rainfall_records(location);
CREATE INDEX idx_timestamp ON rainfall_records(timestamp DESC);
CREATE INDEX idx_hourly_rainfall ON rainfall_records(hourly_rainfall);
```

## 環境変数

| 変数名 | 説明 | デフォルト |
|--------|------|-----------|
| QUARKUS_DATASOURCE_JDBC_URL | PostgreSQL接続URL | jdbc:postgresql://localhost:5432/rainfall_db |
| QUARKUS_DATASOURCE_USERNAME | DBユーザー名 | rainfall |
| QUARKUS_DATASOURCE_PASSWORD | DBパスワード | rainfall123 |
| QUARKUS_HTTP_PORT | HTTPポート | 8080 |
