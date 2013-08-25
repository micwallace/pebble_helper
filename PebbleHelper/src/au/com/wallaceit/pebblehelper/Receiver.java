package au.com.wallaceit.pebblehelper;

import java.util.UUID;

import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.getpebble.android.kit.Constants;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

public class Receiver extends BroadcastReceiver {
	private PebbleData pebbledata;
	public Receiver(){
		pebbledata = new PebbleData();
	}

@Override
public void onReceive(Context context, Intent intent) {
	if (intent.getAction().equals(Constants.INTENT_APP_RECEIVE)) {
        final UUID receivedUuid = (UUID) intent.getSerializableExtra(Constants.APP_UUID);
           
        // Pebble-enabled apps are expected to be good citizens and only inspect broadcasts containing their UUID
        if (!PebbleData.QUOTES_UUID.equals(receivedUuid)) {
            System.out.println("not my UUID");
            return;
        }
        System.out.println("Message received.");
        final int transactionId = intent.getIntExtra(Constants.TRANSACTION_ID, -1);
        PebbleKit.sendAckToPebble(context, transactionId);
        PebbleDictionary pd;
		try {
			pd = PebbleDictionary.fromJson(intent.getStringExtra(Constants.MSG_DATA));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			pd = null;
		}
        final int updateValue = pd.getUnsignedInteger(PebbleData.CMD_KEY).intValue();
        if (pd != null) {
        	switch(updateValue){
        	case PebbleData.CMD_REFRESH:
    		if (!pebbledata.isgettingquote){
    			pebbledata.isgettingquote = true;
    			pebbledata.updateQuote(true, context);
    		}
    		break;
        	}	
        }
	}
}

}
