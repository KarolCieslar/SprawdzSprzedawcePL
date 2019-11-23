package pl.globoox.sprawdzsprzedawce.Utils;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GlobooX on 10.06.2018.
 */

public class ranksDisplay extends StringRequest {

    private static final String RANKS_REQUEST_URL = "https://sprawdzsprzedawce.pl/appAPI/ranksDisplay.php";
    private Map<String, String> params;

    public ranksDisplay(String count, String type, Response.Listener<String> listener) {
        super(Method.POST, RANKS_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("count", count);
        params.put("type", type);
    }


    @Override
    public Map<String, String> getParams() {
        return params;
    }


}
