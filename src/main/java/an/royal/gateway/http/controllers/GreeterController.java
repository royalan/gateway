package an.royal.gateway.http.controllers;

import an.royal.gateway.grpc.GreetServiceProto;
import an.royal.gateway.http.constants.ResponseErrorCode;
import an.royal.gateway.http.dto.requests.SayHelloReq;
import an.royal.gateway.http.dto.responses.ErrorResp;
import an.royal.gateway.http.dto.responses.HttpResponse;
import an.royal.gateway.http.dto.responses.SayHelloResp;
import an.royal.gateway.http.services.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by carl.huang on 12/05/2017.
 */
@RestController
@RequestMapping("/greet")
public class GreeterController {
    private static final Logger log = LoggerFactory.getLogger(GreeterController.class);

    // For demonstrating error response
    private List<String> validUserNames = Arrays.asList("Royalan", "Carl", "Cockroach");

    private IUserService userService;

    /**
     * Greet to service.
     *
     * @param greeter
     * @param req     - SayHelloReq
     * @return ResponseEntity
     */
    @RequestMapping(value = "/{greeter}", method = RequestMethod.POST)
    public ResponseEntity<?> sayHello(@PathVariable String greeter, @RequestBody SayHelloReq req) {
        log.debug("Received request to say hello!");
        if (!validUserNames.contains(greeter)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new HttpResponse(
                            new ErrorResp(req.getRequestId(), ResponseErrorCode.USER_NOT_FOUND, "Cannot find user.")));
        }

        GreetServiceProto.HelloCommandAck ack = userService.sayHello(req.getRequestId(), greeter, req.getGreeting());
        log.info(req.getRequestId() + ": Got response from gRPC server: {}", ack);

        return ResponseEntity.ok(new SayHelloResp(ack.getMessage()));
    }

    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }
}
