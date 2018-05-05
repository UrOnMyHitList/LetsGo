package t.systematic.letsgo;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.ProgressBar;

public class SplashActivity extends AppCompatActivity{
    private int perc = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        new CountDownTimer(5000,1000){
            @Override
            public void onTick(long millisUntilFinished){
                ProgressBar pb = findViewById(R.id.splash_loading);
                pb.setProgress(perc);
                perc = perc + 33;
            }

            @Override
            public void onFinish(){
                Intent loginIntent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(loginIntent);

            }
        }.start();
    }
}
