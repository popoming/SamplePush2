package org.androidtown.push;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    EditText messageInput;
    TextView messageOutput;
    TextView log;

    String regId;

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageInput = (EditText) findViewById(R.id.messageInput);
        messageOutput = (TextView) findViewById(R.id.messageOutput);
        log = (TextView) findViewById(R.id.log);

        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void OnClick(View view) {
                String input = messageInput.getText().toString();
                send(intput);
            }
        });

        queue = Volley.newRequestQueue(getApplicationContext());

        getRegistrationId();
    }

    public void getRegistrationId() {
        println("getRegistrationId() 호출됨");

        regId = FirebaseInstanceId.getInstance().getToken();
        println("regId : " + regId);
    }

    public void send(String input) {
        JSONObject rquestData = new JSONObject();

        try {

            requestData.put("priority", "high");
            JSONObject dataObj = new JSONObject();
            dataObj.put("contents", input);
            requestDate.put("data", dataObj);

            JSONArray idArray = new JSONArray();
            idArray.put(0, regId);
            requestData.put("registration_ids", idArray);
        }catch(Exception e) {
            e.printStackTrace();
        }

        sendData(requestData, new SendResponseListener() {
            @Override
            public void onRequestCompleted() {
                println("onRequestCompleted() 호출됨.");
            }

            @Override
            public void onRequestStarted() {
                println("onRequestStarted() 호출됨.");
            }

            @Override
            public void onRequestWithError(VolleyError error) {
                println("onRequestWithError() 호출됨.");
            }

        });
    }


    public void sendData(JSONObject requestData, final SendResponseListener listener) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                "http://fcm.googleapis.com/fcm/send",
                requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onRequestCompleted();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError Error) {
                listener.onRequestWithError(error);
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "key=AAAAaUHan2E:APA91bHXHdfVLRZi2mNYmEthzGF_KAGD327c4ndWKwZkQI5FTf4eLqAHSX0l_-SMDMpSEfFDZRSiUYiOdH7QdvgQIAP3DG89fD3-nchNNkjrANpwKenH4yD1G71ovfgo4OAgCNwhwPzK");

                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        request.setShouldCache(false);
        listener.onRequestStarted();
        queue.add(request);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        println("onNewIntent() called.");

        if (intent != null) {
            processIntent(intent);
        }
        super.onNewIntent(intent);
    }

    private void processIntent(Intent intent) {
        String from = intent.getStringExtra("from");
        if(from == null) {
            println("from is null.");
            return;
        }

        String contents = intent.getStringExtra("contents");

        println("DATA : " + from + ", " + contents);
        messageOutput.setText("[" + from + "]로부터 수신한 데이터 : " + contents);
    }
}

public interface SendResponseListener {
    public void onRequestStarted();
    public void onRequestCompleted();
    public void onRequestWithError(VolleyError error);
}