package systems.beemo.cloudsystem.master.network.web.router

class Router {

    private val routes: MutableMap<String, Route> = mutableMapOf()

    fun registerRoute(path: String, route: Route) {
        if (this.routes.containsKey(path)) return
        this.routes[path] = route
    }

    fun getRoute(path: String): Route? {
        return this.routes[path]
    }
}