package me.donnior.sparkle.route;

public interface Router {

    HttpScoppedRoutingBuilder route(String path);

    void install(RouteModule module);

    RoutingBuilder match(String cAndActionString);
    
}
