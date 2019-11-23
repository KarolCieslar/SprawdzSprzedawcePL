package pl.globoox.sprawdzsprzedawce.Utils;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GlobooX on 10.06.2018.
 */

public class LastCommentHome extends StringRequest{

    private static final String LASTCOMMENTHOME_REQUEST_URL = "https://sprawdzsprzedawce.pl/appAPI/lastCommentHome.php";
    private Map<String, String> params;

    public LastCommentHome(String count, Response.Listener<String> listener) {
        super(Method.POST, LASTCOMMENTHOME_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("count", count);
    }


    @Override
    public Map<String, String> getParams() {
        return params;
    }



}
