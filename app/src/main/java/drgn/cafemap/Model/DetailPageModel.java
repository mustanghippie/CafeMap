package drgn.cafemap.Model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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

import drgn.cafemap.Controller.DetailPageActivity;
import drgn.cafemap.Controller.MapsActivity;
import drgn.cafemap.Object.MailAttachment;
import drgn.cafemap.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Nobu on 2017/06/11.
 */

public class DetailPageModel {
    // Base param
    private double lat;
    private double lon;
    private Context context;
    private UserCafeMapModel ucm;
    private FragmentActivity fragmentActivity;
    private View view;
    private Resources resources;
    // Common View
    private TextView nameTextView;
    private TextView addressTextView;
    private TextView telTextView;
    private Bitmap uploadImageBmp;

    // Edit page view
    private Button saveButton;
    private Button uploadImageButton;
    private Button previewButton;
    private ImageView uploadImage;
    private Spinner startHourSpinner;
    private Spinner startMinuteSpinner;
    private Spinner startAmPmSpinner;
    private Spinner endHourSpinner;
    private Spinner endMinuteSpinner;
    private Spinner endAmPmSpinner;
    private Spinner socketSpinner;
    private Spinner wifiSpinner;
    private Button deleteButton;
    // Detail page view
    private ImageView cafeScreenshot;
    private TextView timeTextView;
    private TextView socketTextView;
    private TextView wifiTextView;
    private ImageButton editButton;
    private ImageButton sendButton;


    public DetailPageModel(Context context, View view, FragmentActivity fragmentActivity, Resources resources, double lat, double lon) {
        this.context = context;
        this.view = view;
        this.fragmentActivity = fragmentActivity;
        this.resources = resources;
        this.lat = lat;
        this.lon = lon;
        this.ucm = new UserCafeMapModel(context);
    }

    private void prepareViewContents(int viewMode) {

        this.nameTextView = (TextView) view.findViewById(R.id.cafeName);
        this.addressTextView = (TextView) view.findViewById(R.id.cafeAddress);
        this.telTextView = (TextView) view.findViewById(R.id.cafeTel);
        switch (viewMode) {
            case 3:
            case 0:
                this.uploadImage = (ImageView) view.findViewById(R.id.uploadImage);
                this.saveButton = (Button) view.findViewById(R.id.saveButton);
                this.uploadImageButton = (Button) view.findViewById(R.id.uploadImageButton);
                this.previewButton = (Button) view.findViewById(R.id.previewButton);
                this.startHourSpinner = (Spinner) view.findViewById(R.id.startHour);
                this.startMinuteSpinner = (Spinner) view.findViewById(R.id.startMinute);
                this.startAmPmSpinner = (Spinner) view.findViewById(R.id.startAmPm);
                this.endHourSpinner = (Spinner) view.findViewById(R.id.endHour);
                this.endMinuteSpinner = (Spinner) view.findViewById(R.id.endMinute);
                this.endAmPmSpinner = (Spinner) view.findViewById(R.id.endAmPm);
                this.socketSpinner = (Spinner) view.findViewById(R.id.cafeSocket);
                this.wifiSpinner = (Spinner) view.findViewById(R.id.cafeWifi);
                this.deleteButton = (Button) view.findViewById(R.id.deleteButton);

                break;
            case 1:
                this.cafeScreenshot = (ImageView) view.findViewById(R.id.badge);
                this.timeTextView = (TextView) view.findViewById(R.id.cafeTime);
                this.socketTextView = (TextView) view.findViewById(R.id.cafeSocket);
                this.wifiTextView = (TextView) view.findViewById(R.id.cafeWifi);
                this.editButton = (ImageButton) view.findViewById(R.id.editButton);
                this.sendButton = (ImageButton) view.findViewById(R.id.sendButton);
                break;
            case 2:
                this.saveButton = (Button) view.findViewById(R.id.saveButton);
                this.cafeScreenshot = (ImageView) view.findViewById(R.id.badge);
                this.timeTextView = (TextView) view.findViewById(R.id.cafeTime);
                this.socketTextView = (TextView) view.findViewById(R.id.cafeSocket);
                this.wifiTextView = (TextView) view.findViewById(R.id.cafeWifi);
            default:

        }

    }

