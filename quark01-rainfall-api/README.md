# Quark01 雨量監視 API

AWS Lambda から雨量データを受信し、PostgreSQL に保存する Quarkus REST API サーバーです。
河川水位データ API と簡易 dashboard も含みます。

## 前提

- Java 17
- Maven 3.x
- PostgreSQL
- Docker / Docker Compose（ローカルで PostgreSQL を起動する場合）
- nginx（外部公開時の reverse proxy として使用する場合）

このプロジェクトには Maven Wrapper は含めていないため、コマンド例では `mvn` を使用します。

## 機能

- **POST /api/rainfall**: 雨量データ受信・保存
- **GET /api/rainfall**: 全雨量データ取得（location, limitパラメータ対応）
- **GET /api/rainfall/latest**: 最新10件取得
- **GET /api/rainfall/alerts**: 警報レベル（50mm以上）のデータ取得
- **GET /api/river**: 河川水位データ取得
- **POST /api/river**: 河川水位データ登録
- **GET /**: 簡易 dashboard

## 設定

`src/main/resources/application.properties` にはローカル開発用の値が入っています。
本番環境では環境変数で上書きしてください。

| 変数名 | 説明 | デフォルト |
|--------|------|-----------|
| `QUARKUS_DATASOURCE_JDBC_URL` | PostgreSQL 接続 URL | `jdbc:postgresql://localhost:5432/rainfall_db` |
| `QUARKUS_DATASOURCE_USERNAME` | DB ユーザー名 | `rainfall` |
| `QUARKUS_DATASOURCE_PASSWORD` | DB パスワード | `rainfall123` |
| `QUARKUS_HTTP_PORT` | HTTP ポート | `8080` |

dashboard のログイン値は GitHub 用に `CHANGE_ME` にしています。
利用前に `src/main/resources/META-INF/resources/index.html` の `validUsername` と `validPassword` を環境に合わせて変更してください。

## セットアップ

### 1. PostgreSQL の起動

```bash
cd docker
docker-compose up -d
```

### 2. アプリケーションのビルド

```bash
mvn clean package
```

### 3. アプリケーションの起動

開発モード:
```bash
mvn quarkus:dev
```

本番モード:
```bash
java -jar target/quarkus-app/quarkus-run.jar
```

## API 使用例

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

### 河川水位データ登録

```bash
curl -X POST http://localhost:8080/api/river \
  -H "Content-Type: application/json" \
  -d '{
    "riverName": "サンプル川",
    "location": "観測地点A",
    "currentLevel": 1.2,
    "warningLevel": 2.5,
    "dangerLevel": 3.5,
    "timestamp": "2026-06-02T18:00:00"
  }'
```

### dashboard

```bash
open http://localhost:8080/
```

## デプロイ

### サーバーへのデプロイ

1. ビルド:
```bash
mvn clean package
```

2. サーバーに転送:
```bash
scp -r target/quarkus-app <user>@<api-server>:/path/to/quark01-rainfall-api/
```

3. サーバーで実行:
```bash
ssh <user>@<api-server>
cd /path/to/quark01-rainfall-api
java -jar target/quarkus-app/quarkus-run.jar
```

### nginx

nginx で外部公開する場合の抜粋設定は `nginx/rainfall-api.conf` にあります。
この設定は、公開ポート `8080` からローカルの Quarkus アプリ `127.0.0.1:8082` へ proxy する例です。

Quarkus 側を `8082` で起動する場合:

```bash
QUARKUS_HTTP_PORT=8082 java -jar target/quarkus-app/quarkus-run.jar
```

### systemd サービス化例

```bash
sudo cat > /etc/systemd/system/rainfall-api.service << 'EOFS'
[Unit]
Description=Rainfall API Service
After=network.target postgresql.service

[Service]
Type=simple
User=<user>
WorkingDirectory=/path/to/quark01-rainfall-api
Environment=QUARKUS_HTTP_PORT=8082
ExecStart=/usr/bin/java -jar /path/to/quark01-rainfall-api/target/quarkus-app/quarkus-run.jar
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

CREATE TABLE river_levels (
    id BIGSERIAL PRIMARY KEY,
    river_name VARCHAR(255),
    location VARCHAR(255),
    current_level DOUBLE PRECISION,
    warning_level DOUBLE PRECISION,
    danger_level DOUBLE PRECISION,
    timestamp TIMESTAMP,
    created_at TIMESTAMP
);
```
