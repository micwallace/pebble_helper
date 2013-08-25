package au.com.wallaceit.pebblehelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.getpebble.android.kit.PebbleKit;

public class MainActivity extends Activity {
    private Button updatebutton;
    private Button launchbutton;
    private Button closebutton;
    private PebbleData pebbledata;
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		pebbledata = new PebbleData();
		updatebutton = (Button) findViewById(R.id.button);
		updatebutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pebbledata.updateQuote(true, getApplicationContext());
			}
		});
		launchbutton = (Button) findViewById(R.id.launchbutton);
		launchbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startWatchApp();
			}
		});
		closebutton = (Button) findViewById(R.id.closebutton);
		closebutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopWatchApp();
			}
		});
		//checkListeners();
		if (PebbleKit.isWatchConnected(getApplicationContext())){
			System.out.println("Watch connected"); // watch connected
		}
		if (PebbleKit.areAppMessagesSupported(getApplicationContext())){
			System.out.println("Notify Supported"); // watch connected
		}
		PebbleKit.startAppOnPebble(getApplicationContext(), PebbleData.QUOTES_UUID);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/*private void checkListeners(){
		if (dataReceiver == null) {
			registerListeners();
        }
	}*/
	
	// Send a broadcast to launch the specified application on the connected Pebble
    public void startWatchApp() {
        PebbleKit.startAppOnPebble(getApplicationContext(), PebbleData.QUOTES_UUID);
        System.out.println("Message sent: app open");
    }
    
    public void stopWatchApp() {
        PebbleKit.closeAppOnPebble(getApplicationContext(), PebbleData.QUOTES_UUID);
        System.out.println("Message sent: app close");
    }

	/*public void registerListeners() {
	
	final Handler handler = new Handler();
	dataReceiver = new PebbleKit.PebbleDataReceiver(QUOTES_UUID) {
        @Override
        public void receiveData(final Context context, final int transactionId, final PebbleDictionary data) {
        	System.out.println("Message received.");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // All data received from the Pebble must be ACK'd, otherwise you'll hit time-outs in the
                    // watch-app which will cause the watch to feel "laggy" during periods of frequent
                    // communication.
                    PebbleKit.sendAckToPebble(context, transactionId);
                    
                    
                    if (!data.iterator().hasNext()) {
                        return;
                    }
                    final Long updateValue = data.getUnsignedInteger(CMD_KEY);
                    if (updateValue != null) {
                    	switch(updateValue.intValue()){
                    	case UP_KEY:
                    		//if (!isgettingquote){
                    			//isgettingquote = true;
                    			//updateQuote(true);
                    		//}
                    		break;
                    	}	
                    } else {
                    	PebbleKit.sendNackToPebble(context, transactionId);
                    }
                }
            });
        }
    };
    
    PebbleKit.registerReceivedDataHandler(this, dataReceiver);
	
	}*/
}


