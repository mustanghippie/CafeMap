package drgn.cafemap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by com.swift_studying. on 15/10/24.
 */
public class AsyncTaskHttpRequest extends AsyncTask<Uri.Builder, Void, Bitmap> {
    private ImageView imageView;
    private Marker marker;
    private View view;

    public AsyncTaskHttpRequest(ImageView imageView,Marker marker, View view){
        this.imageView = imageView;
        this.marker = marker;
        this.view = view;
    }

    @Override
    protected Bitmap doInBackground(Uri.Builder... builder){
        // 受け取ったbuilderでインターネット通信する
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        Bitmap bitmap = null;

        try{

            URL url = new URL(builder[0].toString());
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            inputStream = connection.getInputStream();

            bitmap = BitmapFactory.decodeStream(inputStream);



        }catch (MalformedURLException exception){

        }catch (IOException exception){

        }finally {
            if (connection != null){
                connection.disconnect();
            }
            try{
                if (inputStream != null){
                    inputStream.close();
                }
            }catch (IOException exception){
            }
        }
        System.out.println("Bitmap " +bitmap);
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result){
        System.out.println("EXECUTE-------------------onPost");
        this.imageView.setImageBitmap(result);

        // タイトル設定
        String title = marker.getTitle();
        TextView titleUi = (TextView) view.findViewById(R.id.title);
        // Spannable string allows us to edit the formatting of the text.
        SpannableString titleText = new SpannableString(title);
        titleText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, titleText.length(), 0);
        titleUi.setText(titleText);

        String snippet = marker.getSnippet();
        TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
        if (snippet != null) {
            SpannableString snippetText = new SpannableString(snippet);
            if (snippet.equals("Wi-fi: Good"))
                snippetText.setSpan(new ForegroundColorSpan(Color.YELLOW), 7, 11, 0);
            if (snippet.equals("Wi-fi: Bad"))
                snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 7, 10, 0);
            snippetUi.setText(snippetText);

        }

    }

}
