package drgn.cafemap.Controller.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import drgn.cafemap.Model.AsyncTaskSendMail;
import drgn.cafemap.Model.CafeModel;
import drgn.cafemap.R;
import drgn.cafemap.databinding.FragmentDetailPageBinding;
import drgn.cafemap.Util.AnimationUtil;
import drgn.cafemap.Util.DetailPageHandlers;

public class DetailPageFragment extends Fragment implements DetailPageHandlers {

    private FragmentDetailPageBinding binding;
    private Context context;
    private DetailPageFragmentListener listener = null;

    private double lat, lon;
    private boolean ownerFlag;
    private CafeModel cafeModel;

    public interface DetailPageFragmentListener {
        void goToEditPageEvent();
    }

    // Constructor
    public DetailPageFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return DetailPageFragment
     */
    public static DetailPageFragment newInstance(double lat, double lon, boolean ownerFlag) {
        DetailPageFragment fragment = new DetailPageFragment();
        Bundle args = new Bundle();
        args.putDouble("lat", lat);
        args.putDouble("lon", lon);
        args.putBoolean("ownerFlag", ownerFlag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof DetailPageFragmentListener) {
            listener = (DetailPageFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DetailPageFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.lat = getArguments().getDouble("lat");
            this.lon = getArguments().getDouble("lon");
            this.ownerFlag = getArguments().getBoolean("ownerFlag");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_detail_page, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // initialize
        this.context = getContext();
        this.cafeModel = new CafeModel(context, getResources());
        // prepare binding
        this.binding = FragmentDetailPageBinding.bind(view);
        // setting handlers
        this.binding.setHandlers(this);
        // setting cafe detail
        this.binding.setCafe(cafeModel.getCafeInstance(ownerFlag, lat, lon));
        // setting image
        Bitmap image = cafeModel.getCafeImage(ownerFlag, lat, lon);
        this.binding.badge.setImageBitmap(image);

        // setting bookmark icon
        boolean bookmarkFlag = cafeModel.getBookmarkFlag(ownerFlag, lat, lon);
        if (bookmarkFlag) {
            binding.addBookmarkButton.setImageResource(R.drawable.icon_bookmark_added);
        } else {
            binding.addBookmarkButton.setImageResource(R.drawable.icon_bookmark_add);
        }

        // hide eMail button in owner data case
        if (ownerFlag) binding.mailButton.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onClickPhoneNumber(View view) {

        new AlertDialog.Builder(getActivity())
                .setTitle("Phone")
//                .setMessage(binding.cafeTel.getText().toString())
                .setPositiveButton("Call", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // OK button pressed
                        Uri uri = Uri.parse("tel:" + binding.cafeTel.getText().toString());
                        Intent i = new Intent(Intent.ACTION_DIAL, uri);
                        startActivity(i);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();


    }

    @Override
    public void onClickMailButton(View view) {
        // click animation
        new AnimationUtil().clickFadeInFadeOutAnimation(binding.mailButton);

        new AlertDialog.Builder(getActivity())
                .setTitle("Could we send this to us?")
                .setMessage("This information will be submitted to our database. " +
                        "Please press \"AGREE\" if you agree.")
                .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // OK button pressed
                        // check send_flag preventing to send email in a row
                        boolean sendFlag = cafeModel.checkMailSendFlag(lat, lon);
                        if (!sendFlag) {
                            AsyncTaskSendMail asyncTaskSendMail = new AsyncTaskSendMail(context, lat, lon);
                            asyncTaskSendMail.execute("");
                        }
                        Toast.makeText(context, "Thank you for your support!!", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onClickAddBookmarkButton(View view) {
        boolean bookmarkFlag;
        // click animation
        new AnimationUtil().clickFadeInFadeOutAnimation(binding.addBookmarkButton);

        bookmarkFlag = cafeModel.getBookmarkFlag(ownerFlag, lat, lon);
        // update bookmark flag on database table
        cafeModel.updateBookmarkFlag(ownerFlag, lat, lon, bookmarkFlag);

        // change bookmark icon and set flag
        if (!bookmarkFlag) {
            binding.addBookmarkButton.setImageResource(R.drawable.icon_bookmark_added);
        } else {
            binding.addBookmarkButton.setImageResource(R.drawable.icon_bookmark_add);
        }
    }

    @Override
    public void onClickEditButton(View view) {
        // click animation
        new AnimationUtil().clickFadeInFadeOutAnimation(binding.editButton);

        if (listener != null) {
            listener.goToEditPageEvent();
        }
    }
}
