package ru.netology.test;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.*;

public class MoneyTransferTest {
    @Test
    void shouldTransferFromFirstToSecond() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCode(authInfo);
        var cardBalance = verificationPage.validVerify(verificationCode);
        var cardInfoFirst = DataHelper.getFirstCardInfo();
        var cardInfoSecond = DataHelper.getSecondCardInfo();
        var firstCardBalance = cardBalance.getCardBalance(cardInfoFirst);
        var secondCardBalance = cardBalance.getCardBalance(cardInfoSecond);
        var amount = generateValidAmount(firstCardBalance);
        var expectedFirstCardBalance = firstCardBalance + amount;
        var expectedSecondCardBalance = secondCardBalance - amount;
        var transactionPage = cardBalance.selectCardToTransfer(cardInfoFirst);
        transactionPage.validTransfer(String.valueOf(amount), cardInfoSecond);

        assertEquals(expectedFirstCardBalance, cardBalance.getCardBalance(cardInfoFirst));
        assertEquals(expectedSecondCardBalance, cardBalance.getCardBalance(cardInfoSecond));
    }

    @Test
    void shouldGetErrorMessageIfAmountMoreBalance() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCode(authInfo);
        var cardBalance = verificationPage.validVerify(verificationCode);
        var cardInfoFirst = DataHelper.getFirstCardInfo();
        var cardInfoSecond = DataHelper.getSecondCardInfo();
        var firstCardBalance = cardBalance.getCardBalance(cardInfoFirst);
        var secondCardBalance = cardBalance.getCardBalance(cardInfoSecond);
        var amount = generateInvalidAmount(secondCardBalance);
        var transactionPage = cardBalance.selectCardToTransfer(cardInfoSecond);
        transactionPage.validTransfer(String.valueOf(amount), cardInfoFirst);
        transactionPage.findErrorMessage("Ошибка! Недостаточно средств на счету.");

        assertEquals(firstCardBalance, cardBalance.getCardBalance(cardInfoFirst));
        assertEquals(secondCardBalance, cardBalance.getCardBalance(cardInfoSecond));
    }
}

