package pl.globoox.sprawdzsprzedawce.Utils;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GlobooX on 10.06.2018.
 */

public class editComment extends StringRequest{

    private static final String EDITCOMMENT_REQUEST_URL = "https://sprawdzsprzedawce.pl/appAPI/editComment.php";
    private Map<String, String> params;

    public editComment(String accessToken, String fbUID, String comment, String commentID, Response.Listener<String> listener) {
        super(Method.POST, EDITCOMMENT_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("fbUID", fbUID);
        params.put("comment", comment);
        params.put("commentID", commentID);
    }


    @Override
    public Map<String, String> getParams() {
        return params;
    }



}


