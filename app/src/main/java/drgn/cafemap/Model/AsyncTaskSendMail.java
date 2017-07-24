package drgn.cafemap.Model;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import drgn.cafemap.Object.MailAttachment;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Nobu on 2017/07/05.
 */

public class AsyncTaskSendMail extends AsyncTask<String, String, String> {

    private Context context;
    private double lat, lon;

    public AsyncTaskSendMail(Context context, double lat, double lon) {
        this.context = context;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // doInBackground前処理
    }

    @Override
    protected String doInBackground(String... value) {

        Map<String, String> mailAccount = getMailAccount();
        final String email = mailAccount.get("mail");
        final String password = mailAccount.get("password");
        final String body = "Cafe map mail";
        final String subject;

        // make attachment json and image
        makeAttachmentOfJson();

        try {
            // get UID
            FileInputStream in = context.openFileInput("UID");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            // uid
            subject = reader.readLine();
            reader.close();

            // update email and password
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putString("email", email).commit();
            sp.edit().putString("password", password).commit();

            // send email
            final Properties property = new Properties();
            property.put("mail.smtp.host", "smtp.gmail.com");
            property.put("mail.host", "smtp.gmail.com");
            property.put("mail.smtp.port", "465");
            property.put("mail.smtp.socketFactory.port", "465");
            property.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

            // session
            final Session session = Session.getInstance(property, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(email, password);
                }
            });

            MimeMessage mimeMsg = new MimeMessage(session);

            mimeMsg.setSubject(subject, "utf-8");
            mimeMsg.setFrom(new InternetAddress(email));
            mimeMsg.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(email));

            // 添付ファイルをする場合はこれを使う
            final MimeBodyPart filePart = new MimeBodyPart();
            File file = new File(context.getFilesDir() + "/image.png");
            FileDataSource fds = new FileDataSource(file);
            DataHandler data = new DataHandler(fds);
            filePart.setDataHandler(data);
            filePart.setFileName(MimeUtility.encodeWord("mail_image.png"));

            // 添付2
            final MimeBodyPart filePart2 = new MimeBodyPart();
            File file2 = new File(context.getFilesDir() + "/mail_attachment.json");
            FileDataSource fds2 = new FileDataSource(file2);
            DataHandler data2 = new DataHandler(fds2);
            filePart2.setDataHandler(data2);
            filePart2.setFileName(MimeUtility.encodeWord("mail_attachment.json"));

            final MimeBodyPart txtPart = new MimeBodyPart();
            txtPart.setText(body, "utf-8");

            final Multipart mp = new MimeMultipart();
            mp.addBodyPart(txtPart);
            mp.addBodyPart(filePart); // 添付ファイル1
            mp.addBodyPart(filePart2); // 添付ファイル2
            mimeMsg.setContent(mp);

            final Transport transport = session.getTransport("smtp");

            transport.connect(email, password);
            transport.sendMessage(mimeMsg, mimeMsg.getAllRecipients());
            transport.close();

        } catch (MessagingException e) {
            System.out.println("exception = " + e);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // update send_flag
            new CafeUserTblHelper(context).executeUpdate(lat, lon, 1);
        }


        return null;
    }

    @Override
    protected void onPostExecute(String result) {
//        Log.d("onPostExecute:", "Execute");
    }

    /**
     * Makes attachment files.
     * Json file is written cafe information and cafe image
     * this method saves both files to android's local.
     */
    private void makeAttachmentOfJson() {
        Map<String, String> cafe;

        cafe = new CafeUserTblHelper(context).executeSelect(lat, lon);

        MailAttachment mailAttachment = new MailAttachment(String.valueOf(lat), String.valueOf(lon), cafe.get("cafeName").toString(), cafe.get("cafeAddress").toString(),
                cafe.get("cafeTime").toString(), cafe.get("cafeTel").toString(), cafe.get("cafeSocket").toString(), cafe.get("cafeWifi").toString());

        String attachment = new Gson().toJson(mailAttachment);

        try {
            // make a json file
            FileOutputStream jsonFile = context.openFileOutput("mail_attachment.json", MODE_PRIVATE);
            jsonFile.write(attachment.getBytes());
            jsonFile.close();

            // save image to local
            FileOutputStream imageFile = context.openFileOutput("image.png", MODE_PRIVATE);
            Bitmap image = new UserCafeMapModel(context).getCafeImage(lat, lon);

            image.compress(Bitmap.CompressFormat.PNG, 100, imageFile);
            imageFile.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtains mail account information
     *
     * @return
     */
    private Map<String, String> getMailAccount() {
        Map<String, String> mailAccount = new HashMap<>();

        InputStream is = null;
        BufferedReader br = null;

        try {
            try {
                is = context.getAssets().open("mail_account");
                br = new BufferedReader(new InputStreamReader(is));

                mailAccount.put("mail", br.readLine());
                mailAccount.put("password", br.readLine());
            } finally {
                if (is != null) is.close();
                if (br != null) br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mailAccount;

    }
}
