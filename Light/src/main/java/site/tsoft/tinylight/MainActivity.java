package site.tsoft.tinylight;

import android.app.Activity;
import android.os.Bundle;

import site.tsoft.tinylight.TinyLightService;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TinyLightService.startActionToggle(this.getApplicationContext());

        finish();
    }


}
