package io.vertx.book.http;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.client.HttpRequest;
import io.vertx.rxjava.ext.web.client.HttpResponse;
import io.vertx.rxjava.ext.web.client.WebClient;
import io.vertx.rxjava.ext.web.codec.BodyCodec;
import rx.Single;

public class HelloConsumerMicroservice extends AbstractVerticle {

	private WebClient client;
	
    @Override
    public void start() {
    	client = WebClient.create(vertx);
    	
    	Router router = Router.router(vertx);
    	router.get("/").handler(this::invokeMyFirstMicroservice);

    	vertx.createHttpServer()
    		.requestHandler(router::accept)
    		.listen(8082);
    }

//    private void invokeMyFirstMicroservice(RoutingContext rc) {
//    	HttpRequest<JsonObject> request = client
//    			.get(8080, "localhost", "/vert.x")
//    			.as(BodyCodec.jsonObject());
//    	
//    	request.send(ar -> {
//    		if(ar.failed()) {
//    			rc.fail(ar.cause());
//    		} else {
//    			rc.response().end(ar.result().body().encode());
//    		}
//    	});
//    }
    
    private void invokeMyFirstMicroservice(RoutingContext rc) {
    	HttpRequest<JsonObject> request1 = client
    			.get(8080, "localhost", "/Luke")
    			.as(BodyCodec.jsonObject());
    	HttpRequest<JsonObject> request2 = client
    			.get(8080, "localhost", "/Leia")
    			.as(BodyCodec.jsonObject());
    	
    	Single<JsonObject> s1 = request1.rxSend().map(HttpResponse::body);
    	Single<JsonObject> s2 = request2.rxSend().map(HttpResponse::body);
    	
    	Single.zip(s1, s2, (luke, leia) -> {
    		//We have the results of both requests in Luke and Leia
    		return new JsonObject()
    				.put("Luke", luke.getString("message"))
    				.put("Leia", leia.getString("message"));
    	}).subscribe(
    			result -> rc.response().end(result.encodePrettily()),
    			error -> {
    				error.printStackTrace();
    				rc.response().setStatusCode(500).end(error.getMessage());
    			});
    }
}
