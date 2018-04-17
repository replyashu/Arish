package ashu.arishdemo.utils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by apple on 17/04/18.
 */

public interface NetworkInterface {

    @GET
    Call<ResponseBody> getUsers(@Url String url);
}
