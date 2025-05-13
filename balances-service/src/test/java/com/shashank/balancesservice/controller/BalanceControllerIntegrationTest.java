package com.shashank.balancesservice.controller;

import com.shashank.balancesservice.model.AccountBalance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BalanceControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetAccountBalance() {
        // Arrange
        String accountNumber = "12345678";
        String url = "http://localhost:" + port + "/api/balances/" + accountNumber;

        // Act
        ResponseEntity<AccountBalance> response = restTemplate.getForEntity(url, AccountBalance.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AccountBalance balance = response.getBody();
        assertNotNull(balance);
        assertEquals(accountNumber, balance.getAccountNumber());
        assertEquals("USD", balance.getCurrency());
        assertNotNull(balance.getLastUpdated());
        
        // Balance checks
        assertTrue(balance.getAvailableBalance().compareTo(new BigDecimal("10000.00")) >= 0, 
                "Available balance should be at least $10,000");
        assertTrue(balance.getCurrentBalance().compareTo(balance.getAvailableBalance()) >= 0, 
                "Current balance should be greater than or equal to available balance");
    }

    @Test
    public void testGetAccountBalanceWithDifferentAccountNumbers() {
        // Arrange
        String accountNumber1 = "12345678";
        String accountNumber2 = "87654321";
        String url1 = "http://localhost:" + port + "/api/balances/" + accountNumber1;
        String url2 = "http://localhost:" + port + "/api/balances/" + accountNumber2;

        // Act
        ResponseEntity<AccountBalance> response1 = restTemplate.getForEntity(url1, AccountBalance.class);
        ResponseEntity<AccountBalance> response2 = restTemplate.getForEntity(url2, AccountBalance.class);

        // Assert
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        
        AccountBalance balance1 = response1.getBody();
        AccountBalance balance2 = response2.getBody();
        
        assertNotNull(balance1);
        assertNotNull(balance2);
        
        assertEquals(accountNumber1, balance1.getAccountNumber());
        assertEquals(accountNumber2, balance2.getAccountNumber());
        
        // Note: Balance values will be random, but both should follow the service's requirements
        assertTrue(balance1.getAvailableBalance().compareTo(new BigDecimal("10000.00")) >= 0);
        assertTrue(balance2.getAvailableBalance().compareTo(new BigDecimal("10000.00")) >= 0);
        
        assertTrue(balance1.getCurrentBalance().compareTo(balance1.getAvailableBalance()) >= 0);
        assertTrue(balance2.getCurrentBalance().compareTo(balance2.getAvailableBalance()) >= 0);
    }
}
