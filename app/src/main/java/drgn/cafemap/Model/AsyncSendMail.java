package drgn.cafemap.Model;


import android.os.AsyncTask;

/**
 * Created by musta on 2017/07/05.
 */

public class AsyncSendMail extends AsyncTask<String, String, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // doInBackground前処理
    }

    @Override
    protected String doInBackground(String... value) {

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
//        Log.d("onPostExecute:", "Execute");
    }
}
