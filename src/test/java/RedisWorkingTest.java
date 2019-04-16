
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 *
 * @author vinay
 */
@ExtendWith(VertxExtension.class)
@Testcontainers
@Timeout(value = 10, timeUnit = TimeUnit.MINUTES)
public class RedisWorkingTest {

  @Container
  private final GenericContainer REDIS_CONTAINER = new GenericContainer("redis:5").withExposedPorts(6379);
  private final RedisClient client;

  public RedisWorkingTest() {
    REDIS_CONTAINER.getPortBindings().add("6379:6379");
    client = RedisClient.create(Vertx.vertx());
  }

  @Test
  public void brokenConnection(VertxTestContext testContext) {

    Observable
            .range(1, 10)
            .flatMapSingle((i) -> client.rxSet("K" + i, "V" + i).andThen(Single.just(i)))
            .map(i -> "K" + i)
            .toList()
            .doOnSuccess(l -> System.out.println("Fetching Keys" + new JsonArray(l).toString()))
            .flatMap((l) -> client.rxMgetMany(l))
            .subscribe((l) -> testContext.completeNow(), err -> testContext.failNow(err));

  }
}
