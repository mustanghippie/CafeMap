package drgn.cafemap.Controller.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import drgn.cafemap.Model.CafeModel;
import drgn.cafemap.R;
import drgn.cafemap.databinding.FragmentPreviewPageBinding;
import drgn.cafemap.Object.Cafe;
import drgn.cafemap.Util.PreviewPageHandlers;

public class PreviewPageFragment extends Fragment implements PreviewPageHandlers {

    private Context context;
    private CafeModel cafeModel;
    private FragmentPreviewPageBinding binding;

    private Cafe cafe;
    private double lat;
    private double lon;
    private Bitmap previewImage;
    private PreviewPageFragmentListener listener = null;

    public interface PreviewPageFragmentListener {
        void finishedPreviewPageEvent();
    }

    public PreviewPageFragment() {
        // Required empty public constructor
    }


    public static PreviewPageFragment newInstance(Cafe cafe, double lat, double lon) {
        PreviewPageFragment fragment = new PreviewPageFragment();
        Bundle args = new Bundle();
        args.putSerializable("cafe", cafe);
        args.putDouble("lat", lat);
        args.putDouble("lon", lon);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof PreviewPageFragmentListener) {
            this.listener = (PreviewPageFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PreviewPageFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.cafe = (Cafe) getArguments().getSerializable("cafe");
            this.lat = getArguments().getDouble("lat");
            this.lon = getArguments().getDouble("lon");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_preview_page, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // initialize
        this.context = getContext();
        this.cafeModel = new CafeModel(context, getResources());
        // prepare binding
        this.binding = FragmentPreviewPageBinding.bind(view);
        // setting cafe data
        binding.setCafe(cafe);
        binding.setHandlers(this);
        binding.badge.setImageBitmap(cafeModel.readPreviewImageFromLocal());
        // read preview image
        this.previewImage = cafeModel.readPreviewImageFromLocal();

    }

    @Override
    public void onClickSaveButton(View view) {
        boolean successfully = true;

        successfully = cafeModel.insertCafeData(cafe, lat, lon, previewImage);
        // If cafe data already exist, execute UPDATE
        if (!successfully) {
            successfully = cafeModel.uploadCafeData(cafe, lat, lon, previewImage);
        }

        if (listener != null) {
            if (successfully) {
                Toast.makeText(context, "Saved successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Sorry, failed to save your cafe map.", Toast.LENGTH_SHORT).show();
            }

            listener.finishedPreviewPageEvent();
        }
    }
}
