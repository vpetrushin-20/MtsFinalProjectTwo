package serviceTest;

import api.models.*;
import api.steps.ReqresSteps;
import api.steps.SpecHelper;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;

@Epic("Api tests")
@Feature("MTS credit service")
@Story("Smoke tests")
@Owner("Петрушин Вадим Васильевич")
public class ServiceCreditSmokeTests {

    private final ReqresSteps reqresSteps = new ReqresSteps();

    @Test
    @DisplayName("Проверка метода get на получение тарифов")
    @Description("Проверка выполнения метода get на получение списка тарифов кредитования")
    @Severity(SeverityLevel.NORMAL)
    public void successGetTariffs() {
        reqresSteps.getTariffsSuccess();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("Проверка создания заявки на кредит с параметром")
    @Description("Проверка создания заявки с существующем тарифом с последующем ее удалением для очистки базы данных")
    @Severity(SeverityLevel.CRITICAL)
    public void createTrueOrder(Integer tariffId) {

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
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("Проверка статуса заявки с параметром")
    @Description("Проверка создания заявки с существующем тарифом с последующей проверкой ее статуса и удалением для очистки базы данных")
    @Severity(SeverityLevel.CRITICAL)
    public void checkStatusOrderTrue(Integer tariffId) {

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

        GetStatusOrder getStatusOrder = given().header("Authorization", "Bearer " + token)
                .spec(SpecHelper.getRequestSpec())
                .when()
                .get("loan-service/getStatusOrder?orderId={orderId}", orderId)
                .then()
                .spec(SpecHelper.getResponseSpec(200))
                .extract().body().jsonPath().getObject("orderstatus", GetStatusOrder.class);

        DeleteOrderRequest deleteOrderRequest = new DeleteOrderRequest(1, orderId);
        Response response2 = given().header("Authorization", "Bearer " + token)
                .spec(SpecHelper.getRequestSpec())
                .when()
                .body(deleteOrderRequest)
                .delete("loan-service/deleteOrder")
                .then()
                .spec(SpecHelper.getResponseSpec(200))
                .extract().response();
    }


}
