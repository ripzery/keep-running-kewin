package com.example.ripzery.projectx01.app;

import android.app.Activity;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ripzery.projectx01.R;
import com.google.android.gms.games.multiplayer.Participant;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentGameMultiplayerStatus.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentGameMultiplayerStatus#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentGameMultiplayerStatus extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int ALL_TEXT[] = {R.id.tvInfo, R.id.tvPlayer1, R.id.tvPlayer2, R.id.tvPlayer3, R.id.tvPlayer4};
    private static final int ALL_INFO_TEXT[] = {R.id.tvPlayer1Info1, R.id.tvPlayer1Info2, R.id.tvPlayer1Info3, R.id.tvPlayer1Info4
            , R.id.tvPlayer2Info1, R.id.tvPlayer2Info2, R.id.tvPlayer2Info3, R.id.tvPlayer2Info4
            , R.id.tvPlayer3Info1, R.id.tvPlayer3Info2, R.id.tvPlayer3Info3, R.id.tvPlayer3Info4
            , R.id.tvPlayer4Info1, R.id.tvPlayer4Info2, R.id.tvPlayer4Info3, R.id.tvPlayer4Info4};

    private HashMap<String, ArrayList<TextView>> allInfoTexts = new HashMap<>();
    private ArrayList<TextView> allPlayersText = new ArrayList<>();
    private OnFragmentInteractionListener mListener;
    private View rootView;
    private ArrayList<TextView> infoPlayer = new ArrayList<>();
    private int[] otherPlayerDurationSecs = new int[2];
    private ArrayList<Participant> finishedPlayer = new ArrayList<>();

    public FragmentGameMultiplayerStatus() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentGameMultiplayerStatus.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentGameMultiplayerStatus newInstance(String param1, String param2) {
        FragmentGameMultiplayerStatus fragment = new FragmentGameMultiplayerStatus();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_game_multiplayer_status, container, false);

        Typeface light = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");

        for (int id : ALL_TEXT) {
            TextView tv = (TextView) rootView.findViewById(id);
            tv.setTypeface(light);
            allPlayersText.add(tv);
        }

        int count = 1;
        for (int id : ALL_INFO_TEXT) {
            infoPlayer.add((TextView) rootView.findViewById(id));
            infoPlayer.get((count - 1) % 4).setTypeface(light);
            if (count % 4 == 0 && count != 1) {
                allInfoTexts.put("p" + (count / 4), infoPlayer);
                infoPlayer = new ArrayList<>();
            }
            count++;
        }

        int otherPlayerIndex = 2; // use for set text at the correct place

        for (int i = 0; i < Singleton.mParticipants.size(); i++) {

//            Log.d("MultiplayerStatus : ",Singleton.mParticipants.get(i).getDisplayName() );
            if (!Singleton.mParticipants.get(i).getParticipantId().equals(Singleton.myId)) {
                String name = Singleton.mParticipants.get(i).getDisplayName();
                if (name.contains(" ")) {
                    getTextView(otherPlayerIndex).setText(name.split(" ")[0]);
                } else {
                    getTextView(otherPlayerIndex).setText(name);
                }

                otherPlayerIndex++;
            }
        }

        return rootView;
    }

    public void addFinishedPlayer(Participant p) {
        finishedPlayer.add(p);
    }

    public ArrayList<Participant> getFinishedPlayer() {
        return finishedPlayer;
    }

    public void setOtherPlayerDurationSecs(int[] durationSecs) {
        otherPlayerDurationSecs = durationSecs;
    }

    public TextView getTextView(String key, int index) {
        return allInfoTexts.get(key).get(index);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onUpdate();
        }
    }

    // index 0 is except
    public TextView getTextView(int index) {
        return allPlayersText.get(index);
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
        public void onUpdate();
    }

}
