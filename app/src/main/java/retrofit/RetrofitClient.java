package retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// 인터페이스 클라이언트 객체 생성
public class RetrofitClient {
    Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    //Retrofit 인스턴스 생성(초기화)
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://110.45.156.137:10209/")    // baseUrl 등록
            .addConverterFactory(GsonConverterFactory.create(gson))  // Gson 변환기 등록
            .build();

    public retrofit.RetrofitAPI service = retrofit.create(retrofit.RetrofitAPI.class);   // 레트로핏 인터페이스 객체 구현
}
