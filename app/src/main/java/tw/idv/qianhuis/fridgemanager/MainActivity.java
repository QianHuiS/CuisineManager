package tw.idv.qianhuis.fridgemanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/*
* 隱藏StatusBar&ActivityBar:
*   res\values\styles.xml 加設定
*   AndroidManifest.xml 更改 <application android:theme>
* 鎖定橫向螢幕: AndroidManifest.xml 加 <activity android:screenOrientation>
*/
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
}
