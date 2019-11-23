package pl.globoox.sprawdzsprzedawce.Utils;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GlobooX on 10.06.2018.
 */

public class AccountComments extends StringRequest{

    private static final String ACCOUNTCMMENTS_REQUEST_URL = "https://sprawdzsprzedawce.pl/appAPI/accountComments.php";
    private Map<String, String> params;

    public AccountComments(String fbID, Response.Listener<String> listener) {
        super(Method.POST, ACCOUNTCMMENTS_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("fbID", fbID);
    }


    @Override
    public Map<String, String> getParams() {
        return params;
    }



}
