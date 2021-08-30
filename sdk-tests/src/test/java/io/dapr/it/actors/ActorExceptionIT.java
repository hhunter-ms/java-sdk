/*
 * Copyright (c) Microsoft Corporation and Dapr Contributors.
 * Licensed under the MIT License.
 */

package io.dapr.it.actors;

import io.dapr.actors.ActorId;
import io.dapr.actors.client.ActorProxy;
import io.dapr.actors.client.ActorProxyBuilder;
import io.dapr.it.BaseIT;
import io.dapr.it.actors.app.MyActor;
import io.dapr.it.actors.app.MyActorService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

import static io.dapr.it.Retry.callWithRetry;
import static io.dapr.it.TestUtils.assertThrowsDaprException;
import static io.dapr.it.TestUtils.assertThrowsDaprExceptionSubstring;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ActorExceptionIT extends BaseIT {

  private static Logger logger = LoggerFactory.getLogger(ActorExceptionIT.class);

  @Test
  public void exceptionTest() throws Exception {
    // The call below will fail if service cannot start successfully.
    startDaprApp(
        ActorExceptionIT.class.getSimpleName(),
        MyActorService.SUCCESS_MESSAGE,
        MyActorService.class,
        true,
        60000);

    logger.debug("Creating proxy builder");
    ActorProxyBuilder<MyActor> proxyBuilder =
        new ActorProxyBuilder("MyActorTest", MyActor.class, newActorClient());
    logger.debug("Creating actorId");
    ActorId actorId1 = new ActorId("1");
    logger.debug("Building proxy");
    MyActor proxy = proxyBuilder.build(actorId1);

    callWithRetry(() -> {
      assertThrowsDaprExceptionSubstring(
          "INTERNAL",
          "INTERNAL: error invoke actor method: error from actor service",
          () ->  proxy.throwException());
    }, 5000);



  }
}