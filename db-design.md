# やること管理アプリ DB設計書

## 1. 文書の目的

本書は、`handout_app_spec.md` と `requirements.md` に基づき、やること管理アプリが使用するデータベース（データを保存する仕組み）の構造を定めるものである。

画面の配置や画面間の詳しい動きは、別途作成する画面設計書で定める。

## 2. データベースの前提

| 項目 | 内容 |
|---|---|
| データベース製品 | MySQL |
| 使用するテーブル数 | 1つ |
| テーブル名 | `todos` |
| 1行の意味 | 1件のやること |
| 利用者 | 1人 |
| ログイン情報 | 保存しない |
| 操作ログ | `todos` 以外のテーブルは作らず、アプリケーションのログ（アプリが出す操作記録）として扱う |

## 3. テーブル一覧

このアプリで使用するテーブルは `todos` だけである。

| テーブル名 | 役割 |
|---|---|
| `todos` | やること、メモ、ジャンル、優先度、期限、完了状態、登録・更新日時を保存する |

## 4. `todos` テーブル設計

### 4.1 カラム一覧

カラム（テーブルの縦列）は、次のとおりとする。

| カラム名 | データ型 | NULL | 初期値 | キー・制約 | 内容 |
|---|---|---|---|---|---|
| `id` | `BIGINT` | 不可 | 自動採番 | 主キー、`AUTO_INCREMENT` | 1件を見分ける番号 |
| `title` | `VARCHAR(255)` | 不可 | なし | 255文字以内 | やることの名前 |
| `detail` | `VARCHAR(255)` | 可 | `NULL` | 255文字以内 | メモ |
| `category` | `VARCHAR(255)` | 不可 | なし | 指定された5種類のいずれか | ジャンル |
| `priority` | `INT` | 不可 | `2` | `1`・`2`・`3`のいずれか | 優先度。1＝高、2＝中、3＝低 |
| `due_date` | `DATE` | 可 | `NULL` | なし | 期限。未設定を許可 |
| `completed` | `BOOLEAN` | 不可 | `FALSE` | なし | 完了状態。`FALSE`＝未完了、`TRUE`＝完了 |
| `created_at` | `DATETIME` | 不可 | `CURRENT_TIMESTAMP` | 自動設定 | 登録した日時 |
| `updated_at` | `DATETIME` | 不可 | `CURRENT_TIMESTAMP` | 更新時に自動変更 | 最後に変更した日時 |

### 4.2 `id`

- 1件を一意に識別する番号（一意＝他の行と重ならない）である。
- `BIGINT` 型を使用する。
- `AUTO_INCREMENT` により、登録時にMySQLが番号を自動で1ずつ増やす。
- 主キー（行を一意に特定する列）とする。
- 登録・編集・削除の画面入力には表示しない。

### 4.3 `title`

- やることの名前を保存する。
- 必須項目とし、`NULL`（値がない状態）を許可しない。
- 最大255文字とする。
- 空文字や半角・全角スペースだけの入力は、アプリ側の入力チェックで受け付けない。
- 名前の一部検索では、このカラムを検索対象とする。

### 4.4 `detail`

- やることのメモを保存する。
- 任意項目とし、`NULL` を許可する。
- 最大255文字とする。
- ログにはこのカラムの内容を残さない。

### 4.5 `category`

- やることのジャンルを保存する。
- 必須項目とし、`NULL` を許可しない。
- 保存できる値は次の5つに限定する。
  - `デザイン`
  - `マーケティング`
  - `プログラミング`
  - `資格`
  - `就職活動`
- 自由入力ではなく、画面では5つの選択肢から選ぶ。
- ジャンル検索では、選択した値と完全一致する行を検索対象とする。

### 4.6 `priority`

- やることの優先度を数値で保存する。
- 必須項目とし、`NULL` を許可しない。
- 保存できる値は `1`、`2`、`3` に限定する。
- 値の意味は、`1`＝高、`2`＝中、`3`＝低とする。
- 初期値は `2`（中）とする。
- 画面では数値ではなく、高・中・低と表示する。

### 4.7 `due_date`

- やることの期限を保存する。
- `DATE` 型を使用し、日付だけを保存する。
- 任意項目とし、期限がない場合は `NULL` とする。
- 期限の近い順、または遠い順の並び替えに使用する。

### 4.8 `completed`

- やることが完了したかどうかを保存する。
- `BOOLEAN` 型を使用する。
- `FALSE` は未完了、`TRUE` は完了を表す。
- 必須項目とし、初期値は `FALSE` とする。
- 登録時は未完了とし、編集画面のチェック欄で変更できる。
- 画面では `TRUE` を「完了」、`FALSE` を「未完了」と表示する。

### 4.9 `created_at`

- やることを登録した日時を保存する。
- `DATETIME` 型を使用する。
- 必須項目とする。
- 登録時にMySQLが現在日時を自動で設定する。
- 利用者の入力欄には表示しない。

### 4.10 `updated_at`

- やることを最後に変更した日時を保存する。
- `DATETIME` 型を使用する。
- 必須項目とする。
- 登録時にMySQLが現在日時を設定し、編集で行が変更されたときにMySQLが現在日時へ更新する。
- 利用者の入力欄には表示しない。

## 5. テーブル作成SQL（DDL）

DDL（Data Definition Language、テーブルなどの構造を作るSQL）は次のとおりとする。

```sql
CREATE TABLE todos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    detail VARCHAR(255) NULL,
    category VARCHAR(255) NOT NULL,
    priority INT NOT NULL DEFAULT 2,
    due_date DATE NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_todos PRIMARY KEY (id),
    CONSTRAINT chk_todos_category CHECK (
        category IN (
            'デザイン',
            'マーケティング',
            'プログラミング',
            '資格',
            '就職活動'
        )
    ),
    CONSTRAINT chk_todos_priority CHECK (priority IN (1, 2, 3))
);
```

