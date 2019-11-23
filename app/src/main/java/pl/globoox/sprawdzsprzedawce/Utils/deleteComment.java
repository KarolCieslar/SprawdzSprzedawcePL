package pl.globoox.sprawdzsprzedawce.Utils;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GlobooX on 10.06.2018.
 */

public class deleteComment extends StringRequest{

    private static final String DELETECOMMENT_REQUEST_URL = "https://sprawdzsprzedawce.pl/appAPI/deleteComment.php";
    private Map<String, String> params;

    public deleteComment(String accessToken, String commentID, Response.Listener<String> listener) {
        super(Method.POST, DELETECOMMENT_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("commentID", commentID);
        Log.d("TAG", commentID);
    }


    @Override
    public Map<String, String> getParams() {
        return params;
    }



}


