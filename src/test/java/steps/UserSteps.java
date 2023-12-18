package steps;

import classes.User;
import io.qameta.allure.Step;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.After;

import static constants.Constants.*;
import static io.restassured.RestAssured.given;


public class UserSteps {
    @Step("Создание пользователя")
    public static Response createUser(User user) {

        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(REGISTRATION)
                .then()
                .extract().response();
    }

    @Step("Создание пользователя")
    public static Response createUser(String email, String password, String name) {

        var user = new User(email, password, name);
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(REGISTRATION)
                .then()
                .extract().response();
    }

    @Step("Удаление пользователя")
    public static Response deleteUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .body("")
                .when()
                .delete(AUTH);
    }

    @Step("Логин пользователя")
    public static Response loginUser(String email, String password) {
        var user = new User(email, password);
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(LOGIN);
    }

    @Step("Изменение данных пользователя c авторизацией")
    public static Response editUser(User user, String token) {
        user.setEmail ("emailnew9@mail.ru");
        user.setName("namenew9");
        user.setPassword("newpassword");
        return given()
                .header("Authorization", token)
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .patch(AUTH);
    }

    @Step("Изменение данных пользователя без авторизации")
    public static Response editUser(User user) {
        user.setEmail ("emailnew9@mail.ru");
        user.setName("namenew9");
        user.setPassword("newpassword5");
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .patch(AUTH);
    }

}
