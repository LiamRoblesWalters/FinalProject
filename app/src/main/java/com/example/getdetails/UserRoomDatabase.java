package com.example.getdetails;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.gson.Gson;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class}, version = 1, exportSchema = false)
@TypeConverters({Converter.class})
public abstract class UserRoomDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    private static volatile UserRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static UserRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (UserRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            UserRoomDatabase.class, "user_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // If you want to keep data through app restarts,
            // comment out the following block
//            databaseWriteExecutor.execute(() -> {
//                // Populate the database in the background.
//                // If you want to start with more words, just add them.
//                UserDao dao = INSTANCE.userDao();
//                dao.deleteAll();
//
//                User user = new User();
//                dao.insert(user);
//                user = new User("World");
//                dao.insert(user);
//            });

//            private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
//                private UserDao userDao;
//
//                private PopulateDbAsyncTask(UserRoomDatabase db){
//                    userDao = db.userDao();
//                }
//
//                @Override
//                protected void doInBackground(Void... voids){
//
//                }
        }

    };
}

class Converter {
    @TypeConverter
    public static Address getAddress(String address) {
//        return street == null? null : new Address(street);
        return new Gson().fromJson(address, Address.class);
    }

    @TypeConverter
    public static String addressToString(Address address) {
//        return address == null? null : address.street + address.geo;
        return new Gson().toJson(address);

    }
}
