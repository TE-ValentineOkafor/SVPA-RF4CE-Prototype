package com.sony.svpa.rf4ceprototype.uei;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.sony.svpa.rf4ceprototype.R;
import com.sony.svpa.rf4ceprototype.activities.MainActivity;
import com.uei.control.ISetup;
import com.uei.control.ResultCode;
import com.uei.encryption.helpers.CallerHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by wwang on 4/25/2017.
 */

public class SetEdidDataActivity extends Activity {
    /** The Constant DIALOG_KEY. */
    private static final int DIALOG_KEY = 0;

    /** The ISetup. */
    private ISetup _setup = null;

    /** The authenticator. */
    private CallerHelper _authenticator = new CallerHelper();

    /** The encryption key. */
    private byte[] _encryptionKey = null;

    private AlertDialog _popDialog = null;

    private boolean _isVisible = false;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //set to landscape mode for large screen
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.setediddata);
        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            this._encryptionKey =  extras.getByteArray("Key");
        }

        this._setup = QuicksetSampleApplication.getSetup();
        Button btn = (Button) this.findViewById(R.id.addEdidData);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // reset status and progress bar

                    EditText fileText = (EditText)findViewById(R.id.editEdidFile);
                    File inputFile = new File(fileText.getText().toString());
                    if (inputFile.length() >= 0) {
                        byte[] dataBuffer = new byte[0];
                        int byteRead = 0;
                        if(inputFile.length() > 0) {
                            InputStream is = new FileInputStream(inputFile);
                            dataBuffer = new byte[is.available()];
                            byteRead = is.read(dataBuffer);
                            is.close();
                        }

                        if(_setup != null && byteRead > 0){

                            int status = _setup.setEdidData(dataBuffer);

                            Log.d(MainActivity.LOGTAG, "Set EDID data status: " + ResultCode.getString(status));
                            finish();
                       }
                    } else {
                        Log.d(MainActivity.LOGTAG, "No valid input file");
                    }
                }
                catch (Exception ex) {
                    Log.d(MainActivity.LOGTAG, "setEdidData exception: " + ex.toString());
                }
            }
        });

        btn = (Button) this.findViewById(R.id.browseData);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetEdidDataActivity.this, FilePickerActivity.class);
                // Start the file picker activity
                startActivityForResult(intent, DIALOG_KEY);
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        this._isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        this._isVisible = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            switch(requestCode) {
                case DIALOG_KEY:
                    if(data.hasExtra(FilePickerActivity.EXTRA_FILE_PATH)) {
                        // Get the file path
                        File f = new File(data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH));
                        EditText fileText = (EditText)findViewById(R.id.editEdidFile);
                        String fileName = f.getPath();
                        // Set the file path text view
                        fileText.setText(fileName);
                        Log.d(MainActivity.LOGTAG, "edid data file: " + fileName);
                    }
            }
        }
    }


}
