package io.vertx.book.http;

import io.vertx.core.AbstractVerticle;

public class HelloMicroservice extends AbstractVerticle {

    @Override
    public void start() {    	
    	vertx.createHttpServer()
    	  .requestHandler(req -> req.response()
    			  .end("hello"))
    	  .listen(8080);
    }

}
