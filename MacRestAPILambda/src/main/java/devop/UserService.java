package devop;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import entities.User;

import java.util.List;
import java.util.Map;

public class UserService {
    private DynamoDBMapper dynamoDBMapper;
    private static  String jsonBody = null;

    public APIGatewayProxyResponseEvent saveUser(APIGatewayProxyRequestEvent apiGatewayRequest, Context context){
        initDynamoDB();
        User user = Utility.convertStringToObj(apiGatewayRequest.getBody(),context);
        dynamoDBMapper.save(user);
        jsonBody = Utility.convertObjToString(user,context) ;
        context.getLogger().log("data saved successfully to dynamodb:::" + jsonBody);
        return createAPIResponse(jsonBody,201,Utility.createHeaders());
    }
    public APIGatewayProxyResponseEvent getUserById(APIGatewayProxyRequestEvent apiGatewayRequest, Context context){
        initDynamoDB();
        String userID = apiGatewayRequest.getPathParameters().get("userId");
        User user =   dynamoDBMapper.load(User.class,userID)  ;
        if(user!=null) {
            jsonBody = Utility.convertObjToString(user, context);
            context.getLogger().log("fetch user By ID:::" + jsonBody);
            return createAPIResponse(jsonBody,200,Utility.createHeaders());
        }else{
            jsonBody = "User Not Found Exception :" + userID;
            return createAPIResponse(jsonBody,400,Utility.createHeaders());
        }

    }

    public APIGatewayProxyResponseEvent getUsers(APIGatewayProxyRequestEvent apiGatewayRequest, Context context){
        initDynamoDB();
        List<User> users = dynamoDBMapper.scan(User.class,new DynamoDBScanExpression());
        jsonBody =  Utility.convertListOfObjToString(users,context);
        context.getLogger().log("fetch user List:::" + jsonBody);
        return createAPIResponse(jsonBody,200,Utility.createHeaders());
    }
    public APIGatewayProxyResponseEvent deleteUserById(APIGatewayProxyRequestEvent apiGatewayRequest, Context context){
        initDynamoDB();
        String userID = apiGatewayRequest.getPathParameters().get("userId");
        User user =  dynamoDBMapper.load(User.class,userID)  ;
        if(user!=null) {
            dynamoDBMapper.delete(user);
            context.getLogger().log("data deleted successfully :::" + userID);
            return createAPIResponse("data deleted successfully." + userID,200,Utility.createHeaders());
        }else{
            jsonBody = "User Not Found Exception :" + userID;
            return createAPIResponse(jsonBody,400,Utility.createHeaders());
        }
    }


    private void initDynamoDB(){
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        dynamoDBMapper = new DynamoDBMapper(client);
    }
    private APIGatewayProxyResponseEvent createAPIResponse(String body, int statusCode, Map<String,String> headers ){
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.setBody(body);
        responseEvent.setHeaders(headers);
        responseEvent.setStatusCode(statusCode);
        return responseEvent;
    }

}
