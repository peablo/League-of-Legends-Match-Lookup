package com.qml.matchlookup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.qml.matchlookup.constants.Constants;
import com.qml.matchlookup.provider.MySuggestionProvider;
import com.qml.matchlookup.util.Util;


public class SettingsActivity extends ActionBarActivity
{

    Button mSkip, mNext;

    EditText mName;

    TextView mEnterName;

    Spinner mRegionsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_entername);

        init();


    }

    private void init()
    {

        mRegionsSpinner = (Spinner) findViewById(R.id.regionsSpinner);

        mSkip = (Button) findViewById(R.id.btnSkip);

        mNext = (Button) findViewById(R.id.btnNext);

        mName = (EditText) findViewById(R.id.summName);



        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.regions, R.layout.support_simple_spinner_dropdown_item);

        mRegionsSpinner.setAdapter( adapter );



        mSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                if( ! Util.validName(mName.getText().toString()) )
                {

                    Toast.makeText(SettingsActivity.this, getResources().getString(R.string.invalidNameWarning), Toast.LENGTH_SHORT).show();

                }else {

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", mName.getText().toString());
                    setResult(RESULT_OK, returnIntent);
                    finish();

                }
            }
        });

        if( getIntent().getBooleanExtra(Constants.CONFIG_BUTTON, false) )
        {

            // init prefs
            SharedPreferences prefs = getSharedPreferences( "com.qml.matchlookup", Context.MODE_PRIVATE );

            String summ = prefs.getString(Constants.SUMM_NAME, "");

            mName.setText(summ);

            Resources r = getResources();

            mSkip.setText( r.getString(R.string.back) );
            mNext.setText( r.getString(R.string.save) );


        }

    }






}