### 5.1 DDLの補足

- `NOT NULL` は、値なしを許可しない指定である。
- `NULL` は、値が設定されていない状態を表す。任意項目の `detail` と `due_date` で使用する。
- `DEFAULT` は、値を指定しなかったときに使う初期値である。
- `CHECK` は、保存できる値を条件で制限する仕組みである。
- `ON UPDATE CURRENT_TIMESTAMP` により、行を編集したときの `updated_at` をMySQLが更新する。
- このDDLはMySQL 8.0.16以降で `CHECK` 制約（値の条件）が有効になる環境を前提とする。画面側でも同じ選択肢と入力チェックを行う。

## 6. 要件とカラムの対応

requirements.md の要件が、どのカラムに対応するかを次に示す。

| 要件 | 対応カラム | 対応内容 |
|---|---|---|
| 4.1 一覧表示 | `title`, `category`, `priority`, `due_date`, `completed`, `id` | 一覧のやること・ジャンル・優先度・期限・状態・編集／削除対象を取得する |
| 4.2 新規登録 | `title`, `detail`, `category`, `priority`, `due_date` | 登録画面の5項目を保存する |
| 4.3 登録確認 | `title`, `detail`, `category`, `priority`, `due_date` | 登録前に表示する入力内容に対応する |
| 4.4 編集 | `id`, `title`, `detail`, `category`, `priority`, `due_date`, `completed` | `id` で対象行を特定し、6項目を変更する |
| 4.5 編集確認 | `id`, `title`, `detail`, `category`, `priority`, `due_date`, `completed` | 編集後の内容に対応する |
| 4.6 削除確認 | `id`, `title`, `category`, `due_date` | `id` で対象行を特定し、確認表示に必要な値を取得する |
| 4.7 絞り込み | `title`, `category` | `title` は一部一致、`category` は完全一致で検索する |
| 4.8 並び替え | `due_date` | 期限の昇順（近い順）または降順（遠い順）で並べる |
| 4.9 操作結果の表示 | `id` | 登録・編集・削除の対象を特定する。表示文そのものは画面側で扱う |
| 4.10 ログへの記録 | `id` | 登録・編集・削除の対象IDをアプリケーションログへ記録する |
| 6.1 入力チェック | `title`, `detail`, `category`, `priority` | 必須、文字数、選択値の条件を確認する |
| 6.2 存在しない番号 | `id` | 指定された番号の行が存在するか確認する |
| 5.2 自動管理する項目 | `id`, `created_at`, `updated_at` | 利用者の入力対象外として自動管理する |

## 7. 保存値と画面表示の対応

データベースには次の値を保存し、画面では利用者向けの言葉に置き換えて表示する。

| カラム | 保存値 | 画面表示 |
|---|---|---|
| `priority` | `1` | 高 |
| `priority` | `2` | 中 |
| `priority` | `3` | 低 |
| `completed` | `TRUE`（MySQL上では1相当） | 完了 |
| `completed` | `FALSE`（MySQL上では0相当） | 未完了 |
| `detail` | `NULL` | （入力なし）など、画面側で指定された表示 |
| `due_date` | `NULL` | 期限なしなど、画面側で指定された表示 |

## 8. データ操作の決まり

### 8.1 登録

- `title`、`detail`、`category`、`priority`、`due_date` を受け取り、`todos` に1行追加する。
- `id`、`created_at`、`updated_at` は指定せず、MySQLに自動設定させる。
- `completed` は指定しない場合、初期値 `FALSE` になる。

### 8.2 編集

- URLに含まれる `id` で対象の1行を特定する。
- `title`、`detail`、`category`、`priority`、`due_date`、`completed` を更新する。
- `id` と `created_at` は変更しない。
- `updated_at` はMySQLが自動更新する。

### 8.3 削除

- URLに含まれる `id` で対象の1行を特定する。
- 対象の1行を削除する。
- 削除前に、画面側でやること・ジャンル・期限を確認表示する。

### 8.4 検索と並び替え

- 名前検索は `title` の一部一致で行う。
- ジャンル検索は `category` の完全一致で行う。
- 期限が近い順は `due_date` の昇順、期限が遠い順は降順とする。
- 検索条件が空の場合、その条件では絞り込まない。

## 9. 対象外とするDB設計

次のテーブル・カラム・仕組みは作成しない。

- 利用者テーブル、ログイン情報、パスワード
- 複数利用者を結び付けるカラム
- 操作ログを保存する専用テーブル
- 削除データを保管する履歴テーブル
- 完了日時を保存するカラム
- 写真・ファイルの保存用カラムまたはテーブル
- ページ分け専用のカラムまたはテーブル

## 10. 叩き台にない補足・設計判断

以下は、元の仕様に書かれた内容をデータベースで実現するための補足であり、新しい画面機能ではない。

- `category` と `priority` の値を守るため、DDLに `CHECK` 制約を追加した。
- 文字化けを防ぐための文字コードや、検索用の追加インデックス（検索を速くする仕組み）は、使用するMySQL環境や実装方法が未指定のため本書では固定していない。
- 操作ログは「使用するテーブルは `todos` の1つだけ」という前提に合わせ、DBのテーブルではなくアプリケーションログとして扱うことを明記した。
- SQLの制約名（`pk_todos`、`chk_todos_category`、`chk_todos_priority`）は、DDL内の制約を識別しやすくするために付けた名前である。
