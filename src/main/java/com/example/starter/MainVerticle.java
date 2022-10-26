package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.grpc.VertxChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();

    vertx.deployVerticle(GrpcVerticle.class, new DeploymentOptions()).onComplete(result -> {
      if (result.succeeded()) {
        LOGGER.info("GRPC deployment complete");
      } else {
        LOGGER.error("GRPC deployment failed", result.cause());
      }
    });

    vertx.deployVerticle(BlockingVerticle.class,new DeploymentOptions().setInstances(2 * Runtime.getRuntime().availableProcessors()))
      .onComplete(result -> {
        if (result.succeeded()) {
          LOGGER.info("Blocking deployment {} complete",result);
        } else {
          LOGGER.error("Blocking deployment {} failed",result);
        }
      });

    vertx.close();
  }

  public static class BlockingVerticle extends AbstractVerticle {
    @Override
    public void start() throws Exception {
      // Simulate long blocking initialization code
      Thread.sleep(120 * 1000L);
    }
  }

  public static class GrpcVerticle extends AbstractVerticle {
    @Override
    public void start() {
      VertxChannelBuilder.forAddress(vertx, "localhost", 8080)
        .useSsl(options -> options.setSsl(true))
        .build();
    }
  }
}

