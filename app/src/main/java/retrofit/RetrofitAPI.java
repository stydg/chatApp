package retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitAPI { //api의 서비스 (행동)
        // @GET( EndPoint-자원위치(URI) )
        @GET("loginMOBILE") //.com/loginMOBILE?USER=userid&CLIENT=clientnum
        Call<String> getPosts(@Query("USER") String userid,
                                     @Query("CLIENT") int clientnum);

}

