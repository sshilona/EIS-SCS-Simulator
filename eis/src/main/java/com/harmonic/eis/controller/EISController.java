package com.harmonic.eis.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.harmonic.eis.Application;

@RestController
public class EISController {

    @GetMapping("/action")
    public String performAction() {
        // Perform your action here
        return "Action performed successfully!";
    }

    @PostMapping("/eis/create-client")
    public String createClient(@RequestBody String requestBody) throws IOException {
        Application.createClient(requestBody);
        return "Client Setup provisioned successfully!";
    }

    @PostMapping("/eis/channel-setup")
    public String channelSetup(@RequestBody String requestBody) throws IOException {
        return Application.channelSetup(requestBody);
    }

    @PostMapping("/eis/channel-close")
    public String channelClose(@RequestBody String requestBody) throws IOException {
        return Application.channelClose(requestBody);
    }

    @PostMapping("/eis/channel-test")
    public String channelTest(@RequestBody String requestBody) throws IOException {
        return Application.channelTest(requestBody);
    }

    @PostMapping("/eis/channel-reset")
    public String channelReset(@RequestBody String requestBody) throws IOException {
        return Application.channelReset(requestBody);
    }
    
    @PostMapping("/eis/run-script")
    public String runScript() throws IOException {
        Application.runScript();
        return "Script provisioned successfully!";
    }

    @PostMapping("/eis/scg-provision")
    public String scgProvision(@RequestBody String requestBody) throws IOException {    
        return  Application.scgProvision(requestBody);
    }

    @PostMapping("/eis/scg-list")
    public String scgList(@RequestBody String requestBody) throws IOException {    
        return  Application.scgList(requestBody);
    }

    @PostMapping("/eis/scg-test")
    public String scgTest(@RequestBody String requestBody) throws IOException {    
        return  Application.scgTest(requestBody);
    }
}
