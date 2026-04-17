# 習慣チェックボードアプリ 設計書

## アプリ概要

毎日の習慣をトグルボタンで管理するチェックボードアプリ。
実行状況をホーム画面ウィジェットで確認できる。

---

## アプリ全体像

```
習慣チェックボードアプリ
├── メイン画面          … 習慣一覧 + トグルボタン
├── 習慣管理画面        … 習慣の追加・編集・削除
└── ホーム画面ウィジェット … 当日の達成状況を表示
```

---

## アーキテクチャ

| レイヤー | 技術 |
|---------|------|
| UI | Jetpack Compose |
| 状態管理 | ViewModel |
| データアクセス | Repository |
| ローカルDB | Room |
| ウィジェット軽量設定 | DataStore |
| ウィジェットUI | Glance（Compose風API） |

---

## データ設計

```kotlin
// 習慣マスタ
@Entity
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val order: Int
)

// 毎日の実行記録
@Entity(primaryKeys = ["habitId", "date"])
data class HabitRecord(
    val habitId: Int,
    val date: LocalDate,   // "2026-04-15" 形式
    val isDone: Boolean
)
```

---

## 依存ライブラリ（build.gradle.kts）

```kotlin
dependencies {
    // Room (DB)
    implementation("androidx.room:room-runtime:2.7.0")
    implementation("androidx.room:room-ktx:2.7.0")
    ksp("androidx.room:room-compiler:2.7.0")

    // Glance (ウィジェット)
    implementation("androidx.glance:glance-appwidget:1.1.1")
    implementation("androidx.glance:glance-material3:1.1.1")

    // ViewModel + Coroutines
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.9.0")
}
```

### KSPプラグイン設定

```kotlin
// build.gradle.kts (project level)
plugins {
    id("com.google.devtools.ksp") version "2.1.20-1.0.32" apply false
}

// build.gradle.kts (app level)
plugins {
    id("com.google.devtools.ksp")
}
```

---

## 画面設計

### メイン画面

```
[今日 2026/04/15]

 ○ 朝のストレッチ     [✓ 完了]
 ○ 読書 30分          [  未完]
 ○ 日記を書く         [✓ 完了]

 [+ 習慣を追加]
```

### ホーム画面ウィジェット

```
┌─────────────────┐
│ 今日の習慣  3/5  │
│ ✓ 朝のストレッチ │
│ ✓ 読書 30分      │
│ ー 日記を書く    │
└─────────────────┘
```

---

## ウィジェット実装の注意点

- クリックは `action*()` 系のAPIを使う（通常の `onClick` は不可）
- ウィジェットからトグル操作する場合は `ActionCallback` 経由で処理
- DB更新後に `GlanceAppWidgetManager.update()` を呼んでウィジェットを再描画

---

## 開発ステップ

| ステップ | 内容 |
|---------|------|
| Step 1 | Room DB のセットアップ（Habit / HabitRecord テーブル） |
| Step 2 | Repository + ViewModel 作成 |
| Step 3 | メイン画面UI（習慣一覧 + トグルボタン） |
| Step 4 | 習慣追加・編集・削除 |
| Step 5 | Glance でウィジェット実装 |
| Step 6 | DB更新 → ウィジェット自動更新の連携 |
