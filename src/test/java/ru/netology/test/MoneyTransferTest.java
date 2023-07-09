package ru.netology.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.*;

public class MoneyTransferTest {
    LoginPage loginPage;
    DashboardPage dashboardPage;

    @BeforeEach
    void  setup(){
        loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCode();
        dashboardPage = verificationPage.validVerify(verificationCode);
    }
    @Test
    void shouldTransferFromFirstToSecond() {
        var cardInfoFirst = DataHelper.getFirstCardInfo();
        var cardInfoSecond = DataHelper.getSecondCardInfo();
        var firstCardBalance = dashboardPage.getCardBalance(cardInfoFirst);
        var secondCardBalance = dashboardPage.getCardBalance(cardInfoSecond);
        var amount = generateValidAmount(firstCardBalance);
        var expectedFirstCardBalance = firstCardBalance - amount;
        var expectedSecondCardBalance = secondCardBalance + amount;
        var transferPage = dashboardPage.selectCardToTransfer(cardInfoSecond);
        transferPage.validTransfer(String.valueOf(amount), cardInfoFirst);

        assertEquals(expectedFirstCardBalance, dashboardPage.getCardBalance(cardInfoFirst));
        assertEquals(expectedSecondCardBalance, dashboardPage.getCardBalance(cardInfoSecond));
    }
    @Test
    void shouldGetErrorMessageIfAmountMoreBalance() {
        var firstCardInfo = getFirstCardInfo();
        var secondCardInfo = getSecondCardInfo();
        var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        var amount = generateInvalidAmount(secondCardBalance);
        var transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
        transferPage.validTransfer(String.valueOf(amount),secondCardInfo);
        transferPage.findErrorMessage("Недостаточно средств на счету");
        var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
        var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardInfo);
        assertEquals(firstCardBalance,actualBalanceFirstCard);
        assertEquals(secondCardBalance, actualBalanceSecondCard);
    }
}

