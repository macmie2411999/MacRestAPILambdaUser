package devop;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayRequest, Context context) {
        UserService userService = new UserService();
        switch (apiGatewayRequest.getHttpMethod()) {

            case "POST":
                return userService.saveUser(apiGatewayRequest, context);

            case "GET":
                if (apiGatewayRequest.getPathParameters() != null) {
                    return userService.getUserById(apiGatewayRequest, context);
                }
                return userService.getUsers(apiGatewayRequest, context);
            case "DELETE":
                if (apiGatewayRequest.getPathParameters() != null) {
                    return userService.deleteUserById(apiGatewayRequest, context);
                }
            default:
                throw new Error("Unsupported Methods:::" + apiGatewayRequest.getHttpMethod());

        }
    }
 }
