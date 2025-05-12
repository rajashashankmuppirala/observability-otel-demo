@echo off
setlocal enabledelayedexpansion

echo Starting microservices and transaction generator...

:: Start balances-service
echo Starting balances-service...
start /B cmd /c "cd balances-service && mvnw.cmd spring-boot:run > balances-service.log 2>&1"

:: Start transaction-service 
echo Starting transaction-service...
start /B cmd /c "cd transaction-service && mvnw.cmd spring-boot:run > transaction-service.log 2>&1"

:: Wait for services to start up
echo Waiting for services to start up...
timeout /t 30 /nobreak > nul

:: Function to generate random characters
:generateRandom
set "characters=abcdefghijklmnopqrstuvwxyz0123456789"
set "result="
for /L %%i in (1,1,8) do (
    set /a rand=!random! %% 36
    for %%j in (!rand!) do set "result=!result!!characters:~%%j,1!"
)
exit /b

:: Function to generate random currency
:generateCurrency
set /a currIndex=!random! %% 3
if !currIndex! equ 0 (
    set "result=USD"
) else if !currIndex! equ 1 (
    set "result=EUR"
) else (
    set "result=GBP"
)
exit /b

:: Function to generate random description
:generateDescription
set "descriptions[0]=Payment for services"
set "descriptions[1]=Monthly subscription"
set "descriptions[2]=Online purchase"
set "descriptions[3]=Utility bill"
set "descriptions[4]=Salary deposit"
set "descriptions[5]=Transfer to savings"

set /a descIndex=!random! %% 6
set "result=!descriptions[%descIndex%]!"
exit /b

:: Function to generate random transaction type
:generateType
set /a typeIndex=!random! %% 3
if !typeIndex! equ 0 (
    set "result=DEPOSIT"
) else if !typeIndex! equ 1 (
    set "result=WITHDRAWAL"
) else (
    set "result=TRANSFER"
)
exit /b

:: Function to send random transaction
:sendTransaction
call :generateRandom
set "accountId=acc_%result%"

set /a amount=!random! %% 1000 + 1

call :generateType
set "transactionType=!result!"

call :generateCurrency
set "currency=!result!"

call :generateDescription
set "description=!result!"

set "jsonData={\"accountId\":\"!accountId!\",\"amount\":!amount!,\"currency\":\"!currency!\",\"description\":\"!description!\",\"type\":\"!transactionType!\"}"
echo Sending transaction: !jsonData!

curl -X POST "http://localhost:8080/api/transactions" -H "Content-Type: application/json" -d "!jsonData!"
echo.
exit /b

:: Function to get all transactions
:getAllTransactions
echo Getting all transactions...
curl -X GET "http://localhost:8080/api/transactions"
echo.
exit /b

:: Send random transactions
echo Starting to send random transactions...
for /L %%i in (1,1,10) do (
    call :sendTransaction
    set /a sleepTime=!random! %% 5 + 1
    timeout /t !sleepTime! /nobreak > nul
)

:: Get all transactions
call :getAllTransactions

echo Press Ctrl+C to stop the services and exit
echo This will leave Java processes running. Use Task Manager to close them if needed.

:: Keep the script running
:loop
timeout /t 10 /nobreak > nul
goto loop
