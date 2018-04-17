# Android数据库系统：Android数据库系统概述

作者：[wusp](https://github.com/wusp)

校对：[郭孝星](https://github.com/guoxiaoxing)

**关于项目**

> [BeesAndroid](https://github.com/BeesAndroid/BeesAndroid)项目旨在通过提供一系列的工具与方法，降低阅读Android系统源码的门槛，让更多的Android工程师理解
Android系统，掌握Android系统。

**文章目录**

+ SQLite 层次结构
+ SQLite Java Framework

## SQLite 层次结构

首先，我们要对 Android 上所使用的数据库技术——SQLite，在代码和功能架构上有个基本的了解：

![sqlite](https://github.com/wusp/BeesAndroid/blob/master/art/principle/app/sqlite/sqlite_architecture.png)

基于以下两点，我们仅会重点介绍 sqlite Java Framework。

+ SQLiteOpenHelper 只是简单的API及流程封装，方便开发者使用，并不是数据库的核心。
+ sqlite library 本质上已经超出了Android的范围，其中的功能特性应该参阅SQLite的文档：[SQLite](https://www.sqlite.org/index.html)。

## Java Framework

Framework 中，四部分最重要的内容撑起了SQLiteDatabase主要功能框架。

+ SQLiteConnectionPool & SQLiteConnection
+ SQLiteSession
+ SQLiteProgram
+ CursorWindow

#### SQLiteConnectionPool & SQLiteConnection
SQLiteConnection 是 SQLiteDatabase 最重要的组成部分，包含为上层API所有的读/写操作过程。当我们通过API对 SQLiteDatabase 进行CRUD操作时，就是基于 SQLiteConnection 进行操作的。当一个 SQLiteConnection 进行时，它会被从 SQLiteConnectionPool 中取出并交给 SQLiteSession；当一个 SQLiteConnection 使用完成之后，便会交回给 SQLiteConnectionPool。SQLiteConnectionPool 便是 SQLiteConnection 在 IDLE 状态的管理机构。
**SQLiteConnectionPool 可以工作在所有请求共享同一个Connection模式或者维持连接池两种模式下。SQLiteConnectionPool 将 SQLiteDatabase 和 SQLiteConnection 相互隔离开。**

看看 SQLiteConnectionPool 是如何将 SQLiteDatabase 和 SQLiteConnection 串联起来的:
###### SQLiteDatabase 创建
SQLiteDatabase.java

```

    public static SQLiteDatabase openOrCreateDatabase(String path, CursorFactory factory,
            DatabaseErrorHandler errorHandler) {
        return openDatabase(path, factory, CREATE_IF_NECESSARY, errorHandler);
    }


    public static SQLiteDatabase openDatabase(String path, CursorFactory factory, int flags,
            DatabaseErrorHandler errorHandler) {
        SQLiteDatabase db = new SQLiteDatabase(path, flags, factory, errorHandler);
        db.open();
        return db;
    }


    private void open() {
        try {
            try {
                //根据在构造器中传入的配置打开，第一次尝试
                openInner();
            } catch (SQLiteDatabaseCorruptException ex) {
                //失败之后先进行错误通知，再重试一次。
                onCorruption();
                openInner();
            }
        } catch (SQLiteException ex) {
            Log.e(TAG, "Failed to open database '" + getLabel() + "'.", ex);
            close();
            throw ex;
        }
    }

    private void openInner() {
        synchronized (mLock) {
            assert mConnectionPoolLocked == null;
            mConnectionPoolLocked = SQLiteConnectionPool.open(mConfigurationLocked);
            mCloseGuardLocked.open("close");
        }

        synchronized (sActiveDatabases) {
            sActiveDatabases.put(this, null);
        }
    }

```
**所谓 SQLiteDatabase 创建，不过也就是创建一个 SQLiteDatabase 实例，同时执行对应 SQLiteConnectionPool 的打开操作。**

SQLiteConnectionPool.java

```
    public static SQLiteConnectionPool open(SQLiteDatabaseConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("configuration must not be null.");
        }

        // 创建连接池实例
        SQLiteConnectionPool pool = new SQLiteConnectionPool(configuration);
        pool.open(); // might throw
        return pool;
    }

    private void open() {
        // Open the primary connection. （也就是所有请求共享一个Connection模式下的那个primary connection）
        // This might throw if the database is corrupt.
        mAvailablePrimaryConnection = openConnectionLocked(mConfiguration,
                true /*primaryConnection*/); // might throw

        // Mark the pool as being open for business.
        mIsOpen = true;
        mCloseGuard.open("close");
    }

    private SQLiteConnection openConnectionLocked(SQLiteDatabaseConfiguration configuration,
            boolean primaryConnection) {
        final int connectionId = mNextConnectionId++;
        return SQLiteConnection.open(this, configuration,
                connectionId, primaryConnection); // might throw
    }
```
**重要的除了创建一个 ConnectionPool 实例，我们还要注意到打开 SQLiteConnectionPool 时还会执行primary connection的打开，该connection将会在共享模式下被使用到。**

SQLiteConnection.java

```
    static SQLiteConnection open(SQLiteConnectionPool pool,
            SQLiteDatabaseConfiguration configuration,
            int connectionId, boolean primaryConnection) {
        // primaryConnection = true
        // 先创建对应的 SQLiteConnection 实例，并执行其对应的打开操作。
        SQLiteConnection connection = new SQLiteConnection(pool, configuration,
                connectionId, primaryConnection);
        try {
            connection.open();
            return connection;
        } catch (SQLiteException ex) {
            connection.dispose(false);
            throw ex;
        }
    }

    private void open() {
        // native 执行，将打开数据库连接的请求转交给 sqlite native library.
        // 后期 SQLiteDatabase 的操作都需要通过 mConnectionPtr 来执行转发.
        mConnectionPtr = nativeOpen(mConfiguration.path, mConfiguration.openFlags,
                mConfiguration.label,
                SQLiteDebug.DEBUG_SQL_STATEMENTS, SQLiteDebug.DEBUG_SQL_TIME);

        setPageSize();
        setForeignKeyModeFromConfiguration();
        setWalModeFromConfiguration();
        setJournalSizeLimit();
        setAutoCheckpointInterval();
        setLocaleFromConfiguration();

        // Register custom functions.
        final int functionCount = mConfiguration.customFunctions.size();
        for (int i = 0; i < functionCount; i++) {
            SQLiteCustomFunction function = mConfiguration.customFunctions.get(i);
            nativeRegisterCustomFunction(mConnectionPtr, function);
        }
    }
```
**除了创建 SQLiteConnection 的实例，可以看到真正执行数据库连接打开的是 sqlite native library。在后面我们会看到，Android Framework中的sqlite，其实只是对底层 sqlite library 的一层 wrapper，目的在于方便Application和Framework层的使用。**

至此，我们已经看到SQLiteDatabase、back的SQLiteConnectionPool以及Pool中primary connection是如何被创建起来的。在正常流程下，SQLiteDatabase已经能够进行最基本的对数据库文件的读/写操作了。

#### SQLiteSession
或许你会问 SQLiteSession 是干啥的？为什么需要它？

**SQLiteSession 用来管理每一个数据库事务或者数据库连接操作的生命周期，并为 SQLiteDatabase 提供操作上的 Thread-Safe。**

**SQLiteSession 帮助将数据库操作过程从 SQLiteDatabase 中抽离出来，使得 SQLiteDatabase 只是一个Connection Container + API Provider 的角色，类似Handler。**

###### 操作 Thread-Safe 的实现方式
SQLiteDatabase.java

```
    private final ThreadLocal<SQLiteSession> mThreadSession = new ThreadLocal<SQLiteSession>() {
        @Override
        protected SQLiteSession initialValue() {
            return createSession();
        }
    };

    SQLiteSession getThreadSession() {
        return mThreadSession.get(); // initialValue() throws if database closed
    }

    SQLiteSession createSession() {
        final SQLiteConnectionPool pool;
        synchronized (mLock) {
            throwIfNotOpenLocked();
            pool = mConnectionPoolLocked;
        }
        return new SQLiteSession(pool);
    }
```
从 SQLiteDatabase 的实现中，可以看到使用了TLS技术来保证 Thread-Safe。SQLiteDatabase 的调用者不用在意自己的线程具体是谁，TLS已经确保不同的调用线程使用的是自己线程内对应的 SQLiteSession。

同时我们也注意到，即便在不同的 SQLiteSession 中，使用的都是 SQLiteDatabase 中同一个 SQLiteConnectionPool，这意味着即便 SQLiteDatabase 并行着多个 SQLiteSession ，大家使用的都是同一个连接池中的数据库连接，并不会为每一个Session都新创建 SQLiteConnectionPool。

在下一小节 SQLiteProgram 中，我们将会看到 SQLiteDatabase 是如何将数据库操作请求转交给 SQLiteSession，以及 SQLiteSession 是如何从 SQLiteConnectionPool 中取出连接并以此完成具体的数据库操作的。

#### SQLiteProgram

SQLiteDatabase CRUD操作的执行离不开一个非常关键的类 —— SQLiteProgram。正是通过SQLiteProgram，SQLiteDatabase完成了操作API到具体SQL语句的转换。 SQLiteProgram有两个实现子类，**SQLiteQuery** 和 **SQLiteStatement**。

![SQLiteProgra](https://github.com/wusp/BeesAndroid/blob/master/art/principle/app/sqlite/SQLiteProgram.png)


+ **SQLiteQuery:** 负责Query的执行转发处理。
+ **SQLiteStatement:** 负责Insert/Update/Delete的执行转发。

**通过 getSession() 方法， SQLiteDatabase 的CRUD操作被转发到 SQLiteSession 中。在 SQLiteSession 中，SQLiteConnection 将CRUD操作转到native去执行。**

###### Insert

SQLiteDatabase.java

```
    public long insertWithOnConflict(String table, String nullColumnHack,
            ContentValues initialValues, int conflictAlgorithm) {
        acquireReference();
        try {
            ......

            SQLiteStatement statement = new SQLiteStatement(this, sql.toString(), bindArgs);
            try {
                return statement.executeInsert();
            } finally {
                statement.close();
            }
        } finally {
            releaseReference();
        }
    }
```
SQLiteStatement.java

```
    public long executeInsert() {
        acquireReference();
        try {
            return getSession().executeForLastInsertedRowId(
                    getSql(), getBindArgs(), getConnectionFlags(), null);
        } catch (SQLiteDatabaseCorruptException ex) {
            onCorruption();
            throw ex;
        } finally {
            releaseReference();
        }
    }
```
insert 操作在SQLiteDatabase中，经过SQLiteStatement 的转接，最终依旧交给了 SQLiteSession 中去执行。
SQLiteSession.java

```
    public long executeForLastInsertedRowId(String sql, Object[] bindArgs, int connectionFlags,
            CancellationSignal cancellationSignal) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }

        if (executeSpecial(sql, bindArgs, connectionFlags, cancellationSignal)) {
            return 0;
        }

        acquireConnection(sql, connectionFlags, cancellationSignal); // might throw
        try {
            return mConnection.executeForLastInsertedRowId(sql, bindArgs,
                    cancellationSignal); // might throw
        } finally {
            releaseConnection(); // might throw
        }
    }
```
SQLiteSession 中先检查Transaction相关的语句去执行，然后获取一个数据库连接，由数据库连接去操作insert。
SQLiteConnection.java

```
    public long executeForLastInsertedRowId(String sql, Object[] bindArgs,
            CancellationSignal cancellationSignal) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }

        final int cookie = mRecentOperations.beginOperation("executeForLastInsertedRowId",
                sql, bindArgs);
        try {
            final PreparedStatement statement = acquirePreparedStatement(sql);
            try {
                throwIfStatementForbidden(statement);
                bindArguments(statement, bindArgs);
                applyBlockGuardPolicy(statement);
                attachCancellationSignal(cancellationSignal);
                try {
                    return nativeExecuteForLastInsertedRowId(
                            mConnectionPtr, statement.mStatementPtr);
                } finally {
                    detachCancellationSignal(cancellationSignal);
                }
            } finally {
                releasePreparedStatement(statement);
            }
        } catch (RuntimeException ex) {
            mRecentOperations.failOperation(cookie, ex);
            throw ex;
        } finally {
            mRecentOperations.endOperation(cookie);
        }
    }
```
关键点在于，**SQLiteConnection将insert的请求转发到native中去执行。**

###### update & delete
**由于update和delete的操作跟insert一样，也是基于 SQLiteStatement 来完成，因此他们在调用流程上都是极其相似的。对于update和delete操作来说更是如此，SQLiteDatabase 将他们两个的请求都合并到了同一个入口。**
这里只带大家看一下update和delete是如何合并请求到同一个入口。对于调用过程的理解参见insert操作就好。

SQLiteDatabase.java

```
    public int updateWithOnConflict(String table, ContentValues values,
            String whereClause, String[] whereArgs, int conflictAlgorithm) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Empty values");
        }

        acquireReference();
        try {
            ......

            SQLiteStatement statement = new SQLiteStatement(this, sql.toString(), bindArgs);
            try {
                return statement.executeUpdateDelete();
            } finally {
                statement.close();
            }
        } finally {
            releaseReference();
        }
    }

    public int delete(String table, String whereClause, String[] whereArgs) {
        acquireReference();
        try {
            SQLiteStatement statement =  new SQLiteStatement(this, "DELETE FROM " + table +
                    (!TextUtils.isEmpty(whereClause) ? " WHERE " + whereClause : ""), whereArgs);
            try {
                return statement.executeUpdateDelete();
            } finally {
                statement.close();
            }
        } finally {
            releaseReference();
        }
    }
```

SQLiteStatement.java

```
    public int executeUpdateDelete() {
        acquireReference();
        try {
            return getSession().executeForChangedRowCount(
                    getSql(), getBindArgs(), getConnectionFlags(), null);
        } catch (SQLiteDatabaseCorruptException ex) {
            onCorruption();
            throw ex;
        } finally {
            releaseReference();
        }
    }
```

**无论是insert还是update，又或者delete操作，我们可以发现他们的调用流程都是这样的：**

+ **起点都是 SQLiteDatabase**
+ SQLiteDatabase 在接收到请求后，将请求包装成对应的 SQLiteProgram 实现类 SQLiteStatement。
+ 调用 SQLiteStatement 实例对应的操作方法。
+ 在 SQLiteStatement 中，请求被转到调用线程中的 SQLiteSession 中去执行。
+ 在 SQLiteSession 中先尝试从对应的 SQLiteConnectionPool 中获取 SQLiteConnection，并调用 SQLiteConnection 对应的方法来执行请求。
+ 在 SQLiteConnection 中，请求被转发到 native 中。
+ **终点都是native中的 sqlite library**

###### query

从上面可以看到一个请求是如何从 SQLiteDatabase 被转发到 sqlite library 中的。总的来说，query 的过程也是差不多的，**最大也是最需要引人注意的不同在于 SQLiteDatabase 创建了 SQLiteProgram 之后并不是立即执行进行数据库查询，而是延迟执行**。

SQLiteDatabase.java

```
public Cursor rawQueryWithFactory(
            CursorFactory cursorFactory, String sql, String[] selectionArgs,
            String editTable, CancellationSignal cancellationSignal) {
        acquireReference();
        try {
        // SQLiteCursorDriver 用来帮助生成 SQLiteCursor
            SQLiteCursorDriver driver = new SQLiteDirectCursorDriver(this, sql, editTable,
                    cancellationSignal);
            return driver.query(cursorFactory != null ? cursorFactory : mCursorFactory,
                    selectionArgs);
        } finally {
            releaseReference();
        }
    }
```

SQLiteDirectCursorDriver.java

```
    public Cursor query(CursorFactory factory, String[] selectionArgs) {
    // 创建了 SQLiteProgram 用于查询的子类的实例 SQLiteQuery
        final SQLiteQuery query = new SQLiteQuery(mDatabase, mSql, mCancellationSignal);
        final Cursor cursor;
        try {
            query.bindAllArgsAsStrings(selectionArgs);

            if (factory == null) {
                cursor = new SQLiteCursor(this, mEditTable, query);
            } else {
                cursor = factory.newCursor(mDatabase, this, mEditTable, query);
            }
        } catch (RuntimeException ex) {
            query.close();
            throw ex;
        }

        mQuery = query;
        return cursor;
    }
```

SQLiteCursor.java

```
    public SQLiteCursor(SQLiteCursorDriver driver, String editTable, SQLiteQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("query object cannot be null");
        }
        if (StrictMode.vmSqliteObjectLeaksEnabled()) {
            mStackTrace = new DatabaseObjectNotClosedException().fillInStackTrace();
        } else {
            mStackTrace = null;
        }
        mDriver = driver;
        mEditTable = editTable;
        mColumnNameMap = null;
        mQuery = query;

        mColumns = query.getColumnNames();
    }
```

**我们可以清楚的看到，当成功创建一个 SQLiteCursor 实例之后，便将该实例直接返回给调用方了，虽然SQLiteCursor 保存了 SQLiteQuery 实例，但是却并没有立即执行它。SQLiteQuery 没有得到执行，自然就不会有相关联的连接数据库进行查询。**

那么数据库查询是什么时候执行的呢？

源码告诉我们，数据库查询的真正发生时间是在 SQLiteCursor 的 fillWindow 方法执行时：

```
    private void fillWindow(int requiredPos) {
        clearOrCreateWindow(getDatabase().getPath());

        try {
            if (mCount == NO_COUNT) {
                int startPos = DatabaseUtils.cursorPickFillWindowStartPosition(requiredPos, 0);
                // 调用 SQLiteQuery 的 fillWindow ，该方法里面会开启真正的数据库查询。
                mCount = mQuery.fillWindow(mWindow, startPos, requiredPos, true);
                mCursorWindowCapacity = mWindow.getNumRows();
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "received count(*) from native_fill_window: " + mCount);
                }
            } else {
                int startPos = DatabaseUtils.cursorPickFillWindowStartPosition(requiredPos,
                        mCursorWindowCapacity);
                mQuery.fillWindow(mWindow, startPos, requiredPos, false);
            }
        } catch (RuntimeException ex) {
            closeWindow();
            throw ex;
        }
    }
```

那么 fillWindow 方法什么时候会被调用呢？

```
    public boolean onMove(int oldPosition, int newPosition) {
        // Cursor 的游标移动之后如果不能正常读取到内容，则需要重新查询。
        if (mWindow == null || newPosition < mWindow.getStartPosition() ||
                newPosition >= (mWindow.getStartPosition() + mWindow.getNumRows())) {
            fillWindow(newPosition);
        }

        return true;
    }

    @Override
    public int getCount() {
        if (mCount == NO_COUNT) {
            fillWindow(0);
        }
        return mCount;
    }
```

+ getCount 一般是在客户端拿到Cursor之后判断数量时调用，又比如在APP开发教程中提到Cursor的 moveToFirst 方法，都会调用到 getCount 方法。
+ onMove 方法设计上是用来在服务器端映射客户端对 Cursor 游标的移动的，但是目前并没有怎么使用到。

在 SQLiteQuery 的 fillWindow 方法中，可以看到：

SQLiteQuery.java

```
    int fillWindow(CursorWindow window, int startPos, int requiredPos, boolean countAllRows) {
        acquireReference();
        try {
            window.acquireReference();
            try {
                int numRows = getSession().executeForCursorWindow(getSql(), getBindArgs(),
                        window, startPos, requiredPos, countAllRows, getConnectionFlags(),
                        mCancellationSignal);
                return numRows;
            } catch (SQLiteDatabaseCorruptException ex) {
                onCorruption();
                throw ex;
            } catch (SQLiteException ex) {
                Log.e(TAG, "exception: " + ex.getMessage() + "; query: " + getSql());
                throw ex;
            } finally {
                window.releaseReference();
            }
        } finally {
            releaseReference();
        }
    }
```
最终查询还是走上了跟 insert/update/delete 一样的老路，将请求交给 SQLiteSession 去处理，并最终交由 sqlite library 来完成。

#### CursorWindow

讲Cursor就离不开CursorWindow。目前Android的Application开发中，基本上所有的Cursor都是 back by CursorWindow 的。
这就意味着，所有通过 ContentProvider 拿到Cursor的客户端与服务器端之间，都是通过共享一片内存缓冲区来完成数据读取的，而不是通过客户端IPC操作服务器端中Cursor的实例这样的方式来完成数据读取。

**CursorWindow 的工作原理**

+ 一个包含了多条Cursor row的内存缓冲区。
+ 在Server端创建时处于可读写的状态，如果一直在该进程内使用也会一直是可读写；但是如果经过Parcel的IPC传送之后，在远端进程中只能是只读的状态。
+ 典型的生产者-消费者结构，在生产者Server端分配内存空间，填充数据然后发回给Client端，而Client APP仅仅读取其中的内容。
+ 无论读还是写都是native方法，Java只是包了一层Java API。

下面就带大家以 getLong & getLong 为例， 看看CursorWindow 是如何支持Cursor的。

SQLiteCursor 处于 AbstractWindowedCursor 的继承链上，同时没有复写方法。
AbstractWindowedCursor.java

```
    public long getLong(int columnIndex) {
        checkPosition();
        return mWindow.getLong(mPos, columnIndex);
    }
```
进入到 CursorWindow 当中
CursorWindow.java

```
    public long getLong(int row, int column) {
        acquireReference();
        try {
            return nativeGetLong(mWindowPtr, row - mStartPos, column);
        } finally {
            releaseReference();
        }
    }

    public boolean putLong(long value, int row, int column) {
        acquireReference();
        try {
            return nativePutLong(mWindowPtr, value, row - mStartPos, column);
        } finally {
            releaseReference();
        }
    }

```

本质上，CursorWindow 都是对一块内存区域的读写。

跳到native中看看：

```
status_t CursorWindow::putLong(uint32_t row, uint32_t column, int64_t value) {
    if (mReadOnly) {
        return INVALID_OPERATION;
    }

    FieldSlot* fieldSlot = getFieldSlot(row, column);
    if (!fieldSlot) {
        return BAD_VALUE;
    }

    fieldSlot->type = FIELD_TYPE_INTEGER;
    fieldSlot->data.l = value;
    return OK;
}
```

+ 空间不足时则不允许写入，返回false。
+ Cursor概念上的行、列，都会被转化成一个用于保存数据的内存块 **FieldSlot**。在客户端读取 Cursor 上的内容就是在读取这片内存区域上，先前由服务器端写入的数据。
+ **mReadOnly为true时则不允许写入**。mReadOnly在调用create()方法构造CursorWindow时会被赋值为false；在通过createFromParcel反序列化回CursorWindow时会被赋值为true。只有一方具有写入权限的设定也保证了在使用这块内存区域时不需要使用同步的机制。
