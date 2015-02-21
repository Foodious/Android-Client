package robert.com.foodious;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import robert.com.foodious.dummy.DummyContent;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class PlaceListFragment extends Fragment implements AbsListView.OnItemClickListener, LoaderManager.LoaderCallbacks<List<FoodPlace>> {

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    private List<FoodPlace> mGlobalList;

    /**
     * Flag keeps track of if our data loader has completed its task
     */
    private boolean loaded = false;

    private double mLatitude;
    private double mLongitude;
    List<FoodPlace> onePlace = new ArrayList<FoodPlace>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlaceListFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLatitude = getArguments().getDouble("latitude");
        mLongitude = getArguments().getDouble("longitude");
    }

    @Override
    public void onStart(){
        super.onStart();
        if(!loaded) {
            getLoaderManager().initLoader(0, null, this).forceLoad();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        mListView = (AbsListView) view.findViewById(android.R.id.list);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    public void updateList(List<FoodPlace> placeList){
        mAdapter = new ArrayAdapter<FoodPlace>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1,placeList);
        mAdapter.notify();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void randomizer(){
        if (!mGlobalList.isEmpty())
        {
            int random = (int) ((Math.random() * 100) % mGlobalList.size());
            if (!onePlace.isEmpty())
            {
                FoodPlace temp = onePlace.get(0);
                onePlace.clear();
                onePlace.add(mGlobalList.get(random));
                while (onePlace.get(0).equals(temp))
                {
                    random = (int) ((Math.random() * 100) % mGlobalList.size());
                    onePlace.clear();
                    onePlace.add(mGlobalList.get(random));
                }
            }
            else
            {
                onePlace.add(mGlobalList.get(random));
            }

            mAdapter = new ArrayAdapter<FoodPlace>(getActivity(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, onePlace);
            mListView.setAdapter(mAdapter);
        }
    }

    public View.OnClickListener randomButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PlaceListFragment frag = (PlaceListFragment) getFragmentManager().findFragmentById(R.id.FoodList);
            frag.randomizer();

        }
    };


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }



    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    @Override
    public Loader<List<FoodPlace>> onCreateLoader(int i, Bundle bundle) {
        return new FoodListDataLoader(getActivity(),null, mLongitude, mLatitude);
    }

    @Override
    public void onLoadFinished(Loader<List<FoodPlace>> listLoader, List<FoodPlace> foodPlaces) {
        if(foodPlaces!=null){
            loaded = true;
            mAdapter = new ArrayAdapter<FoodPlace>(getActivity(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, foodPlaces);
            mGlobalList = foodPlaces;
            randomizer();

            Button foodBtn = (Button) getActivity().findViewById(R.id.FoodButton);
            foodBtn.setOnClickListener(randomButtonListener);
        }


    }

    @Override
    public void onLoaderReset(Loader<List<FoodPlace>> listLoader) {

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
