import Classes.RequestStatus;
import Classes.RestPath;

public class Main {
    public static void main(String[] args) {
        RestWrapper restWrapper = new RestWrapper();

        // Test POST request
        RequestStatus postStatus = restWrapper.sendPost(RestPath.POST_PATH, "Test Payload");
        System.out.println("POST Request Success: " + postStatus.isSuccess());
        System.out.println("POST Request Message: " + postStatus.getMessage());

        // Test GET request
        RequestStatus getStatus = restWrapper.sendGet(RestPath.GET_PATH);
        System.out.println("GET Request Success: " + getStatus.isSuccess());
        System.out.println("GET Request Message: " + getStatus.getMessage());
    }
}
