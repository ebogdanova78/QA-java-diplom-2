package tests;

import classes.Order;
import classes.User;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.UserSteps;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.is;
import static steps.OrderSteps.*;

public class CreateOrderTests {

    public String EMAIL = RandomStringUtils.randomAlphabetic(8) + "@yandex.ru";
    public String PASSWORD = "RandomStringUtils.randomAlphanumeric(8)";
    public String NAME = RandomStringUtils.randomAlphanumeric(8);
    public String[] ingredients = {"61c0c5a71d1f82001bdaaa6d","61c0c5a71d1f82001bdaaa6f"};
    public String[] invalid_ingredients = {"61c0c5a71d1f82001bda6d","61c0c5a71d1f82001aaa6f"};

    protected String token;
    private final User user = new User(EMAIL, PASSWORD, NAME);

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Заказ авторизованного пользователя")
    @Description("Заказ с ингредиентами и авторизацией")
    public void createOrderWithAuthAndIngredientsPositiveTest() {
        token = UserSteps.createUser(EMAIL, PASSWORD, NAME).then().extract().path("accessToken");
        Order order = new Order(ingredients);
        createOrderWithAuth(order, token)
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .and()
                .body("success", is(true));
    }

    @Test
    @DisplayName("Заказ без авторизации")
    @Description("Заказ с ингредиентами без авторизации")
    public void createOrderWithoutAuthTest() {
        Order order = new Order(ingredients);
        createOrderWithoutAuth(order)
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .and()
                .body("success", is(true));
    }


    @Test
    @DisplayName("Заказ без ингредиентов")
    @Description("Заказ с авторизацией без ингридиентов")
    public void createOrderWithoutIngredientTest() {
        token = UserSteps.createUser(EMAIL, PASSWORD, NAME).then().extract().path("accessToken");
        createOrderWithAuthWithoutIngredient(token)
                .then()
                .assertThat()
                .statusCode(HTTP_BAD_REQUEST)
                .and()
                .body("success", is(false))
                .and()
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Заказ с невалидным хэшем ингредиента")
    @Description("Заказ с авторизацией и невалидным хэшом ингридиентов")
    public void createOrderWithIncorrectIngredientTest() {
        token = UserSteps.createUser(EMAIL, PASSWORD, NAME).then().extract().path("accessToken");
        Order order = new Order(invalid_ingredients);
        createOrderWithAuth(order, token)
                .then()
                .assertThat()
                .statusCode(HTTP_INTERNAL_ERROR);
    }

    @After
    @Description("Delete data")
    public void tearDown() {
        if (token != null)
            UserSteps.deleteUser(token);
    }

}
