#!/bin/bash
# Cross-platform script to start microservices and generate random transactions
# Works on Unix/Linux with bash and on Windows with Git Bash or WSL

# Function to detect the operating system
detect_os() {
    case "$(uname -s)" in
        Darwin*)
            echo "macos"
            ;;
        Linux*|CYGWIN*|MINGW*|MSYS*)
            echo "unix"
            ;;
        *)
            echo "windows"
            ;;
    esac
}

OS=$(detect_os)
echo "Detected OS: $OS"

# No need to set Maven commands globally as we execute them directly in the service directory

# Function to start a service in background
start_service() {
    local service_name=$1
    local service_dir=$3
    
    echo "Starting $service_name..."
    cd "$service_dir" || exit 1
    
    # Make sure the Maven wrapper is executable
    chmod +x mvnw 2>/dev/null
    
    if [ "$OS" = "unix" ] || [ "$OS" = "macos" ]; then
        nohup ./mvnw spring-boot:run > "$service_name.log" 2>&1 &
        echo "$!" > "$service_name.pid"
    else
        start /B cmd /c "mvnw.cmd spring-boot:run > $service_name.log 2>&1"
    fi
    
    cd - > /dev/null || exit 1
    echo "$service_name started"
}

# Function to generate a random account ID (compatible with macOS, Linux, and Windows with proper tools)
generate_account_id() {
    if [ "$(uname)" = "Darwin" ]; then
        # macOS approach
        echo "acc_$(LC_ALL=C tr -dc 'a-z0-9' < /dev/urandom | head -c 8)"
    else
        # Linux and Windows with proper tools approach
        echo "acc_$(cat /dev/urandom 2>/dev/null | tr -dc 'a-z0-9' | fold -w 8 | head -n 1 2>/dev/null || 
              LC_ALL=C tr -dc 'a-z0-9' < /dev/urandom | head -c 8 || 
              echo "$(date +%s)_$(($RANDOM % 1000))")"
    fi
}

# Function to generate a random amount between 1 and 100000
generate_amount() {
    echo $((RANDOM % 100000 + 1))
}

generate_description() {
  echo "transfer request"
}

currency() {
  echo "USD"
}

type() {
  echo "PAYMENT"
}

# Function to send a transaction request
send_transaction() {
    local to_account=$(generate_account_id)
    local amount=$(generate_amount)
    local type=$(type)
    local currency=$(currency)
    local description=$(generate_description)
    
    local json_data="{\"accountId\":\"$to_account\",\"amount\":$amount,\"currency\":\"$currency\",\"description\":\"$description\",\"type\":\"$type\"}"
    echo "Sending transaction: $json_data"
    
    curl -X POST "http://localhost:8080/api/transactions" \
         -H "Content-Type: application/json" \
         -d "$json_data"
    
    echo ""
}

# Function to get all transactions
get_all_transactions() {
    echo "Getting all transactions..."
    curl -X GET "http://localhost:8080/api/transactions"
    echo ""
}

# Function to clean up on exit
cleanup() {
    echo "Cleaning up and stopping services..."
    
    if [ -f "balances-service/balances-service.pid" ]; then
        kill "$(cat balances-service/balances-service.pid)" 2>/dev/null
        rm balances-service/balances-service.pid
    fi
    
    if [ -f "transaction-service/transaction-service.pid" ]; then
        kill "$(cat transaction-service/transaction-service.pid)" 2>/dev/null
        rm transaction-service/transaction-service.pid
    fi
    
    # For Windows, we need a different approach
    if [ "$OS" = "windows" ]; then
        taskkill /F /IM java.exe /T 2>/dev/null
    elif [ "$OS" = "macos" ] || [ "$OS" = "unix" ]; then
        # Additional cleanup for hanging processes on Unix/macOS
        pkill -f "spring-boot:run" 2>/dev/null || true
    fi
    
    echo "Services stopped"
    exit 0
}

# Register the cleanup function on script exit
trap cleanup EXIT

# Start both services
start_service "balances-service" "" "balances-service"
start_service "transaction-service" "" "transaction-service"

# Wait for services to start up
echo "Waiting for services to start up..."
sleep 10

# Send random transactions
echo "Starting to send random transactions..."
for i in {1..10}; do
    send_transaction
    # Random pause between 1-5 seconds
    sleep $((RANDOM % 5 + 1))
done

# Get all transactions
get_all_transactions

echo "Press Ctrl+C to stop the services and exit"
# Keep the script running to allow manual inspection
while true; do
    sleep 10
done
