package com.qml.matchlookup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class TeamFragment extends Fragment
{
    int team = 0;

    public static TeamFragment newInstance(int team)
    {
        TeamFragment f = new TeamFragment();
        f.team = team;
        return f;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        View base = rootView.findViewById(R.id.layout_base);

        /*
        base.setBackgroundColor( getResources().getColor(android.R.color.holo_blue_light) );

        if(team == 200)
            base.setBackgroundColor( getResources().getColor(android.R.color.holo_red_light) ); */

        return rootView;
    }



}
