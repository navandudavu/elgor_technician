package rs.elgor.technician.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@SuppressWarnings({"unchecked", "deprecation"})
public final class JobDao_Impl implements JobDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<JobEntity> __insertionAdapterOfJobEntity;

  private final EntityInsertionAdapter<JobNoteEntity> __insertionAdapterOfJobNoteEntity;

  private final EntityInsertionAdapter<JobPhotoEntity> __insertionAdapterOfJobPhotoEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllJobs;

  private final SharedSQLiteStatement __preparedStmtOfDeleteNotesForJob;

  private final SharedSQLiteStatement __preparedStmtOfDeletePhotosForJob;

  public JobDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfJobEntity = new EntityInsertionAdapter<JobEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `jobs` (`id`,`customerId`,`applianceId`,`assignedTo`,`createdBy`,`title`,`description`,`status`,`priority`,`scheduledAt`,`finishedAt`,`address`,`hoursLogged`,`createdAt`,`updatedAt`,`customerName`,`customerPhone`,`applianceType`,`applianceBrand`,`applianceModel`,`applianceSerial`,`assignedToName`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final JobEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCustomerId());
        if (entity.getApplianceId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getApplianceId());
        }
        if (entity.getAssignedTo() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getAssignedTo());
        }
        if (entity.getCreatedBy() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getCreatedBy());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getTitle());
        }
        if (entity.getDescription() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getDescription());
        }
        if (entity.getStatus() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getStatus());
        }
        if (entity.getPriority() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getPriority());
        }
        if (entity.getScheduledAt() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getScheduledAt());
        }
        if (entity.getFinishedAt() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getFinishedAt());
        }
        if (entity.getAddress() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getAddress());
        }
        if (entity.getHoursLogged() == null) {
          statement.bindNull(13);
        } else {
          statement.bindDouble(13, entity.getHoursLogged());
        }
        if (entity.getCreatedAt() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getCreatedAt());
        }
        if (entity.getUpdatedAt() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getUpdatedAt());
        }
        if (entity.getCustomerName() == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.getCustomerName());
        }
        if (entity.getCustomerPhone() == null) {
          statement.bindNull(17);
        } else {
          statement.bindString(17, entity.getCustomerPhone());
        }
        if (entity.getApplianceType() == null) {
          statement.bindNull(18);
        } else {
          statement.bindString(18, entity.getApplianceType());
        }
        if (entity.getApplianceBrand() == null) {
          statement.bindNull(19);
        } else {
          statement.bindString(19, entity.getApplianceBrand());
        }
        if (entity.getApplianceModel() == null) {
          statement.bindNull(20);
        } else {
          statement.bindString(20, entity.getApplianceModel());
        }
        if (entity.getApplianceSerial() == null) {
          statement.bindNull(21);
        } else {
          statement.bindString(21, entity.getApplianceSerial());
        }
        if (entity.getAssignedToName() == null) {
          statement.bindNull(22);
        } else {
          statement.bindString(22, entity.getAssignedToName());
        }
      }
    };
    this.__insertionAdapterOfJobNoteEntity = new EntityInsertionAdapter<JobNoteEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `job_notes` (`id`,`jobId`,`authorId`,`note`,`createdAt`,`authorName`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final JobNoteEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getJobId());
        if (entity.getAuthorId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getAuthorId());
        }
        if (entity.getNote() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getNote());
        }
        if (entity.getCreatedAt() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getCreatedAt());
        }
        if (entity.getAuthorName() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getAuthorName());
        }
      }
    };
    this.__insertionAdapterOfJobPhotoEntity = new EntityInsertionAdapter<JobPhotoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `job_photos` (`id`,`jobId`,`uploadedBy`,`filename`,`originalName`,`caption`,`category`,`createdAt`,`uploadedByName`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final JobPhotoEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getJobId());
        if (entity.getUploadedBy() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getUploadedBy());
        }
        if (entity.getFilename() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getFilename());
        }
        if (entity.getOriginalName() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getOriginalName());
        }
        if (entity.getCaption() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getCaption());
        }
        if (entity.getCategory() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getCategory());
        }
        if (entity.getCreatedAt() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getCreatedAt());
        }
        if (entity.getUploadedByName() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getUploadedByName());
        }
      }
    };
    this.__preparedStmtOfDeleteAllJobs = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM jobs";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteNotesForJob = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM job_notes WHERE jobId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeletePhotosForJob = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM job_photos WHERE jobId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertJobs(final List<JobEntity> jobs,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfJobEntity.insert(jobs);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertJob(final JobEntity job, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfJobEntity.insert(job);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertNotes(final List<JobNoteEntity> notes,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfJobNoteEntity.insert(notes);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertPhotos(final List<JobPhotoEntity> photos,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfJobPhotoEntity.insert(photos);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object replaceJobs(final List<JobEntity> jobs,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> JobDao.DefaultImpls.replaceJobs(JobDao_Impl.this, jobs, __cont), $completion);
  }

  @Override
  public Object deleteAllJobs(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllJobs.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAllJobs.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteNotesForJob(final int jobId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteNotesForJob.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, jobId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteNotesForJob.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePhotosForJob(final int jobId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeletePhotosForJob.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, jobId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeletePhotosForJob.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<JobEntity>> getAllJobs() {
    final String _sql = "SELECT * FROM jobs ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"jobs"}, new Callable<List<JobEntity>>() {
      @Override
      @NonNull
      public List<JobEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCustomerId = CursorUtil.getColumnIndexOrThrow(_cursor, "customerId");
          final int _cursorIndexOfApplianceId = CursorUtil.getColumnIndexOrThrow(_cursor, "applianceId");
          final int _cursorIndexOfAssignedTo = CursorUtil.getColumnIndexOrThrow(_cursor, "assignedTo");
          final int _cursorIndexOfCreatedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "createdBy");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfScheduledAt = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledAt");
          final int _cursorIndexOfFinishedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "finishedAt");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfHoursLogged = CursorUtil.getColumnIndexOrThrow(_cursor, "hoursLogged");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customerName");
          final int _cursorIndexOfCustomerPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "customerPhone");
          final int _cursorIndexOfApplianceType = CursorUtil.getColumnIndexOrThrow(_cursor, "applianceType");
          final int _cursorIndexOfApplianceBrand = CursorUtil.getColumnIndexOrThrow(_cursor, "applianceBrand");
          final int _cursorIndexOfApplianceModel = CursorUtil.getColumnIndexOrThrow(_cursor, "applianceModel");
          final int _cursorIndexOfApplianceSerial = CursorUtil.getColumnIndexOrThrow(_cursor, "applianceSerial");
          final int _cursorIndexOfAssignedToName = CursorUtil.getColumnIndexOrThrow(_cursor, "assignedToName");
          final List<JobEntity> _result = new ArrayList<JobEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final JobEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCustomerId;
            _tmpCustomerId = _cursor.getInt(_cursorIndexOfCustomerId);
            final Integer _tmpApplianceId;
            if (_cursor.isNull(_cursorIndexOfApplianceId)) {
              _tmpApplianceId = null;
            } else {
              _tmpApplianceId = _cursor.getInt(_cursorIndexOfApplianceId);
            }
            final Integer _tmpAssignedTo;
            if (_cursor.isNull(_cursorIndexOfAssignedTo)) {
              _tmpAssignedTo = null;
            } else {
              _tmpAssignedTo = _cursor.getInt(_cursorIndexOfAssignedTo);
            }
            final Integer _tmpCreatedBy;
            if (_cursor.isNull(_cursorIndexOfCreatedBy)) {
              _tmpCreatedBy = null;
            } else {
              _tmpCreatedBy = _cursor.getInt(_cursorIndexOfCreatedBy);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpStatus;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmpStatus = null;
            } else {
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            }
            final String _tmpPriority;
            if (_cursor.isNull(_cursorIndexOfPriority)) {
              _tmpPriority = null;
            } else {
              _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            }
            final String _tmpScheduledAt;
            if (_cursor.isNull(_cursorIndexOfScheduledAt)) {
              _tmpScheduledAt = null;
            } else {
              _tmpScheduledAt = _cursor.getString(_cursorIndexOfScheduledAt);
            }
            final String _tmpFinishedAt;
            if (_cursor.isNull(_cursorIndexOfFinishedAt)) {
              _tmpFinishedAt = null;
            } else {
              _tmpFinishedAt = _cursor.getString(_cursorIndexOfFinishedAt);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final Double _tmpHoursLogged;
            if (_cursor.isNull(_cursorIndexOfHoursLogged)) {
              _tmpHoursLogged = null;
            } else {
              _tmpHoursLogged = _cursor.getDouble(_cursorIndexOfHoursLogged);
            }
            final String _tmpCreatedAt;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmpCreatedAt = null;
            } else {
              _tmpCreatedAt = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final String _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getString(_cursorIndexOfUpdatedAt);
            }
            final String _tmpCustomerName;
            if (_cursor.isNull(_cursorIndexOfCustomerName)) {
              _tmpCustomerName = null;
            } else {
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            }
            final String _tmpCustomerPhone;
            if (_cursor.isNull(_cursorIndexOfCustomerPhone)) {
              _tmpCustomerPhone = null;
            } else {
              _tmpCustomerPhone = _cursor.getString(_cursorIndexOfCustomerPhone);
            }
            final String _tmpApplianceType;
            if (_cursor.isNull(_cursorIndexOfApplianceType)) {
              _tmpApplianceType = null;
            } else {
              _tmpApplianceType = _cursor.getString(_cursorIndexOfApplianceType);
            }
            final String _tmpApplianceBrand;
            if (_cursor.isNull(_cursorIndexOfApplianceBrand)) {
              _tmpApplianceBrand = null;
            } else {
              _tmpApplianceBrand = _cursor.getString(_cursorIndexOfApplianceBrand);
            }
            final String _tmpApplianceModel;
            if (_cursor.isNull(_cursorIndexOfApplianceModel)) {
              _tmpApplianceModel = null;
            } else {
              _tmpApplianceModel = _cursor.getString(_cursorIndexOfApplianceModel);
            }
            final String _tmpApplianceSerial;
            if (_cursor.isNull(_cursorIndexOfApplianceSerial)) {
              _tmpApplianceSerial = null;
            } else {
              _tmpApplianceSerial = _cursor.getString(_cursorIndexOfApplianceSerial);
            }
            final String _tmpAssignedToName;
            if (_cursor.isNull(_cursorIndexOfAssignedToName)) {
              _tmpAssignedToName = null;
            } else {
              _tmpAssignedToName = _cursor.getString(_cursorIndexOfAssignedToName);
            }
            _item = new JobEntity(_tmpId,_tmpCustomerId,_tmpApplianceId,_tmpAssignedTo,_tmpCreatedBy,_tmpTitle,_tmpDescription,_tmpStatus,_tmpPriority,_tmpScheduledAt,_tmpFinishedAt,_tmpAddress,_tmpHoursLogged,_tmpCreatedAt,_tmpUpdatedAt,_tmpCustomerName,_tmpCustomerPhone,_tmpApplianceType,_tmpApplianceBrand,_tmpApplianceModel,_tmpApplianceSerial,_tmpAssignedToName);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<JobEntity> getJobById(final int jobId) {
    final String _sql = "SELECT * FROM jobs WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, jobId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"jobs"}, new Callable<JobEntity>() {
      @Override
      @Nullable
      public JobEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCustomerId = CursorUtil.getColumnIndexOrThrow(_cursor, "customerId");
          final int _cursorIndexOfApplianceId = CursorUtil.getColumnIndexOrThrow(_cursor, "applianceId");
          final int _cursorIndexOfAssignedTo = CursorUtil.getColumnIndexOrThrow(_cursor, "assignedTo");
          final int _cursorIndexOfCreatedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "createdBy");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfScheduledAt = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledAt");
          final int _cursorIndexOfFinishedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "finishedAt");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfHoursLogged = CursorUtil.getColumnIndexOrThrow(_cursor, "hoursLogged");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customerName");
          final int _cursorIndexOfCustomerPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "customerPhone");
          final int _cursorIndexOfApplianceType = CursorUtil.getColumnIndexOrThrow(_cursor, "applianceType");
          final int _cursorIndexOfApplianceBrand = CursorUtil.getColumnIndexOrThrow(_cursor, "applianceBrand");
          final int _cursorIndexOfApplianceModel = CursorUtil.getColumnIndexOrThrow(_cursor, "applianceModel");
          final int _cursorIndexOfApplianceSerial = CursorUtil.getColumnIndexOrThrow(_cursor, "applianceSerial");
          final int _cursorIndexOfAssignedToName = CursorUtil.getColumnIndexOrThrow(_cursor, "assignedToName");
          final JobEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCustomerId;
            _tmpCustomerId = _cursor.getInt(_cursorIndexOfCustomerId);
            final Integer _tmpApplianceId;
            if (_cursor.isNull(_cursorIndexOfApplianceId)) {
              _tmpApplianceId = null;
            } else {
              _tmpApplianceId = _cursor.getInt(_cursorIndexOfApplianceId);
            }
            final Integer _tmpAssignedTo;
            if (_cursor.isNull(_cursorIndexOfAssignedTo)) {
              _tmpAssignedTo = null;
            } else {
              _tmpAssignedTo = _cursor.getInt(_cursorIndexOfAssignedTo);
            }
            final Integer _tmpCreatedBy;
            if (_cursor.isNull(_cursorIndexOfCreatedBy)) {
              _tmpCreatedBy = null;
            } else {
              _tmpCreatedBy = _cursor.getInt(_cursorIndexOfCreatedBy);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpStatus;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmpStatus = null;
            } else {
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            }
            final String _tmpPriority;
            if (_cursor.isNull(_cursorIndexOfPriority)) {
              _tmpPriority = null;
            } else {
              _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            }
            final String _tmpScheduledAt;
            if (_cursor.isNull(_cursorIndexOfScheduledAt)) {
              _tmpScheduledAt = null;
            } else {
              _tmpScheduledAt = _cursor.getString(_cursorIndexOfScheduledAt);
            }
            final String _tmpFinishedAt;
            if (_cursor.isNull(_cursorIndexOfFinishedAt)) {
              _tmpFinishedAt = null;
            } else {
              _tmpFinishedAt = _cursor.getString(_cursorIndexOfFinishedAt);
            }
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            final Double _tmpHoursLogged;
            if (_cursor.isNull(_cursorIndexOfHoursLogged)) {
              _tmpHoursLogged = null;
            } else {
              _tmpHoursLogged = _cursor.getDouble(_cursorIndexOfHoursLogged);
            }
            final String _tmpCreatedAt;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmpCreatedAt = null;
            } else {
              _tmpCreatedAt = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final String _tmpUpdatedAt;
            if (_cursor.isNull(_cursorIndexOfUpdatedAt)) {
              _tmpUpdatedAt = null;
            } else {
              _tmpUpdatedAt = _cursor.getString(_cursorIndexOfUpdatedAt);
            }
            final String _tmpCustomerName;
            if (_cursor.isNull(_cursorIndexOfCustomerName)) {
              _tmpCustomerName = null;
            } else {
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            }
            final String _tmpCustomerPhone;
            if (_cursor.isNull(_cursorIndexOfCustomerPhone)) {
              _tmpCustomerPhone = null;
            } else {
              _tmpCustomerPhone = _cursor.getString(_cursorIndexOfCustomerPhone);
            }
            final String _tmpApplianceType;
            if (_cursor.isNull(_cursorIndexOfApplianceType)) {
              _tmpApplianceType = null;
            } else {
              _tmpApplianceType = _cursor.getString(_cursorIndexOfApplianceType);
            }
            final String _tmpApplianceBrand;
            if (_cursor.isNull(_cursorIndexOfApplianceBrand)) {
              _tmpApplianceBrand = null;
            } else {
              _tmpApplianceBrand = _cursor.getString(_cursorIndexOfApplianceBrand);
            }
            final String _tmpApplianceModel;
            if (_cursor.isNull(_cursorIndexOfApplianceModel)) {
              _tmpApplianceModel = null;
            } else {
              _tmpApplianceModel = _cursor.getString(_cursorIndexOfApplianceModel);
            }
            final String _tmpApplianceSerial;
            if (_cursor.isNull(_cursorIndexOfApplianceSerial)) {
              _tmpApplianceSerial = null;
            } else {
              _tmpApplianceSerial = _cursor.getString(_cursorIndexOfApplianceSerial);
            }
            final String _tmpAssignedToName;
            if (_cursor.isNull(_cursorIndexOfAssignedToName)) {
              _tmpAssignedToName = null;
            } else {
              _tmpAssignedToName = _cursor.getString(_cursorIndexOfAssignedToName);
            }
            _result = new JobEntity(_tmpId,_tmpCustomerId,_tmpApplianceId,_tmpAssignedTo,_tmpCreatedBy,_tmpTitle,_tmpDescription,_tmpStatus,_tmpPriority,_tmpScheduledAt,_tmpFinishedAt,_tmpAddress,_tmpHoursLogged,_tmpCreatedAt,_tmpUpdatedAt,_tmpCustomerName,_tmpCustomerPhone,_tmpApplianceType,_tmpApplianceBrand,_tmpApplianceModel,_tmpApplianceSerial,_tmpAssignedToName);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<JobNoteEntity>> getNotesForJob(final int jobId) {
    final String _sql = "SELECT * FROM job_notes WHERE jobId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, jobId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"job_notes"}, new Callable<List<JobNoteEntity>>() {
      @Override
      @NonNull
      public List<JobNoteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfJobId = CursorUtil.getColumnIndexOrThrow(_cursor, "jobId");
          final int _cursorIndexOfAuthorId = CursorUtil.getColumnIndexOrThrow(_cursor, "authorId");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfAuthorName = CursorUtil.getColumnIndexOrThrow(_cursor, "authorName");
          final List<JobNoteEntity> _result = new ArrayList<JobNoteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final JobNoteEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpJobId;
            _tmpJobId = _cursor.getInt(_cursorIndexOfJobId);
            final Integer _tmpAuthorId;
            if (_cursor.isNull(_cursorIndexOfAuthorId)) {
              _tmpAuthorId = null;
            } else {
              _tmpAuthorId = _cursor.getInt(_cursorIndexOfAuthorId);
            }
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            final String _tmpCreatedAt;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmpCreatedAt = null;
            } else {
              _tmpCreatedAt = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final String _tmpAuthorName;
            if (_cursor.isNull(_cursorIndexOfAuthorName)) {
              _tmpAuthorName = null;
            } else {
              _tmpAuthorName = _cursor.getString(_cursorIndexOfAuthorName);
            }
            _item = new JobNoteEntity(_tmpId,_tmpJobId,_tmpAuthorId,_tmpNote,_tmpCreatedAt,_tmpAuthorName);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<JobPhotoEntity>> getPhotosForJob(final int jobId) {
    final String _sql = "SELECT * FROM job_photos WHERE jobId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, jobId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"job_photos"}, new Callable<List<JobPhotoEntity>>() {
      @Override
      @NonNull
      public List<JobPhotoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfJobId = CursorUtil.getColumnIndexOrThrow(_cursor, "jobId");
          final int _cursorIndexOfUploadedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "uploadedBy");
          final int _cursorIndexOfFilename = CursorUtil.getColumnIndexOrThrow(_cursor, "filename");
          final int _cursorIndexOfOriginalName = CursorUtil.getColumnIndexOrThrow(_cursor, "originalName");
          final int _cursorIndexOfCaption = CursorUtil.getColumnIndexOrThrow(_cursor, "caption");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUploadedByName = CursorUtil.getColumnIndexOrThrow(_cursor, "uploadedByName");
          final List<JobPhotoEntity> _result = new ArrayList<JobPhotoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final JobPhotoEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpJobId;
            _tmpJobId = _cursor.getInt(_cursorIndexOfJobId);
            final Integer _tmpUploadedBy;
            if (_cursor.isNull(_cursorIndexOfUploadedBy)) {
              _tmpUploadedBy = null;
            } else {
              _tmpUploadedBy = _cursor.getInt(_cursorIndexOfUploadedBy);
            }
            final String _tmpFilename;
            if (_cursor.isNull(_cursorIndexOfFilename)) {
              _tmpFilename = null;
            } else {
              _tmpFilename = _cursor.getString(_cursorIndexOfFilename);
            }
            final String _tmpOriginalName;
            if (_cursor.isNull(_cursorIndexOfOriginalName)) {
              _tmpOriginalName = null;
            } else {
              _tmpOriginalName = _cursor.getString(_cursorIndexOfOriginalName);
            }
            final String _tmpCaption;
            if (_cursor.isNull(_cursorIndexOfCaption)) {
              _tmpCaption = null;
            } else {
              _tmpCaption = _cursor.getString(_cursorIndexOfCaption);
            }
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final String _tmpCreatedAt;
            if (_cursor.isNull(_cursorIndexOfCreatedAt)) {
              _tmpCreatedAt = null;
            } else {
              _tmpCreatedAt = _cursor.getString(_cursorIndexOfCreatedAt);
            }
            final String _tmpUploadedByName;
            if (_cursor.isNull(_cursorIndexOfUploadedByName)) {
              _tmpUploadedByName = null;
            } else {
              _tmpUploadedByName = _cursor.getString(_cursorIndexOfUploadedByName);
            }
            _item = new JobPhotoEntity(_tmpId,_tmpJobId,_tmpUploadedBy,_tmpFilename,_tmpOriginalName,_tmpCaption,_tmpCategory,_tmpCreatedAt,_tmpUploadedByName);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
