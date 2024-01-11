package stc.training.shop.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component
public class OpenAIRequest {

//    @Value("${openai.key}")
//    private String API_KEY;
    private String API_KEY="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";

    private static final String URL = "https://api.openai.com/v1/images/edits";

    public void sendRequest() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("image/png");

        byte[] imageData = null;
        try {
            Path imagePath = Paths.get(getClass().getResource("/test.png").toURI());
            imageData = Files.readAllBytes(imagePath);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        RequestBody imageBody = RequestBody.create(imageData, mediaType);
        
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("image","test.png", imageBody)
                
                .addFormDataPart("prompt","이 사진에 어울리는 가구 하나를 배치해줘")
                .addFormDataPart("n","2")
                .addFormDataPart("size","1024x1024")
                .build();

        Request request = new Request.Builder()
                .url(URL)
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
        
        //////////
        
        Response okhttpResponse = client.newCall(request).execute();

        Gson gson = new Gson();
        stc.training.shop.model.Response parsedResponse = gson.fromJson(okhttpResponse.body().string(), stc.training.shop.model.Response.class);

        // 이미지 URL에서 이미지 다운로드 및 저장
        for (int i = 0; i < parsedResponse.getData().size(); i++) {
            String imageUrl = parsedResponse.getData().get(i).getUrl();
            saveImage(imageUrl, "resources/image" + i + ".png");
        }
        
    }
    // 이미지 저장 메서드
    public void saveImage(String imageUrl, String destinationFile) throws IOException {
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(destinationFile);

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        is.close();
        os.close();
    }
    
    public static void main(String[] args) {
        try {
            OpenAIRequest openAIRequest = new OpenAIRequest();
            System.out.println("API KEY:"+openAIRequest.API_KEY);
            openAIRequest.sendRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}
