package foodious.com.foodious;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by danielmargosian on 3/9/15.
 */
public class PreferencesDialogFragment extends DialogFragment {

    private TextView radiusProgress;
    private boolean changed = false;
    private int radius;
    /* The activity that creates an instance of this dialog fragment must
    * implement this interface in order to receive event callbacks.
            * Each method passes the DialogFragment in case the host needs to query it. */
    public interface PreferencesDialogListener {
        void onDialogPositiveClick(PreferencesDialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    PreferencesDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (PreferencesDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_preferences, null);
        builder.setView(view);
        builder.setMessage("Slide to change search radius");
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onDialogPositiveClick(PreferencesDialogFragment.this);
            }
        });
        SeekBar radiusBar = (SeekBar) view.findViewById(R.id.radiusBar);
        radiusProgress = (TextView) view.findViewById(R.id.radiusProgress);
        updateProgress((int) MainActivity.getRadius());
        radiusBar.setProgress((int) MainActivity.getRadius());
        radiusBar.setOnSeekBarChangeListener(radiusBarChangeListener);
        radiusBar.setMax(200);
        return builder.create();
    }

    public SeekBar.OnSeekBarChangeListener radiusBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress != MainActivity.getRadius()) {
                if (progress < 1)
                    progress = 1;
                updateProgress(progress);
                radius = progress;
                changed = true;
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    public void updateProgress(int i) {
        radiusProgress.setText("Search Radius: " + i/10.0  + " mi\n");
    }

    public int getRadius() {return radius;}

    public boolean isChanged() {return changed;}

    public void setChanged(boolean b) {changed = b;}
}
