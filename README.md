# 31-quarkus-aws01

Quarkus で構成した雨量監視システムです。

このリポジトリには、AWS Lambda 側と API サーバー側の 2 つのアプリケーションを含みます。

## 構成

- `aws-lambda-rainfall/`: 気象庁 API から雨量データを取得し、API サーバーへ送信する Lambda
- `quark01-rainfall-api/`: 雨量データと河川水位データを保存・参照する Quarkus REST API

## 共通の前提

- Java 17
- Maven 3.x
- Git

このリポジトリには Maven Wrapper は含めていないため、コマンド例では `mvn` を使用します。

## 実行に必要な外部要素

API 側:

- PostgreSQL
- API サーバーを外部公開する場合は nginx などの reverse proxy
- dashboard を使う場合は `quark01-rainfall-api/src/main/resources/META-INF/resources/index.html` のログイン値設定

Lambda 側:

- AWS アカウント
- Lambda 実行ロール
- API サーバーの URL
- 必要に応じて API 認証トークン

## GitHub に含めないもの

以下はローカルまたは実行環境で管理します。

- `.env`
- パスワード、秘密鍵、AWS 認証情報
- 実環境の固定 IP や個人の接続情報
- `target/`
- `*.zip`
- `*.tar.gz`
- `*.log`

## セットアップ順序

1. `quark01-rainfall-api/` をセットアップして API と DB を起動する
2. API の公開 URL を確認する
3. `aws-lambda-rainfall/` の `QUARK01_API_URL` に API URL を設定する
4. Lambda をビルド・デプロイする

詳細は各ディレクトリの README を参照してください。
