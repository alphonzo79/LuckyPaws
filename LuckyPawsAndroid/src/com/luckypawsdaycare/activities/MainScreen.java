package com.luckypawsdaycare.activities;

/*
 * Copyright (c) 2013 Lucky Paws, Inc
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import com.luckypawsdaycare.R;

public class MainScreen extends Activity {
    Button webCamButton;
    Button reservationsButton;
    Button settingsButton;

    FrameLayout bodyRoot;

    private final String TAG = "MainScreen";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        bodyRoot = (FrameLayout)findViewById(R.id.main_root);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        findButtonsAddListeners();
        setButtonSizes();
    }

    private void findButtonsAddListeners() {
        webCamButton = (Button)findViewById(R.id.web_cam_button);
        reservationsButton = (Button)findViewById(R.id.reservations_button);
        settingsButton = (Button)findViewById(R.id.settings_button);

        webCamButton.setOnClickListener(launchWebCam);
        reservationsButton.setOnClickListener(launchReservations);
        settingsButton.setOnClickListener(launchSettings);
    }

    //Set the buttons to be square based upon slightly less than 1/3 of the longest dimension of the screen
    //The buttons should be 1.5X wider than they are tall
    private void setButtonSizes() {
        Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int windowHeight = display.getHeight();
        int windowWidth = display.getWidth();
        int height = 0;
        int width = 0;
        if(windowHeight > windowWidth) {
            height = (int) ((windowHeight / 3) * 0.72);
            width = (int) (height * 1.5);
        } else {
            width = (int) ((windowWidth / 3) * 0.95);
            height = (int) (width / 1.5);
        }
        Log.i(TAG, String.format("Button Size is %d by %d", width, height));
        Button[] buttons = new Button[]{webCamButton, reservationsButton, settingsButton};
        for(Button button : buttons) {
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams)button.getLayoutParams();
            params.height = height;
            params.width = width;
            button.setLayoutParams(params);
        }
    }

    protected final Button.OnClickListener launchWebCam = new Button.OnClickListener(){
        public void onClick(View view){
            Intent intent = new Intent(MainScreen.this, WebCamViewScreen.class);
            startActivity(intent);
        }
    };

    protected final Button.OnClickListener launchReservations = new Button.OnClickListener(){
        public void onClick(View view){
            Intent intent = new Intent(MainScreen.this, ReservationsScreen.class);
            startActivity(intent);
        }
    };

    protected final Button.OnClickListener launchSettings = new Button.OnClickListener(){
        public void onClick(View view){
            Intent intent = new Intent(MainScreen.this, SettingsScreen.class);
            startActivity(intent);
        }
    };
}
