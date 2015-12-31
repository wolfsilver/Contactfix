package modify.contact.com.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    public static String isWithCountryCode = "isWithCountryCode";

    Config config = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        config = new Config();

        Switch aSwitch = (Switch) findViewById(R.id.switch1);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                config.setIsContryPrefix(isChecked);
                Log.i("param", "isChecked: " + isChecked);
            }
        });
    }

    /**
     * @param view
     */
    public void showContactList(View view) {
        Intent intent = new Intent(this, ContactListActivity.class);

        Log.i("param", "isCountryPrefix: " + config.isCountryPrefix());
        intent.putExtra(isWithCountryCode, config.isCountryPrefix());
        startActivity(intent);
    }

    /**
     * 条件
     */
    class Config {
        /**
         * 国家码
         */
        boolean isCountryPrefix = false;

        /**
         * 区号
         */
        boolean isAreaPrefix = false;

        public boolean isCountryPrefix() {
            return isCountryPrefix;
        }

        public void setIsContryPrefix(boolean isCountryPrefix) {
            this.isCountryPrefix = isCountryPrefix;
        }

        public boolean isAreaPrefix() {
            return isAreaPrefix;
        }

        public void setIsAreaPrefix(boolean isAreaPrefix) {
            this.isAreaPrefix = isAreaPrefix;
        }


    }
}
