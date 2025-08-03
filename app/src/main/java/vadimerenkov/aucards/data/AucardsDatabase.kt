package vadimerenkov.aucards.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.ColumnInfo
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import vadimerenkov.aucards.converters.Converters
import kotlin.reflect.KClass

@Database(
	encryptionKey = "aucards_encryption_key".toByteArray(),
	encryptionKeyAlias = "aucards_key_alias",
	entities = [Aucard::class],
	exportSchema = true,
	version = 4,
	autoMigrations = [
		AutoMigration(2, 3,
			spec = AucardsDatabase.Migrations::class
		)
	]
)
@TypeConverters(Converters::class)
abstract class AucardsDatabase: RoomDatabase() {
	abstract fun aucardDao(): AucardDao

	@DeleteColumn(
		"Aucard",
		"title"
	)
	class Migrations : AutoMigrationSpec

    companion object {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Aucard ADD COLUMN imageUri TEXT DEFAULT ''")
            }
        }
    }


	companion object {
		@Volatile
		private var Instance: AucardsDatabase? = null

		fun getDatabase(context: Context): AucardsDatabase {
			return Instance ?: synchronized(this) {
				Room.databaseBuilder(context, AucardsDatabase::class.java, "aucards_database")
					.addMigrations(MIGRATION_3_4)
					//.fallbackToDestructiveMigration()
					.build()
					.also { Instance = it }
			}
		}

	}
}