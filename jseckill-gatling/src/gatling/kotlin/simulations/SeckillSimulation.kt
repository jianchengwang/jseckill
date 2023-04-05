package simulations

import io.gatling.javaapi.core.*
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.http.HttpDsl.*
import java.time.Duration

class SeckillSimulation : Simulation() {
    val baseUrl = "http://localhost:9071"
    val skGoodsId = 1
    val entryKey = "djingd!"
    private val protocol = http
            .baseUrl(baseUrl)
            .contentTypeHeader("application/json")

    val userFeeder = csv("data/users.csv").circular()

    private val scn = scenario("loginThenGetToken")
        .feed(userFeeder)
        .exec {
            session ->
                session.set("skGoodsId", skGoodsId).set("entryKey", entryKey)
        }
        .exec(http("Login By Username")
                .post("/api/auth/user/loginByUsername")
                .body(ElFileBody("bodies/LoginByUsername.json"))
                .check(status().`is`(200))
                .check(jsonPath("$.status").find().`is`("200"))
                .check(jsonPath("$.data").find().saveAs("userToken"))
        )
        .exec {
            session ->
                session
        }
        .pause(Duration.ofMillis(10))
        .exec(http("Get GoodsInfo")
                .get("/api/client/seckill/goodsInfo/#{skGoodsId}")
                .header("Authorization", "#{userToken}")
                .check(jsonPath("$.status").find().`is`("200"))
        )
        .exec(http("Get SkToken")
                .get("/api/client/seckill/token/#{skGoodsId}?entryKey=#{entryKey}")
                .header("Authorization", "#{userToken}")
                .check(bodyString().saveAs( "RESPONSE_DATA" ))
                .check(jsonPath("$.status").find().`is`("200"))
                .check(jsonPath("$.data").find().saveAs("skToken"))
        )
        .exec {
            session ->
            session
        }
        .pause(Duration.ofMillis(10))
        .exec(http("Create Order")
                .post("/api/client/seckill/order")
                .body(ElFileBody("bodies/CreateOrder.json"))
                .header("Authorization", "#{userToken}")
                .check(jsonPath("$.status").find().`is`("200"))

        )
        .pause(Duration.ofMillis(50))
        .doWhile { session -> session.getString("orderNo") == null || session.getString("orderNo") == "0" }.on(
            exec(http("Check Order")
                .get("/api/client/seckill/order/check/#{skToken}")
                .header("Authorization", "#{userToken}")
                .check(jsonPath("$.status").find().`is`("200"))
                .check(jsonPath("$.data").find().saveAs("orderNo"))
            )
        ).exec({
            session ->
//                println(session.getString("orderNo"))
                session
        })
        .pause(Duration.ofMillis(10))
        .doIf { session -> session.getString("orderNo") != null && session.getString("orderNo") != "-1" }.then(
            exec(http("Confirm PayInfo")
                .post("/api/client/seckill/order/confirmPayInfo")
                .body(ElFileBody("bodies/ConfirmPayInfo.json"))
                .header("Authorization", "#{userToken}")
                .check(jsonPath("$.status").find().`is`("200"))
            )
        )

    init {
        setUp(scn.injectOpen(
                nothingFor(Duration.ofMillis(10)),
                rampUsers(1000).during(Duration.ofSeconds(10))
        )).protocols(protocol)
    }
}
