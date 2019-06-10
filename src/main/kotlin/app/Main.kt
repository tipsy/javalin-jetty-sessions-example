package app

import io.javalin.Javalin
import io.javalin.core.security.Role
import io.javalin.http.Context
import io.javalin.http.UnauthorizedResponse

fun main() {

    val app = Javalin.create {
        it.sessionHandler(::fileSessionHandler)
        it.accessManager { handler, ctx, roles ->
            val currentUser = ctx.sessionAttribute<String?>("current-user") // retrieve user stored during login
            when {
                currentUser == null -> redirectToLogin(ctx)
                currentUser != null && userHasValidRole(ctx, roles) -> handler.handle(ctx)
                else -> throw UnauthorizedResponse()
            }
        }
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

}

fun redirectToLogin(ctx: Context) = ctx.redirect("/login")
fun userHasValidRole(ctx: Context, roles: Set<Role>) = false // your code here
