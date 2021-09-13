package app;

import io.javalin.Javalin;
import io.javalin.core.security.RouteRole;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import java.util.Set;

public class Main {
    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {
            config.sessionHandler(Sessions::fileSessionHandler);
            config.accessManager((handler, ctx, roles) -> {
                String currentUser = ctx.sessionAttribute("current-user"); // retrieve user stored during login
                if (currentUser == null) {
                    redirectToLogin(ctx);
                } else if (userHasValidRole(ctx, roles)) {
                    handler.handle(ctx);
                } else {
                    throw new UnauthorizedResponse();
                }
            });
        }).start(7000);

        app.get("/write", ctx -> {
            // values written to the session will be available on all your instances if you use a session db
            ctx.sessionAttribute("my-key", "My value");
        });

        app.get("/read", ctx -> {
            // values on the session will be available on all your instances if you use a session db
            String myValue = ctx.sessionAttribute("my-key");
        });

        app.get("/invalidate", ctx -> {
            // if you want to invalidate a session, jetty will clean everything up for you
            ctx.req.getSession().invalidate();
        });

        app.get("/change-id", ctx -> {
            // it could be wise to change the session id on login, to protect against session fixation attacks
            ctx.req.changeSessionId();
        });

    }

    private static boolean userHasValidRole(Context ctx, Set<RouteRole> roles) {
        return false; // your code here
    }

    private static void redirectToLogin(Context ctx) {
    }

}
