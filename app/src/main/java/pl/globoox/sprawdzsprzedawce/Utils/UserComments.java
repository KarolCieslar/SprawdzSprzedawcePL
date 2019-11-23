package pl.globoox.sprawdzsprzedawce.Utils;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GlobooX on 10.06.2018.
 */

public class UserComments extends StringRequest{

    private static final String USERCOMMENTS_REQUEST_URL = "https://sprawdzsprzedawce.pl/appAPI/userComments.php";
    private Map<String, String> params;

    public UserComments(String userID, Response.Listener<String> listener) {
        super(Method.POST, USERCOMMENTS_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("userID", userID);
    }


    @Override
    public Map<String, String> getParams() {
        return params;
    }



}
