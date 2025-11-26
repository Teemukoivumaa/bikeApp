package com.example.bikeapp.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add a new column to the "strava_activities" table
        db.execSQL("ALTER TABLE strava_activities ADD COLUMN activity_end_time TEXT NOT NULL DEFAULT ''")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add new columns to the "strava_activities" table
        // description: String?,
        // calories: Float?,
        // sportType: String,
        // elev_high: Float,
        // elev_low: Float,
        // device_name: String?,
        // average_heartrate: Float?,
        // max_heartrate: Float?,

        db.execSQL("ALTER TABLE strava_activities ADD COLUMN description TEXT")
        db.execSQL("ALTER TABLE strava_activities ADD COLUMN calories REAL")
        db.execSQL("ALTER TABLE strava_activities ADD COLUMN sport_type TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE strava_activities ADD COLUMN elev_high REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE strava_activities ADD COLUMN elev_low REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE strava_activities ADD COLUMN device_name TEXT")
        db.execSQL("ALTER TABLE strava_activities ADD COLUMN average_heartrate REAL")
        db.execSQL("ALTER TABLE strava_activities ADD COLUMN max_heartrate REAL")

        // Add new table "activity_locations"

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS "activity_locations" (
                "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                "activity_id" INTEGER NOT NULL,
                "latitude" REAL NOT NULL,
                "longitude" REAL NOT NULL,
                "coordinatesAsString" TEXT NOT NULL,
                "type" TEXT NOT NULL,
                FOREIGN KEY("activity_id") REFERENCES "strava_activities"("id") ON UPDATE NO ACTION ON DELETE CASCADE
                )
            """
        )
        db.execSQL(
            """
                CREATE INDEX IF NOT EXISTS "index_activity_locations_activity_id"
                ON "activity_locations" ("activity_id" ASC)
            """
        )

    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add new column to "strava_activities" table
        // fullInfoFetched: Boolean = false
        db.execSQL("ALTER TABLE strava_activities ADD COLUMN full_info_fetched INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add new table "challenges"
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS "challenges" (
                "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                "name" TEXT NOT NULL,
                "description" TEXT NOT NULL,
                "goal" REAL NOT NULL,
                "current_progress" REAL NOT NULL,
                "unit" TEXT NOT NULL,
                "start_date" INTEGER NOT NULL,
                "end_date" INTEGER NOT NULL,
                "is_active" INTEGER NOT NULL DEFAULT 1,
                "is_completed" INTEGER NOT NULL DEFAULT 0,
                "recurring" TEXT DEFAULT NULL
                )
            """
        )
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add new column to "strava_activities" table
        // summary_polyline: String?
        db.execSQL("ALTER TABLE strava_activities ADD COLUMN summary_polyline TEXT")
    }
}