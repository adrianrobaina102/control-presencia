package software.ulpgc.controlpresencia.infrastructure.adapters;

import io.javalin.Javalin;

public interface EndPoint {
    void execute(Javalin app);
}
