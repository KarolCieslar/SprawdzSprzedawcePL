package pl.globoox.sprawdzsprzedawce.Utils;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GlobooX on 10.06.2018.
 */

public class reportComment extends StringRequest{

    private static final String REPORTCOMMENT_REQUEST_URL = "https://sprawdzsprzedawce.pl/appAPI/reportComment.php";
    private Map<String, String> params;

    public reportComment(String accessToken, String commentID, String comment, String fbUID, Response.Listener<String> listener) {
        super(Method.POST, REPORTCOMMENT_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("comment", comment);
        params.put("commentID", commentID);
        params.put("fbUID", fbUID);
    }


    @Override
    public Map<String, String> getParams() {
        return params;
    }



}
