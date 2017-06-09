package drgn.cafemap;

import android.provider.ContactsContract;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by musta on 2017/06/08.
 */

public class FutureSnapshot implements Future<DataSnapshot>, ValueEventListener {
    private Query query;
    private boolean canceled;
    private DataSnapshot snapshot;

    public FutureSnapshot(DatabaseReference mDatabase) {
        mDatabase.addListenerForSingleValueEvent(this);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (isDone()) {
            return false;
        }

        query.removeEventListener(this);
        canceled = true;
        return true;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public boolean isDone() {
        return snapshot != null;
    }

    @Override
    public DataSnapshot get() throws InterruptedException, ExecutionException {
        while (!isDone() && !canceled) {
            Thread.sleep(100);
        }
        return snapshot;
    }

    @Override
    public DataSnapshot get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        Thread.sleep(unit.toMillis(timeout));
        return snapshot;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        this.snapshot = dataSnapshot;
    }

    @Override
    public void onCancelled(DatabaseError firebaseError) {
        canceled = true;
    }
}
