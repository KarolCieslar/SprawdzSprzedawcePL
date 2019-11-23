package pl.globoox.sprawdzsprzedawce.Utils;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GlobooX on 10.06.2018.
 */

public class AddUser extends StringRequest{

    private static final String USERCOMMENTS_REQUEST_URL = "https://sprawdzsprzedawce.pl/appAPI/addUser.php";
    private Map<String, String> params;

    public AddUser(String isBank, String userID, String registerDate, String userName, Response.Listener<String> listener) {
        super(Method.POST, USERCOMMENTS_REQUEST_URL, listener, null);
        params = new HashMap<>();
        Log.d("TAG", isBank);
        Log.d("TAG", userID);
        Log.d("TAG", userName);
        Log.d("TAG", registerDate);
        params.put("isBank", isBank);
        params.put("userID", userID);
        params.put("userName", userName);
        params.put("registerDate", registerDate);
    }


    @Override
    public Map<String, String> getParams() {
        return params;
    }



}
