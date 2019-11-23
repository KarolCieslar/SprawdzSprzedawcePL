package pl.globoox.sprawdzsprzedawce.Utils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GlobooX on 10.06.2018.
 */

public class QueryCheck extends StringRequest{

    private static final String CHECKOFFERID_REQUEST_URL = "https://www.sprawdzsprzedawce.pl/appAPI/checkOfferID.php";
    private Map<String, String> params;

    public QueryCheck(String offerLink, Response.Listener<String> listener) {
        super(Request.Method.POST, CHECKOFFERID_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("offer", offerLink);
    }


    @Override
    public Map<String, String> getParams() {
        return params;
    }



}
