package com.example.getdetails;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.RoomDatabase;

import java.util.List;

public class UserRepository {
    private UserDao userDao;
    private LiveData<List<User>> allUsers;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public UserRepository(Application application) {
        UserRoomDatabase db = UserRoomDatabase.getDatabase(application);
        userDao = db.userDao();
        allUsers = userDao.getUsers();
    }

    void updateUsers(List<User> user){
//        UserRoomDatabase.databaseWriteExecutor.execute(() -> {
//            userDao.update(user);
//        });
        new UpdateUserAsyncTask(userDao).execute(user);

    }
    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(User user) {
//        UserRoomDatabase.databaseWriteExecutor.execute(() -> {
//            userDao.insert(user);
//    });
        new InsertUserAsyncTask(userDao).execute(user);
    }

    private static class InsertUserAsyncTask extends AsyncTask<User, Void, Void>{
        private UserDao userDao;

        private InsertUserAsyncTask(UserDao userDao){
            this.userDao = userDao;
        }
        @Override
        protected Void doInBackground(User... users) {
            userDao.insert(users[0]);
            return null;
        }
    }
//    private static class DeleteUserAsyncTask extends AsyncTask<User, Void, Void>{
//        private UserDao userDao;
//
//        private DeleteUserAsyncTask(UserDao userDao){
//            this.userDao = userDao;
//        }
//        @Override
//        protected Void doInBackground(User... users) {
//            userDao.delete(users[0]);
//            return null;
//        }
//    }
    private static class UpdateUserAsyncTask extends AsyncTask<List<User>, Void, Void>{
        private UserDao userDao;

        private UpdateUserAsyncTask(UserDao userDao){
            this.userDao = userDao;
        }
        @Override
        protected Void doInBackground(List<User>... users) {
            userDao.updateUsers(users[0]);
            return null;
        }

}
}

