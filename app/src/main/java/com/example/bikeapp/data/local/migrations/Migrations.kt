package com.example.bikeapp.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add a new column to the "strava_activities" table
        database.execSQL("ALTER TABLE strava_activities ADD COLUMN activity_end_time TEXT NOT NULL DEFAULT ''")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add new columns to the "strava_activities" table
        // description: String?,
        // calories: Float?,
        // sportType: String,
        // elev_high: Float,
        // elev_low: Float,
        // device_name: String?,
        // average_heartrate: Float?,
        // max_heartrate: Float?,

        database.execSQL("ALTER TABLE strava_activities ADD COLUMN description TEXT")
        database.execSQL("ALTER TABLE strava_activities ADD COLUMN calories REAL")
        database.execSQL("ALTER TABLE strava_activities ADD COLUMN sport_type TEXT NOT NULL DEFAULT ''")
        database.execSQL("ALTER TABLE strava_activities ADD COLUMN elev_high REAL NOT NULL DEFAULT 0.0")
        database.execSQL("ALTER TABLE strava_activities ADD COLUMN elev_low REAL NOT NULL DEFAULT 0.0")
        database.execSQL("ALTER TABLE strava_activities ADD COLUMN device_name TEXT")
        database.execSQL("ALTER TABLE strava_activities ADD COLUMN average_heartrate REAL")
        database.execSQL("ALTER TABLE strava_activities ADD COLUMN max_heartrate REAL")

        // Add new table "activity_locations"

        database.execSQL(
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
        database.execSQL(
            """
                CREATE INDEX IF NOT EXISTS "index_activity_locations_activity_id"
                ON "activity_locations" ("activity_id" ASC)
            """
        )

    }
}