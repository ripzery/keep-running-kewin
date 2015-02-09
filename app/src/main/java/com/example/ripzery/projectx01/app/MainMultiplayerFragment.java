package com.example.ripzery.projectx01.app;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ripzery.projectx01.R;
import com.google.android.gms.games.Games;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainMultiplayerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainMultiplayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainMultiplayerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int DES_ID[] = {R.id.tvQuickMatchDes, R.id.tvInviteDes, R.id.tvSignOutDes};
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private Button signOutButton;
    private Button inviteButton;
    private Button quickMatchButton;
    private LinearLayout playerStatusBar;
    private TextView playerName;
    private CircleImageView playerIcon;
    private Typeface light;

    public MainMultiplayerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainMultiplayerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainMultiplayerFragment newInstance(String param1, String param2) {
        MainMultiplayerFragment fragment = new MainMultiplayerFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_multiplayer, container, false);
        String myName = Games.Players.getCurrentPlayer(Singleton.mGoogleApiClient).getDisplayName();
        String myImageUrl = Games.Players.getCurrentPlayer(Singleton.mGoogleApiClient).getHiResImageUrl();
        light = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");

        signOutButton = (Button) rootView.findViewById(R.id.signOutButton);
        inviteButton = (Button) rootView.findViewById(R.id.inviteButton);
        playerStatusBar = (LinearLayout) rootView.findViewById(R.id.layoutPlayerBar);
        playerName = (TextView) rootView.findViewById(R.id.playerName);
        playerName.setText(myName);
        playerName.setTypeface(light);
        playerIcon = (CircleImageView) rootView.findViewById(R.id.profile_image);
//        playerIcon.setImageURI(myImageUrl);
        Picasso.with(getActivity())
                .load(myImageUrl)
                .noFade()
                .into(playerIcon);

        for (int id : DES_ID) {
            ((TextView) rootView.findViewById(id)).setTypeface(light);
        }

        quickMatchButton = (Button) rootView.findViewById(R.id.quickMatchButton);
        quickMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed(R.id.quickMatchButton);
            }
        });
        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed(R.id.inviteButton);
            }
        });
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed(R.id.signOutButton);
            }
        });
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(int id) {
        if (mListener != null) {
            mListener.onMainMultiplayerButtonClicked(id);
        }
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
        public void onMainMultiplayerButtonClicked(int id);
    }

}
