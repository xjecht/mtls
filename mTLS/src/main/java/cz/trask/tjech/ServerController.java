package cz.trask.tjech;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class ServerController {

    @GetMapping("/connect")
    public String get(Principal principal) {
        return "Successfully connected: " + principal.getName();
    }
}