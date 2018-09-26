package uoft.ubicomp.pageturnercontrol;
/**
 * Created by Mingming on 8/30/2018.
 */
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button bt_connect;
    EditText editText_IP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt_connect = (Button)findViewById(R.id.button_connect);
        bt_connect.setOnClickListener(this);
        editText_IP = (EditText)findViewById(R.id.editText_IP);

    }

    @Override
    public void onClick(View view) {
        String IP = editText_IP.getText().toString();
        Bundle b = new Bundle();
        b.putString("IP", IP);
        Intent i = new Intent(getApplicationContext(), PageTurnerControlNew.class);
//        Intent i = new Intent(getApplicationContext(), PageTurnerControl.class);
        i.putExtras(b);
        startActivity(i);
    }
}
