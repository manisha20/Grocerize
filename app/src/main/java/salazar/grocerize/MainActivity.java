package salazar.grocerize;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;


public class MainActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String username = "testuser";
        String password = "testpass";

        new GetAsync().execute(username, password);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            new GetAsync().execute("", "");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void openApp(View view) {
        String url="";
        switch(view.getId()) {
            case R.id.iBAmazon:
                url = "http://www.amazon.in/";
                break;
            case R.id.iBGrofers:
                url = "https://grofers.com/";
                break;
            case R.id.iBBb:
                url="http://www.bigbasket.com/";
                break;
        }

        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setPackage("com.android.chrome");
        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            i.setPackage(null);
            startActivity(i);
        }

    }



    class GetAsync extends AsyncTask<String,String, JSONArray> {
        JSONParser jsonParser = new JSONParser();
        private ProgressDialog pDialog;
        private static final String Temp_URL = "https://proactivegrocerize.mybluemix.net/get_rack";
        private static final String Bottle_URL = "https://proactivegrocerize.mybluemix.net/get_item";
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Attempting connection...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            JSONArray json1=null;
            JSONArray json2=null;

            try {

                Log.d("request", "starting");

                json1 = jsonParser.makeHttpRequest(
                        Temp_URL, "GET", args[0]);
                json2 = jsonParser.makeHttpRequest(
                        Bottle_URL, "GET", args[0]);

                if (json1 != null && json2 != null) {
                    Log.d("JSON result", json1.toString());
                    json1.put(json2);
                    return json1;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return json1;
        }

        protected void onPostExecute(JSONArray json) {
            String temp,humid;

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (json != null) {
                Toast.makeText(MainActivity.this, "Data downloaded",
                        Toast.LENGTH_SHORT).show();
                Log.d("Async", json.toString());

                try {

                    temp = json.get(1).toString();
                    humid=json.get(2).toString();
                    Log.d("inPostExecute",temp+" "+humid);
                    JSONArray jarr=json.getJSONArray(3);
                    int arr[]={Integer.parseInt(jarr.get(1).toString()),
                            Integer.parseInt(jarr.get(2).toString()),
                                    Integer.parseInt(jarr.get(3).toString()),
                                            Integer.parseInt(jarr.get(4).toString())};
                    setImagesForBottle(arr);

                    setTextViewTempHumid(temp,humid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    private void setTextViewTempHumid(String temp, String humid) {
        TextView tvTemp=(TextView)findViewById(R.id.tvTemperature);
        TextView tvHumid=(TextView)findViewById(R.id.tvHumidity);

        if(temp.equals(null)||temp == "null" || temp.isEmpty()){temp="32";}
        if(humid.equals(null)||humid == "null" || humid.isEmpty()){humid="37";}

        tvTemp.setText(temp+"C");
        tvHumid.setText(humid+ "%");

    }

    void setImagesForBottle(int bottleLevel[]){

        for(int i=0;i<4;i++){
            if(bottleLevel[i]>=0&&bottleLevel[i]<=25){
                setImageHelper(0,i);

            }
            else if(bottleLevel[i]>25&&bottleLevel[i]<75){
                setImageHelper(50,i);
            }
            else setImageHelper(100,i);
        }
    }

    private void setImageHelper(int bottleLevel, int bottleNumber) {
        ImageView ivRed= (ImageView)findViewById(R.id.ivRed);
        ImageView ivBlue= (ImageView)findViewById(R.id.ivBlue);
        ImageView ivGreen= (ImageView)findViewById(R.id.ivGreen);
        ImageView ivYellow= (ImageView)findViewById(R.id.ivYellow);
        int resourceID;
        String bottleName[]={"red","yellow","blue","green"};
        resourceID=getResources().getIdentifier(bottleName[bottleNumber]+bottleLevel,"drawable",getPackageName());
        Log.d("Setting Image View",bottleName[bottleNumber]+bottleLevel);
        switch(bottleNumber) {
            case 0:
                ivRed.setImageResource(resourceID);
                break;
            case 1:
                ivYellow.setImageResource(resourceID);
                break;
            case 2:
                ivBlue.setImageResource(resourceID);
                break;
            case 3:
                ivGreen.setImageResource(resourceID);
                break;
        }
    }
}
