package com.android.messaging.ui.chatbotservice;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ChatbotMenuRetrofitService {
    @GET("{id}")
    Observable<GetChatbotMenuApi> getChatbotMenu(@Path("id") String path);
}
