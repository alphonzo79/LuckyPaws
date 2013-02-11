package com.luckypawsdaycare.activities;

/*
 * Copyright (c) 2013 Lucky Paws, Inc
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainScreen extends Activity {
    Button webCamButton;
    Button reservationsButton;
    Button settingsButton;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        findButtonsAddListeners();
    }

    private void findButtonsAddListeners() {
        webCamButton = (Button)findViewById(R.id.web_cam_button);
        reservationsButton = (Button)findViewById(R.id.reservations_button);
        settingsButton = (Button)findViewById(R.id.settings_button);

        webCamButton.setOnClickListener(launchWebCam);
        reservationsButton.setOnClickListener(launchReservations);
        settingsButton.setOnClickListener(launchSettings);
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
