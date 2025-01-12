package serviceTest;

import api.models.*;
import api.steps.ReqresSteps;
import api.steps.SpecHelper;
import io.qameta.allure.*;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;

@Epic("Api tests")
@Feature("MTS credit service")
@Story("Regress tests")
@Owner("Петрушин Вадим Васильевич")
public class ServiceCreditRegressTest {

    private final ReqresSteps reqresSteps = new ReqresSteps();

    @ParameterizedTest
    @ValueSource(ints = {0, 4, -1})
    @DisplayName("Проверка ошибки при создании заявки по тарифу с параметром")
    @Description("Проверка создания заявки с несуществующем тарифом и проверка текста ошибки")
    @Severity(SeverityLevel.NORMAL)
    public void createFalseOrderIdTariff(Integer tariffId) {

        Authentication auth = new Authentication("ivanov@mail.ru", "1234");
        Response response = reqresSteps.authUserSuccess(auth);

        String token = response.jsonPath().get("token");

        CreateOrderRequest createOrderRequest = new CreateOrderRequest(1, tariffId);
        Response response1 = given().header("Authorization", "Bearer " + token)
                .spec(SpecHelper.getRequestSpec())
                .when()
                .body(createOrderRequest)
                .post("loan-service/order")
                .then()
                .spec(SpecHelper.getResponseSpec(400))
                .extract().response();

        ResponseBody responseBody = response1.getBody();
        String errorMessage = responseBody.asString();
        assert errorMessage.contains("Тариф не найден");

    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2, -1})
    @DisplayName("Проверка ошибки при создании заявки по id юзера с параметром")
    @Description("Проверка создания заявки с несуществующем id юзера и проверка ошибки")
    @Severity(SeverityLevel.NORMAL)
    public void createFalseOrderIdUser(Integer userId) {

        Authentication auth = new Authentication("ivanov@mail.ru", "1234");
        Response response = reqresSteps.authUserSuccess(auth);

        String token = response.jsonPath().get("token");

        CreateOrderRequest createOrderRequest = new CreateOrderRequest(userId, 2);
        Response response1 = given().header("Authorization", "Bearer " + token)
                .spec(SpecHelper.getRequestSpec())
                .when()
                .body(createOrderRequest)
                .post("loan-service/order")
                .then()
                .spec(SpecHelper.getResponseSpec(400))
                .extract().response();

        ResponseBody responseBody = response1.getBody();
        String errorMessage = responseBody.asString();
        assert errorMessage.contains("Пользователь не найден");

    }

    @Test
    @DisplayName("Проверка ошибки при удалении заявки по id")
    @Description("Проверка на удаление заявки с несуществующем id и проверка текста ошибки")
    @Severity(SeverityLevel.NORMAL)
    public void deleteOrderFalseId() {

        Authentication auth = new Authentication("ivanov@mail.ru", "1234");
        Response response = reqresSteps.authUserSuccess(auth);

        String token = response.jsonPath().get("token");

        DeleteOrderRequest deleteOrderRequest = new DeleteOrderRequest(1, "9c675d0b-da13-4a07-bb73-66544384539c");
        Response response2 = given().header("Authorization", "Bearer " + token)
                .spec(SpecHelper.getRequestSpec())
                .when()
                .body(deleteOrderRequest)
                .delete("loan-service/deleteOrder")
                .then()
                .spec(SpecHelper.getResponseSpec(400))
                .extract().response();

        ResponseBody responseBody = response2.getBody();
        String errorMessage = responseBody.asString();
        assert errorMessage.contains("Заявка не найдена");

    }

    @Test
    @DisplayName("Проверка ошибки при получении статуса заявки по id")
    @Description("Проверка на получение статуса заявки с несуществующем id")
    @Severity(SeverityLevel.NORMAL)
    public void getStatusOrderFalse() {

        Authentication auth = new Authentication("ivanov@mail.ru", "1234");
        Response response = reqresSteps.authUserSuccess(auth);

        String token = response.jsonPath().get("token");

        GetStatusOrder getStatusOrder = given().header("Authorization", "Bearer " + token)
                .spec(SpecHelper.getRequestSpec())
                .when()
                .get("loan-service/getStatusOrder?orderId=9c675d0b-da13-4a07-bb73-66544384539c")
                .then()
                .spec(SpecHelper.getResponseSpec(400))
                .extract().body().jsonPath().getObject("orderstatus", GetStatusOrder.class);


    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("Проверка ошибки получения статуса заявки с параметром")
    @Description("Проверка создания заявки с существующем тарифом с последующем удалением и проверкой статуса заявки")
    @Severity(SeverityLevel.CRITICAL)
    public void checkStatusOrderFalse(Integer tariffId) {

        Authentication auth = new Authentication("ivanov@mail.ru", "1234");
        Response response = reqresSteps.authUserSuccess(auth);

        String token = response.jsonPath().get("token");

        CreateOrderRequest createOrderRequest = new CreateOrderRequest(1, tariffId);
        Response response1 = given().header("Authorization", "Bearer " + token)
                .spec(SpecHelper.getRequestSpec())
                .when()
                .body(createOrderRequest)
                .post("loan-service/order")
                .then()
                .spec(SpecHelper.getResponseSpec(200))
                .extract().response();

        String orderId = response1.jsonPath().get("data.orderId");

        DeleteOrderRequest deleteOrderRequest = new DeleteOrderRequest(1, orderId);
        Response response2 = given().header("Authorization", "Bearer " + token)
                .spec(SpecHelper.getRequestSpec())
                .when()
                .body(deleteOrderRequest)
                .delete("loan-service/deleteOrder")
                .then()
                .spec(SpecHelper.getResponseSpec(200))
                .extract().response();

        GetStatusOrder getStatusOrder = given().header("Authorization", "Bearer " + token)
                .spec(SpecHelper.getRequestSpec())
                .when()
                .get("loan-service/getStatusOrder?orderId={orderId}", orderId)
                .then()
                .spec(SpecHelper.getResponseSpec(400))
                .extract().body().jsonPath().getObject("orderstatus", GetStatusOrder.class);

    }
}
