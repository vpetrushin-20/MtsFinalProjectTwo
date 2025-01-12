package api.steps;

import api.models.*;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ReqresSteps {

    @Step("Отправить запрос GET http://localhost:8080/loan-service/getTariffs")
    public static Tariff getTariffsSuccess() {

        return given()
                .spec(SpecHelper.getRequestSpec())
                .when()
                .get("loan-service/getTariffs")
                .then()
                .spec(SpecHelper.getResponseSpec(200))
                .extract().body().jsonPath().getObject("tariffs", Tariff.class);
    }

    @Step("Отправить запрос POST http://localhost:8080/auth/authenticate")
    public static Response authUserSuccess(Authentication auth) {

        return given()
                .spec(SpecHelper.getRequestSpec())
                .when()
                .body(auth)
                .post("auth/authenticate")
                .then()
                .spec(SpecHelper.getResponseSpec(200))
                .extract()
                .response();
    }

}