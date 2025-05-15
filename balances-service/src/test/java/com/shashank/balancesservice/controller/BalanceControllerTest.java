package com.shashank.balancesservice.controller;

import com.shashank.balancesservice.model.AccountBalance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class BalanceControllerTest {

    @InjectMocks
    private BalanceController balanceController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAccountBalance_ReturnsValidResponse() {
        // Arrange
        String accountNumber = "12345678";

        // Act
        ResponseEntity<AccountBalance> response = balanceController.getAccountBalance(accountNumber);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(accountNumber, response.getBody().getAccountNumber());
        assertEquals("USD", response.getBody().getCurrency());
        assertNotNull(response.getBody().getLastUpdated());
        assertTrue(response.getBody().getLastUpdated().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(response.getBody().getLastUpdated().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    public void testGetAccountBalance_AvailableBalanceIsGreaterThan10000() {
        // Arrange
        String accountNumber = "12345678";

        // Act
        ResponseEntity<AccountBalance> response = balanceController.getAccountBalance(accountNumber);

        // Assert
        AccountBalance balance = response.getBody();
        assertNotNull(balance);
        
        BigDecimal minExpectedBalance = new BigDecimal("10000.00");
        assertTrue(balance.getAvailableBalance().compareTo(minExpectedBalance) >= 0, 
                "Available balance should be greater than or equal to $10,000");
    }

    @Test
    public void testGetAccountBalance_AvailableBalanceIsLessThanOrEqualTo100000() {
        // Arrange
        String accountNumber = "12345678";

        // Act
        ResponseEntity<AccountBalance> response = balanceController.getAccountBalance(accountNumber);

        // Assert
        AccountBalance balance = response.getBody();
        assertNotNull(balance);
        
        BigDecimal maxExpectedBalance = new BigDecimal("100000.00");
        assertTrue(balance.getAvailableBalance().compareTo(maxExpectedBalance) <= 0, 
                "Available balance should be less than or equal to $100,000");
    }

    @Test
    public void testGetAccountBalance_CurrentBalanceIsGreaterThanAvailableBalance() {
        // Arrange
        String accountNumber = "12345678";

        // Act
        ResponseEntity<AccountBalance> response = balanceController.getAccountBalance(accountNumber);

        // Assert
        AccountBalance balance = response.getBody();
        assertNotNull(balance);
        
        assertTrue(balance.getCurrentBalance().compareTo(balance.getAvailableBalance()) >= 0, 
                "Current balance should be greater than or equal to available balance");
    }

    @Test
    public void testGetAccountBalance_DifferentAccountNumbersReturnDifferentBalances() {
        // Arrange
        String accountNumber1 = "12345678";
        String accountNumber2 = "87654321";

        // Act
        ResponseEntity<AccountBalance> response1 = balanceController.getAccountBalance(accountNumber1);
        ResponseEntity<AccountBalance> response2 = balanceController.getAccountBalance(accountNumber2);

        // Assert
        AccountBalance balance1 = response1.getBody();
        AccountBalance balance2 = response2.getBody();
        
        assertNotNull(balance1);
        assertNotNull(balance2);
        
        // Note: There's a tiny chance this might fail randomly if the two random balances happen to be equal
        // But the probability is extremely low
        assertNotEquals(0, balance1.getAvailableBalance().compareTo(balance2.getAvailableBalance()) | 
                           balance1.getCurrentBalance().compareTo(balance2.getCurrentBalance()),
                "Different account numbers should generally have different balances");
    }

    @Test
    public void testGetAccountBalance_BalancesHaveTwoDecimalPlaces() {
        // Arrange
        String accountNumber = "12345678";

        // Act
        ResponseEntity<AccountBalance> response = balanceController.getAccountBalance(accountNumber);

        // Assert
        AccountBalance balance = response.getBody();
        assertNotNull(balance);
        
        assertEquals(2, balance.getAvailableBalance().scale(), 
                "Available balance should have 2 decimal places");
        assertEquals(2, balance.getCurrentBalance().scale(), 
                "Current balance should have 2 decimal places");
    }

    @Test
    public void testGetAccountBalance_CurrentBalanceIsAtMost10HigherThanAvailableBalance() {
        // Arrange
        String accountNumber = "12345678";

        // Act
        ResponseEntity<AccountBalance> response = balanceController.getAccountBalance(accountNumber);

        // Assert
        AccountBalance balance = response.getBody();
        assertNotNull(balance);
        
        BigDecimal difference = balance.getCurrentBalance().subtract(balance.getAvailableBalance());
        BigDecimal maxDifference = new BigDecimal("10.00");
        
        assertTrue(difference.compareTo(maxDifference) <= 0, 
                "Difference between current and available balance should be at most $10.00");
    }

    @Test
    public void testGenerateRandomBalanceAbove10000() {
        // Arrange
        BalanceController controller = new BalanceController();
        
        // Act
        // Using reflection to access private method
        BigDecimal balance = null;
        try {
            java.lang.reflect.Method method = BalanceController.class.getDeclaredMethod("generateRandomBalanceAbove10000");
            method.setAccessible(true);
            balance = (BigDecimal) method.invoke(controller);
        } catch (Exception e) {
            fail("Failed to invoke private method: " + e.getMessage());
        }
        
        // Assert
        assertNotNull(balance);
        assertTrue(balance.compareTo(new BigDecimal("10000.00")) >= 0, 
                "Generated balance should be greater than or equal to $10,000");
        assertTrue(balance.compareTo(new BigDecimal("100000.00")) <= 0, 
                "Generated balance should be less than or equal to $100,000");
        assertEquals(2, balance.scale(), "Generated balance should have 2 decimal places");
    }
}
