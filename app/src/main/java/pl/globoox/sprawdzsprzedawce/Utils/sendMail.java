package pl.globoox.sprawdzsprzedawce.Utils;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GlobooX on 10.06.2018.
 */

public class sendMail extends StringRequest{

    private static final String SENDMAIL_REQUEST_URL = "https://sprawdzsprzedawce.pl/appAPI/sendMail.php";
    private Map<String, String> params;

    public sendMail(String nick, String mail, String message, Response.Listener<String> listener) {
        super(Method.POST, SENDMAIL_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("nick", nick);
        params.put("mail", mail);
        params.put("message", message);
    }


    @Override
    public Map<String, String> getParams() {
        return params;
    }



}