    /**
     * Displays edit page
     * View mode 2
     *
     * @param viewMode
     * @param cafeData
     */
    public void displayPreviewPage(int viewMode, Map<String, String> cafeData) {
        final Map<String, String> temporaryCafeData = cafeData;
        prepareViewContents(viewMode);

        // read preview image
        try {
            InputStream in = context.openFileInput("preview.png");
            uploadImageBmp = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cafeScreenshot.setImageBitmap(uploadImageBmp);

        if (temporaryCafeData.get("cafeName").equals("")) {
            temporaryCafeData.put("cafeName", "No name");
        }
        nameTextView.setText(temporaryCafeData.get("cafeName"));
        addressTextView.setText(temporaryCafeData.get("cafeAddress"));
        timeTextView.setText(temporaryCafeData.get("cafeTime"));
        telTextView.setText(temporaryCafeData.get("cafeTel"));
        socketTextView.setText(temporaryCafeData.get("cafeSocket"));
        wifiTextView.setText(temporaryCafeData.get("cafeWifi"));

        // Save
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CafeUserTblHelper cafeUserTblHelper = new CafeUserTblHelper(context);
                boolean successFlag = cafeUserTblHelper.executeInsert(lat, lon, temporaryCafeData.get("cafeName"), temporaryCafeData.get("cafeAddress"),
                        temporaryCafeData.get("cafeTime"), temporaryCafeData.get("cafeTel"),
                        temporaryCafeData.get("cafeSocket"), temporaryCafeData.get("cafeWifi"), new UserCafeMapModel(context).convertBitmapToByte(uploadImageBmp));

                // If cafe data already exist, execute UPDATE
                if (!successFlag) {
                    cafeUserTblHelper.executeUpdate(lat, lon, temporaryCafeData.get("cafeName"), temporaryCafeData.get("cafeAddress"),
                            temporaryCafeData.get("cafeTime"), temporaryCafeData.get("cafeTel"),
                            temporaryCafeData.get("cafeSocket"), temporaryCafeData.get("cafeWifi"), new UserCafeMapModel(context).convertBitmapToByte(uploadImageBmp));
                }

                Intent intent = new Intent(context, MapsActivity.class);

                intent.putExtra("defaultPosLat", lat);
                intent.putExtra("defaultPosLon", lon);
                fragmentActivity.startActivity(intent);
            }
        });
    }

    /**
     * Displays edit page
     * View mode 0 or 3
     * From making a new data
     *
     * @param viewMode
     */
    public void displayEditPage(int viewMode) { // view 0
        prepareViewContents(viewMode);

        // Invisible
        deleteButton.setVisibility(View.INVISIBLE);

        // preview
        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cafe information that is set by users
                // name
                String cafeName = nameTextView.getText().toString();
                // cafe address
                String cafeAddress = addressTextView.getText().toString();
                // cafe tel
                String cafeTel = telTextView.getText().toString();
                // cafe time
                String startHour = startHourSpinner.getSelectedItem().toString();
                String startMinute = startMinuteSpinner.getSelectedItem().toString();
                String startAmPm = startAmPmSpinner.getSelectedItem().toString();
                String endHour = endHourSpinner.getSelectedItem().toString();
                String endMinute = endMinuteSpinner.getSelectedItem().toString();
                String endAmPm = endAmPmSpinner.getSelectedItem().toString();
                String cafeTime = startHour + ":" + startMinute + startAmPm + " - " + endHour + ":" + endMinute + endAmPm;
                // socket
                String cafeSocket = socketSpinner.getSelectedItem().toString();
                // wifi
                String cafeWifi = wifiSpinner.getSelectedItem().toString();

                Intent intent = new Intent(context, DetailPageActivity.class);

                intent.putExtra("lat", lat);
                intent.putExtra("lon", lon);
                intent.putExtra("viewMode", 2);
                intent.putExtra("cafeName", cafeName);
                intent.putExtra("cafeAddress", cafeAddress);
                intent.putExtra("cafeTel", cafeTel);
                intent.putExtra("cafeTime", cafeTime);
                intent.putExtra("cafeWifi", cafeWifi);
                intent.putExtra("cafeSocket", cafeSocket);

                // temp preview image
                try {
                    OutputStream out = context.openFileOutput("preview.png", MODE_PRIVATE);
                    if (uploadImageBmp == null) {
                        // Set no image
                        try {
                            InputStream istream = resources.getAssets().open("noImage.png");
                            uploadImageBmp = BitmapFactory.decodeStream(istream);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    uploadImageBmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fragmentActivity.startActivity(intent);

            }
        });

    }

    // From existing data(View mode 3)
    public void displayEditPage(int viewMode, Map<String, String> cafeData, boolean ownerFlag) {
        prepareViewContents(viewMode);
        final Map<String, String> previousCafeData = cafeData;

        nameTextView.setText(previousCafeData.get("cafeName"));
        addressTextView.setText(previousCafeData.get("cafeAddress"));
        telTextView.setText(previousCafeData.get("cafeTel"));

        // set time parts
        String[] timeArray = previousCafeData.get("cafeTime").split(" - ");
        final String fromTime = timeArray[0].replaceAll("AM", "").replaceAll("PM", ""); // EX) 10:00
        String toTime = timeArray[1].replaceAll("AM", "").replaceAll("PM", ""); // EX) 19:00
        setSpinnerData(startHourSpinner, fromTime.split(":")[0]);
        setSpinnerData(startMinuteSpinner, fromTime.split(":")[1]);
        setSpinnerData(startAmPmSpinner, timeArray[0].substring(timeArray[0].length() - 2, timeArray[0].length()));
        setSpinnerData(endHourSpinner, toTime.split(":")[0]);
        setSpinnerData(endMinuteSpinner, toTime.split(":")[1]);
        setSpinnerData(endAmPmSpinner, timeArray[1].substring(timeArray[1].length() - 2, timeArray[1].length()));

        setSpinnerData(socketSpinner, previousCafeData.get("cafeSocket"));
        setSpinnerData(wifiSpinner, previousCafeData.get("cafeWifi"));

        // set previous image
        if (ownerFlag) {
            uploadImageBmp = getImageFromAssets(previousCafeData.get("cafeName"));
            uploadImage.setImageBitmap(uploadImageBmp);
        } else {
            uploadImageBmp = new UserCafeMapModel(context).convertByteToBitmap(new CafeUserTblHelper(context).executeSelect(lat, lon, "image"));
            uploadImage.setImageBitmap(uploadImageBmp);
        }

        // Delete only existing cafe info
        if (!ownerFlag) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(fragmentActivity)
                            .setTitle("Delete cafe information")
                            .setMessage("Are you sure you want to permanently delete this cafe info ?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // OK button pressed
                                    new CafeUserTblHelper(context).executeDelete(lat, lon); // DELETE

                                    Intent intent = new Intent(context, MapsActivity.class);

                                    intent.putExtra("defaultPosLat", lat);
                                    intent.putExtra("defaultPosLon", lon);
                                    fragmentActivity.startActivity(intent);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });
        } else {
            deleteButton.setVisibility(View.INVISIBLE);
        }

        // preview
        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cafe information that is set by users
                // name
                String cafeName = nameTextView.getText().toString();
                // cafe address
                String cafeAddress = addressTextView.getText().toString();
                // cafe tel
                String cafeTel = telTextView.getText().toString();
                // cafe time
                String startHour = startHourSpinner.getSelectedItem().toString();
                String startMinute = startMinuteSpinner.getSelectedItem().toString();
                String startAmPm = startAmPmSpinner.getSelectedItem().toString();
                String endHour = endHourSpinner.getSelectedItem().toString();
                String endMinute = endMinuteSpinner.getSelectedItem().toString();
                String endAmPm = endAmPmSpinner.getSelectedItem().toString();
                String cafeTime = startHour + ":" + startMinute + startAmPm + " - " + endHour + ":" + endMinute + endAmPm;
                // socket
                String cafeSocket = socketSpinner.getSelectedItem().toString();
                // wifi
                String cafeWifi = wifiSpinner.getSelectedItem().toString();

                Intent intent = new Intent(context, DetailPageActivity.class);

                intent.putExtra("lat", lat);
                intent.putExtra("lon", lon);
                intent.putExtra("viewMode", 2);
                intent.putExtra("cafeName", cafeName);
                intent.putExtra("cafeAddress", cafeAddress);
                intent.putExtra("cafeTel", cafeTel);
                intent.putExtra("cafeTime", cafeTime);
                intent.putExtra("cafeWifi", cafeWifi);
                intent.putExtra("cafeSocket", cafeSocket);

                // temp preview image
                try {
                    OutputStream out = context.openFileOutput("preview.png", MODE_PRIVATE);
                    uploadImageBmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                fragmentActivity.startActivity(intent);

            }
        });
    }

    /**
     * Displays detail page
     * View mode 1
     *
     * @param viewMode
     */
    public void displayDetailPage(int viewMode, final boolean ownerFlag) {
        prepareViewContents(viewMode);

        // image in cafe_user_tbl exits or not
        // Get data owner's or user's
        final Map<String, Object> cafeDetail;
        Bitmap image = null;

        if (ownerFlag) {
            cafeDetail = new CafeMasterTblHelper(context).executeSelect(lat, lon);
            image = getImageFromAssets(cafeDetail.get("cafeName").toString());
        } else {
            cafeDetail = new CafeUserTblHelper(context).executeSelect(lat, lon);
            byte[] imageByte = (byte[]) cafeDetail.get("cafeImage");
            image = ucm.convertByteToBitmap(imageByte);
        }

        nameTextView.setText(cafeDetail.get("cafeName").toString());
        addressTextView.setText(cafeDetail.get("cafeAddress").toString());
        telTextView.setText(cafeDetail.get("cafeTel").toString());
        timeTextView.setText(cafeDetail.get("cafeTime").toString());
        socketTextView.setText(cafeDetail.get("cafeSocket").toString());
        wifiTextView.setText(cafeDetail.get("cafeWifi").toString());
        cafeScreenshot.setImageBitmap(image);

        //if (type.equals("master")) editButton.setVisibility(View.INVISIBLE);

        // go to edit page
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailPageActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lon", lon);
                intent.putExtra("viewMode", 3);
                intent.putExtra("ownerFlag", ownerFlag);
                intent.putExtra("cafeName", cafeDetail.get("cafeName").toString());
                intent.putExtra("cafeAddress", cafeDetail.get("cafeAddress").toString());
                intent.putExtra("cafeTel", cafeDetail.get("cafeTel").toString());
                intent.putExtra("cafeTime", cafeDetail.get("cafeTime").toString());
                intent.putExtra("cafeSocket", cafeDetail.get("cafeSocket").toString());
                intent.putExtra("cafeWifi", cafeDetail.get("cafeWifi").toString());

                fragmentActivity.startActivity(intent);

            }
        });

        // send cafe information
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> mailAccount = getMailAccount();
                final String email = mailAccount.get("mail");
                final String password = mailAccount.get("password");
                final String body = "Cafe map mail";
                final String subject;

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

                    // make attachment json and image
                    makeAttachmentOfJson();

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
//                    System.out.println("finish sending email");
                }

            }
        });

    }

    /**
     * Makes attachment files.
     * Json file is written cafe information and cafe image
     * this method saves both files to android's local.
     */
    private void makeAttachmentOfJson() {
        Map<String, Object> cafe;

        cafe = new CafeUserTblHelper(context).executeSelect(lat, lon);

        MailAttachment mailAttachment = new MailAttachment(lat, lon, cafe.get("cafeName").toString(), cafe.get("cafeAddress").toString(),
                cafe.get("cafeTime").toString(), cafe.get("cafeTel").toString(), cafe.get("cafeSocket").toString(), cafe.get("cafeWifi").toString());

        String attachment = new Gson().toJson(mailAttachment);

        try {
            // make a json file
            FileOutputStream jsonFile = context.openFileOutput("mail_attachment.json", MODE_PRIVATE);
            jsonFile.write(attachment.getBytes());
            jsonFile.close();

            // save image to local
            byte[] bytes = new CafeUserTblHelper(context).executeSelect(lat, lon, "image");
            FileOutputStream imageFile = context.openFileOutput("image.png", MODE_PRIVATE);
            Bitmap image = ucm.convertByteToBitmap(bytes);
            image.compress(Bitmap.CompressFormat.PNG, 100, imageFile);
            imageFile.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setSpinnerData(Spinner timeSpinner, String time) {
        Adapter adp = (ArrayAdapter<String>) timeSpinner.getAdapter();
        int index = 0;
        for (int i = 0; i < adp.getCount(); i++) {
            if (adp.getItem(i).equals(time)) {
                index = i;
                break;
            }
        }
        timeSpinner.setSelection(index);
    }

    /**
     * Sets a upload image by the preview button.
     * This method suppose to be called
     * from onActivityResult@DetailPageFragment.
     *
     * @param uploadImageBmp
     */
    public void setUploadImageBmp(Bitmap uploadImageBmp) {
        this.uploadImage.setImageBitmap(uploadImageBmp);
        this.uploadImageBmp = uploadImageBmp;
    }

    /**
     * Obtains PNG image from assets folder.
     *
     * @param name
     * @return Bitmap image
     */
    private Bitmap getImageFromAssets(String name) {
        Bitmap bitmap = null;

        try {
            InputStream inputStream = resources.getAssets().open(name.replaceAll(" ", "_").toLowerCase() + ".png");
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
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
