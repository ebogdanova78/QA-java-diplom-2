package steps;

import classes.Order;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static constants.Constants.CREATE_ORDER;
import static io.restassured.RestAssured.given;

public class OrderSteps {

    @Step("Создание закаказа c авторизацией и c ингредиентами")
    public static Response createOrderWithAuth(Order order, String token){
        return given()
                .header("Authorization", token)
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post(CREATE_ORDER);
    }

    @Step("Создание закаказа без авторизации c ингредиентами")
    public static Response createOrderWithoutAuth (Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post(CREATE_ORDER);
    }

    @Step("Создание закаказа c авторизацией без ингредиентов")
    public static Response createOrderWithAuthWithoutIngredient(String token) {
        return given()
                .header("Authorization", token)
                .header("Content-type", "application/json")
                .when()
                .post(CREATE_ORDER);
    }





}
