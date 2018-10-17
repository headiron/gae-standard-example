package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@SpringBootApplication
@RestController
public class DemoApplication {
    private final Gson gson = new Gson();
    private final JsonParser jsonParser = new JsonParser();

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@GetMapping("/")
    public String hello(){
	    return "hello world:";
    }
    @PostMapping("/message")
    public void message(HttpServletRequest request, HttpServletResponse resp) throws IOException
    {
        try{
            while(request.getHeaderNames().hasMoreElements() ){
                String name = request.getHeaderNames().nextElement();
                System.out.println("request.header["+name+"]:"+request.getHeader(name));
            }
            String requestBody = request.getReader().lines().collect(Collectors.joining("\n"))      ;
            System.out.println(requestBody.getBytes().length+" bytes");
            System.out.println("body:"+requestBody);
            JsonElement jsonRoot = jsonParser.parse(requestBody);
            String messageStr = jsonRoot.getAsJsonObject().get("message").toString();
            Message message = gson.fromJson(messageStr, Message.class);
            String decoded = decode(message.getData());
            System.out.println("decoded:"+decoded);
            message.setData(decoded);
            System.out.println("message:"+message);
            resp.setStatus(200);
        }
        catch(Exception e){
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private String decode(String data) {
        return new String(Base64.getDecoder().decode(data));
    }
}
