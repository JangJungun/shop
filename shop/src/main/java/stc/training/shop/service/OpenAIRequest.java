package stc.training.shop.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

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
    private String API_KEY="";
//    private String API_KEY="";

    private static final String URL = "https://api.openai.com/v1/images/edits";
//    private static final String URL = "https://api.openai.com/v1/images/variations";
//    private static final String URL = "https://api.kakaobrain.com/v2/inference/karlo/variations";
    public void sendRequest() throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(90, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("image/png");

        byte[] imageData = null;
        try {
            Path imagePath = Paths.get(getClass().getResource("/test.png").toURI());
            imageData = Files.readAllBytes(imagePath);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        RequestBody imageBody = RequestBody.create(imageData, mediaType);
        
        byte[] imageData2 = null;
        try {
        	Path imagePath2 = Paths.get(getClass().getResource("/mask6.png").toURI());
        	imageData2 = Files.readAllBytes(imagePath2);
        } catch (URISyntaxException | IOException e) {
        	e.printStackTrace();
        }
        RequestBody imageBody2 = RequestBody.create(imageData2, mediaType);
        
        
        
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("image","test.png", imageBody)
                .addFormDataPart("mask", "mask6.png", imageBody2)
                .addFormDataPart("prompt","모니터를 다른 브랜드의 제품으로 바꿔줘")
                .addFormDataPart("n","1")
                .addFormDataPart("size","512x512")
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
            String imageDir = System.getProperty("user.dir") + "/src/main/opimg/";
            new File(imageDir).mkdirs();  // 디렉토리가 존재하지 않으면 생성
            saveImage(imageUrl, imageDir + "image" + (i+1) + ".png");
        }
    }
    // 이미지 저장 메서드
    public void saveImage(String imageUrl, String destinationFile) throws IOException {
        URL url = new URL(imageUrl);
        byte[] b = new byte[2048];
        int length;

        try (InputStream is = url.openStream();
             OutputStream os = new FileOutputStream(destinationFile)) {
            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }
        }
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
