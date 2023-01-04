package com.example.abasad.mylocationsmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class AddPlaceActivity extends AppCompatActivity {

    private PlaceList list;
    EditText LatFld, LongFld, CommFld;
    TextView textView4;
    RadioGroup typeRdGroup;
    Boolean haveBeen;
    LatLng currLoc;
    XStream xstream;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        // ## Getting current location from main activity
       currLoc = new LatLng(getIntent().getExtras().getDouble("lat"),getIntent().getExtras().getDouble("long")) ;

        // ## setting up the stream object
        xstream = new XStream();
        xstream.alias("Place", Place.class);
        xstream.alias("Places", PlaceList.class);
        xstream.addImplicitCollection(PlaceList.class, "list");

        // ## populating places from xml file
        onRead();

        // ## initilaizing components
        LatFld = findViewById(R.id.LatFld);
        LongFld = findViewById(R.id.LongFld);
        CommFld = findViewById(R.id.CommFld);
        haveBeen = false;

        // ## Radio group for place type - setting callback
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.TypeRdGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.WantToBtn) {
                    LongFld.setEnabled(true);
                    LatFld.setEnabled(true);
                    ClearFields();
                    haveBeen = false;
                } else
                {
                    LongFld.setEnabled(false);
                    LatFld.setEnabled(false);
                    LongFld.setText( Double.toString(currLoc.longitude));
                    LatFld.setText( Double.toString(currLoc.latitude));
                    haveBeen = true;
                }
            }
        });


        // ## Recycler View Set Up
        recyclerView = (RecyclerView) findViewById(R.id.placesToGoRecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new PlacesAdapter(getApplicationContext(),list.list);
        recyclerView.setAdapter(mAdapter);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.divider));

        recyclerView.addItemDecoration(itemDecorator);

    }

    // Buttons callbacks
    public void CloseClicked(View view) {
        finish();
    }

    public void AddPlaceClicked(View view) {

        boolean added = true;
        if(haveBeen) {
            list.add(new Place(CommFld.getText().toString(),currLoc.latitude , currLoc.longitude, true));
        }
        else{

            if(isNotEmpty(LatFld) && isNotEmpty(LongFld))
            {
                double  lat = Double.parseDouble( LatFld.getText().toString());
                double  longt = Double.parseDouble( LongFld.getText().toString());

                if(lat < -90 || lat > 90)
                {  Toast.makeText(this, "Latitude value should be between +90 and -90", Toast.LENGTH_SHORT).show(); added= false;}
                else if(longt < -180 || longt > 180)
                {  Toast.makeText(this, "Longitude value should be between +180 and -180", Toast.LENGTH_SHORT).show(); added= false;}
                else
                list.add(new Place(CommFld.getText().toString(),lat , longt));
            }
            else
            {
                added = false;
                Toast.makeText(this, "Latitude and Longtitude are required fields!", Toast.LENGTH_SHORT).show();
            }


        }

        if(added)
        {
          ClearFields();
            recyclerView.requestLayout();
            onWrite();
        }

    }

    public void ClearClicked(View view) {

        list.list.clear();
        onWrite();
        recyclerView.requestLayout();
    }

    // Utilities methods
    private void ClearFields() {
        CommFld.setText("");
        LatFld.setText("");
        LongFld.setText("");
    }

    private boolean isNotEmpty(EditText et)
    {
        String str = et.getText().toString();
        if(str.matches("")) return false; else return  true;
    }

    // Write places list to xml file
    private void onWrite() {

        try
        {
            // parsing the xml structure to string
            String xml = xstream.toXML(list);

            // to save to file in data/data/packagename
            FileOutputStream ofile = openFileOutput(getString(R.string.File_Name),MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(ofile);
            osw.write(xml.toCharArray());
            osw.flush();
            osw.close();
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    // To read from XML File
    private void onRead() {
        // read the file from the data/data/packagename
        if (fileExists(this, getString(R.string.File_Name))) {
            try {

                // reading from data/data/packagename
                FileInputStream fin = openFileInput(getString(R.string.File_Name));
                InputStreamReader isr = new InputStreamReader(fin);
                char[] inputBuffer = new char[100];
                String str = "";
                int charRead;
                while ((charRead = isr.read(inputBuffer)) > 0) {
                    String readString = String.copyValueOf(inputBuffer, 0, charRead);
                    str += readString;
                }
                isr.close();

                XStream xstream = new XStream();

                xstream.alias("Place", Place.class);
                xstream.alias("Places", PlaceList.class);
                xstream.addImplicitCollection(PlaceList.class, "list");

                list = (PlaceList) xstream.fromXML(str);

                if (list.list == null)
                    list.list = new ArrayList<>();

            }
            catch(IOException ioe)
            {
                ioe.printStackTrace();
            }
        }

        else
            list = new PlaceList();


    }

    private boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }


}
