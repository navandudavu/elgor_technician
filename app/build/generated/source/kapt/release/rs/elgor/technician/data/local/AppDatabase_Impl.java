package rs.elgor.technician.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile JobDao _jobDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `jobs` (`id` INTEGER NOT NULL, `customerId` INTEGER NOT NULL, `applianceId` INTEGER, `assignedTo` INTEGER, `createdBy` INTEGER, `title` TEXT NOT NULL, `description` TEXT, `status` TEXT NOT NULL, `priority` TEXT NOT NULL, `scheduledAt` TEXT, `finishedAt` TEXT, `address` TEXT, `hoursLogged` REAL, `createdAt` TEXT NOT NULL, `updatedAt` TEXT NOT NULL, `customerName` TEXT, `customerPhone` TEXT, `applianceType` TEXT, `applianceBrand` TEXT, `applianceModel` TEXT, `applianceSerial` TEXT, `assignedToName` TEXT, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `job_notes` (`id` INTEGER NOT NULL, `jobId` INTEGER NOT NULL, `authorId` INTEGER, `note` TEXT NOT NULL, `createdAt` TEXT NOT NULL, `authorName` TEXT, PRIMARY KEY(`id`), FOREIGN KEY(`jobId`) REFERENCES `jobs`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_job_notes_jobId` ON `job_notes` (`jobId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `job_photos` (`id` INTEGER NOT NULL, `jobId` INTEGER NOT NULL, `uploadedBy` INTEGER, `filename` TEXT NOT NULL, `originalName` TEXT, `caption` TEXT, `category` TEXT NOT NULL, `createdAt` TEXT NOT NULL, `uploadedByName` TEXT, PRIMARY KEY(`id`), FOREIGN KEY(`jobId`) REFERENCES `jobs`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_job_photos_jobId` ON `job_photos` (`jobId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'f4abab123f77ea565e40f3a507eca214')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `jobs`");
        db.execSQL("DROP TABLE IF EXISTS `job_notes`");
        db.execSQL("DROP TABLE IF EXISTS `job_photos`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsJobs = new HashMap<String, TableInfo.Column>(22);
        _columnsJobs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobs.put("customerId", new TableInfo.Column("customerId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobs.put("applianceId", new TableInfo.Column("applianceId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobs.put("assignedTo", new TableInfo.Column("assignedTo", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobs.put("createdBy", new TableInfo.Column("createdBy", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobs.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobs.put("description", new TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobs.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobs.put("priority", new TableInfo.Column("priority", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobs.put("scheduledAt", new TableInfo.Column("scheduledAt", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobs.put("finishedAt", new TableInfo.Column("finishedAt", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobs.put("address", new TableInfo.Column("address", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobs.put("hoursLogged", new TableInfo.Column("hoursLogged", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobs.put("createdAt", new TableInfo.Column("createdAt", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobs.put("updatedAt", new TableInfo.Column("updatedAt", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobs.put("customerName", new TableInfo.Column("customerName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobs.put("customerPhone", new TableInfo.Column("customerPhone", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobs.put("applianceType", new TableInfo.Column("applianceType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobs.put("applianceBrand", new TableInfo.Column("applianceBrand", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobs.put("applianceModel", new TableInfo.Column("applianceModel", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobs.put("applianceSerial", new TableInfo.Column("applianceSerial", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobs.put("assignedToName", new TableInfo.Column("assignedToName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysJobs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesJobs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoJobs = new TableInfo("jobs", _columnsJobs, _foreignKeysJobs, _indicesJobs);
        final TableInfo _existingJobs = TableInfo.read(db, "jobs");
        if (!_infoJobs.equals(_existingJobs)) {
          return new RoomOpenHelper.ValidationResult(false, "jobs(rs.elgor.technician.data.local.JobEntity).\n"
                  + " Expected:\n" + _infoJobs + "\n"
                  + " Found:\n" + _existingJobs);
        }
        final HashMap<String, TableInfo.Column> _columnsJobNotes = new HashMap<String, TableInfo.Column>(6);
        _columnsJobNotes.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobNotes.put("jobId", new TableInfo.Column("jobId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobNotes.put("authorId", new TableInfo.Column("authorId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobNotes.put("note", new TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobNotes.put("createdAt", new TableInfo.Column("createdAt", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobNotes.put("authorName", new TableInfo.Column("authorName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysJobNotes = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysJobNotes.add(new TableInfo.ForeignKey("jobs", "CASCADE", "NO ACTION", Arrays.asList("jobId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesJobNotes = new HashSet<TableInfo.Index>(1);
        _indicesJobNotes.add(new TableInfo.Index("index_job_notes_jobId", false, Arrays.asList("jobId"), Arrays.asList("ASC")));
        final TableInfo _infoJobNotes = new TableInfo("job_notes", _columnsJobNotes, _foreignKeysJobNotes, _indicesJobNotes);
        final TableInfo _existingJobNotes = TableInfo.read(db, "job_notes");
        if (!_infoJobNotes.equals(_existingJobNotes)) {
          return new RoomOpenHelper.ValidationResult(false, "job_notes(rs.elgor.technician.data.local.JobNoteEntity).\n"
                  + " Expected:\n" + _infoJobNotes + "\n"
                  + " Found:\n" + _existingJobNotes);
        }
        final HashMap<String, TableInfo.Column> _columnsJobPhotos = new HashMap<String, TableInfo.Column>(9);
        _columnsJobPhotos.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobPhotos.put("jobId", new TableInfo.Column("jobId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobPhotos.put("uploadedBy", new TableInfo.Column("uploadedBy", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobPhotos.put("filename", new TableInfo.Column("filename", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobPhotos.put("originalName", new TableInfo.Column("originalName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobPhotos.put("caption", new TableInfo.Column("caption", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobPhotos.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobPhotos.put("createdAt", new TableInfo.Column("createdAt", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobPhotos.put("uploadedByName", new TableInfo.Column("uploadedByName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysJobPhotos = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysJobPhotos.add(new TableInfo.ForeignKey("jobs", "CASCADE", "NO ACTION", Arrays.asList("jobId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesJobPhotos = new HashSet<TableInfo.Index>(1);
        _indicesJobPhotos.add(new TableInfo.Index("index_job_photos_jobId", false, Arrays.asList("jobId"), Arrays.asList("ASC")));
        final TableInfo _infoJobPhotos = new TableInfo("job_photos", _columnsJobPhotos, _foreignKeysJobPhotos, _indicesJobPhotos);
        final TableInfo _existingJobPhotos = TableInfo.read(db, "job_photos");
        if (!_infoJobPhotos.equals(_existingJobPhotos)) {
          return new RoomOpenHelper.ValidationResult(false, "job_photos(rs.elgor.technician.data.local.JobPhotoEntity).\n"
                  + " Expected:\n" + _infoJobPhotos + "\n"
                  + " Found:\n" + _existingJobPhotos);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "f4abab123f77ea565e40f3a507eca214", "2126ac05126350f1d8890ee285ddccc5");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "jobs","job_notes","job_photos");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `jobs`");
      _db.execSQL("DELETE FROM `job_notes`");
      _db.execSQL("DELETE FROM `job_photos`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(JobDao.class, JobDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public JobDao jobDao() {
    if (_jobDao != null) {
      return _jobDao;
    } else {
      synchronized(this) {
        if(_jobDao == null) {
          _jobDao = new JobDao_Impl(this);
        }
        return _jobDao;
      }
    }
  }
}
