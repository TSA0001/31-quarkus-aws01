# Rainfall Lambda

気象庁 API から雨量データを取得し、Quark01 雨量監視 API へ送信する AWS Lambda です。

## 前提

- Java 17
- Maven 3.x
- AWS CLI
- AWS Lambda 実行ロール
- 送信先 API サーバーが起動していること

## 環境変数

| 変数名 | 説明 | デフォルト |
|--------|------|------------|
| `QUARK01_API_URL` | 雨量監視 API のベース URL | `http://localhost:8080` |
| `QUARK01_API_TOKEN` | API に送信する Bearer token | `dummy-token-for-dev` |

実環境の URL、固定 IP、トークンは GitHub に含めず、Lambda の環境変数で設定します。

## ビルド

```bash
mvn clean package
```

ビルド成果物は `target/` に生成されます。`target/` と zip ファイルは Git 管理対象外です。

## ローカル確認

```bash
mvn test
```

## デプロイ時の注意

- `QUARK01_API_URL` は API サーバーの公開 URL に設定してください。
- API 側が nginx 経由で公開されている場合は、nginx の公開ポートを含む URL を指定します。
- AWS 認証情報、実 IP、秘密値はソースに書かず、AWS 側の環境変数や Secrets Manager で管理してください。
