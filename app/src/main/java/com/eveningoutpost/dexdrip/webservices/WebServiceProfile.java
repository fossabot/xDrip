package com.eveningoutpost.dexdrip.webservices;

import android.util.Log;

import com.eveningoutpost.dexdrip.Models.JoH;
import com.eveningoutpost.dexdrip.Models.UserError;
import com.eveningoutpost.dexdrip.UtilityModels.Constants;
import com.eveningoutpost.dexdrip.UtilityModels.Pref;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import static com.eveningoutpost.dexdrip.Models.JoH.tolerantParseDouble;

/**
 * @author James Woglom (j@wogloms.net)
 */

public class WebServiceProfile extends BaseWebService {

    private static String TAG = "WebServiceProfile";

    public WebResponse request(String query) {
        final JSONObject reply = new JSONObject();

        // populate json structures
        try {
            // "settings":{"units":"mmol"}
            final JSONObject settings = new JSONObject();
            final boolean using_mgdl = Pref.getString("units", "mgdl").equals("mgdl");
            settings.put("units", using_mgdl ? "mg/dl" : "mmol");

            // thresholds":{"bgHigh":260,"bgTargetTop":180,"bgTargetBottom":80,"bgLow":55}
            double highMark = tolerantParseDouble(Pref.getString("highValue", "170"), 170d);
            double lowMark = tolerantParseDouble(Pref.getString("lowValue", "70"), 70d);

            if (!using_mgdl) {
                // if we're using mmol then the marks will be in mmol but should be expressed in mgdl
                // to be in line with how Nightscout presents data
                highMark = JoH.roundDouble(highMark * Constants.MMOLL_TO_MGDL, 0);
                lowMark = JoH.roundDouble(lowMark * Constants.MMOLL_TO_MGDL, 0);
            }

            final JSONObject thresholds = new JSONObject();
            thresholds.put("bgHigh", highMark);
            thresholds.put("bgLow", lowMark);

            settings.put("thresholds", thresholds);

            reply.put("settings", settings);

            Log.d(TAG, "Output: " + reply.toString());
        } catch (JSONException e) {
            UserError.Log.wtf(TAG, "Got json exception: " + e);
        }

        final JSONArray wrapper = new JSONArray();
        wrapper.append(reply);
        return new WebResponse(wrapper.toString());
    }


}