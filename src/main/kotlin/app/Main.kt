package app

import io.javalin.Context
import io.javalin.Javalin
import io.javalin.UnauthorizedResponse
import io.javalin.security.Role

fun main(args: Array<String>) {

    val app = Javalin.create().apply {
        sessionHandler(::fileSessionHandler)
    }.start(7000)

    app.get("/write") { ctx ->
        // values written to the session will be available on all your instances if you use a session db
        ctx.sessionAttribute("my-key", "My value")
    }

    app.get("/read") { ctx ->
        // values on the session will be available on all your instances if you use a session db
        val myValue = ctx.sessionAttribute<String>("my-key")
    }

    app.get("/invalidate") { ctx ->
        // if you want to invalidate a session, jetty will clean everything up for you
        ctx.req.session.invalidate()
    }

    app.get("/change-id") { ctx ->
        // it could be wise to change the session id on login, to protect against session fixation attacks
        ctx.req.changeSessionId()
    }

    app.accessManager { handler, ctx, roles ->
        val currentUser = ctx.sessionAttribute<String?>("current-user") // retrieve user stored during login
        when {
            currentUser == null -> redirectToLogin(ctx)
            currentUser != null && userHasValidRole(ctx, roles) -> handler.handle(ctx)
            else -> throw UnauthorizedResponse()
        }
    }

}

fun redirectToLogin(ctx: Context) = ctx.redirect("/login")
fun userHasValidRole(ctx: Context, roles: Set<Role>) = false // your code here
