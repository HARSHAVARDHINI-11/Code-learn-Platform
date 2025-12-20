#!/bin/bash

# Code-Learn Development Helper Script

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "=========================================="
echo "Code-Learn Development Helper"
echo "=========================================="

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check prerequisites
check_prerequisites() {
    echo -e "${YELLOW}Checking prerequisites...${NC}"
    
    if ! command_exists java; then
        echo -e "${RED}Java is not installed. Please install Java 17 or higher.${NC}"
        exit 1
    fi
    
    if ! command_exists mvn; then
        echo -e "${RED}Maven is not installed. Please install Maven 3.8+.${NC}"
        exit 1
    fi
    
    if ! command_exists docker; then
        echo -e "${RED}Docker is not installed. Please install Docker.${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}All prerequisites are met!${NC}"
}

# Build all services
build_services() {
    echo -e "${YELLOW}Building all services...${NC}"
    mvn clean package -DskipTests
    echo -e "${GREEN}Build completed successfully!${NC}"
}

# Start infrastructure services
start_infrastructure() {
    echo -e "${YELLOW}Starting infrastructure services (PostgreSQL, Redis, RabbitMQ)...${NC}"
    docker-compose up -d postgres redis rabbitmq
    
    echo -e "${YELLOW}Waiting for services to be ready...${NC}"
    sleep 10
    
    echo -e "${GREEN}Infrastructure services started!${NC}"
    echo -e "PostgreSQL: localhost:5432"
    echo -e "Redis: localhost:6379"
    echo -e "RabbitMQ: localhost:5672, Management UI: http://localhost:15672"
}

# Stop infrastructure services
stop_infrastructure() {
    echo -e "${YELLOW}Stopping infrastructure services...${NC}"
    docker-compose down
    echo -e "${GREEN}Infrastructure services stopped!${NC}"
}

# Start all services with Docker Compose
start_all_services() {
    echo -e "${YELLOW}Building and starting all services with Docker Compose...${NC}"
    
    # Build first
    build_services
    
    # Start all services
    docker-compose up -d
    
    echo -e "${YELLOW}Waiting for services to be ready...${NC}"
    sleep 30
    
    echo -e "${GREEN}All services started!${NC}"
    echo -e "API Gateway: http://localhost:8080"
    echo -e "User Service: http://localhost:8081"
    echo -e "Post Service: http://localhost:8082"
    echo -e "Group Service: http://localhost:8083"
    echo -e "Contest Service: http://localhost:8084"
    echo -e "Notification Service: http://localhost:8085"
}

# Stop all services
stop_all_services() {
    echo -e "${YELLOW}Stopping all services...${NC}"
    docker-compose down
    echo -e "${GREEN}All services stopped!${NC}"
}

# Check service health
check_health() {
    echo -e "${YELLOW}Checking service health...${NC}"
    
    services=("8080:API Gateway" "8081:User Service" "8082:Post Service" "8083:Group Service" "8084:Contest Service" "8085:Notification Service")
    
    for service in "${services[@]}"; do
        port="${service%%:*}"
        name="${service##*:}"
        
        if curl -sf http://localhost:$port/actuator/health > /dev/null; then
            echo -e "${GREEN}✓ $name (port $port) is healthy${NC}"
        else
            echo -e "${RED}✗ $name (port $port) is not responding${NC}"
        fi
    done
}

# View logs
view_logs() {
    echo -e "${YELLOW}Viewing logs for all services...${NC}"
    docker-compose logs -f
}

# Clean up
cleanup() {
    echo -e "${YELLOW}Cleaning up...${NC}"
    mvn clean
    docker-compose down -v
    echo -e "${GREEN}Cleanup completed!${NC}"
}

# Show menu
show_menu() {
    echo ""
    echo "Select an option:"
    echo "1) Check prerequisites"
    echo "2) Build all services"
    echo "3) Start infrastructure only (PostgreSQL, Redis, RabbitMQ)"
    echo "4) Start all services with Docker Compose"
    echo "5) Stop infrastructure services"
    echo "6) Stop all services"
    echo "7) Check service health"
    echo "8) View logs"
    echo "9) Clean up everything"
    echo "0) Exit"
    echo ""
}

# Main menu loop
while true; do
    show_menu
    read -p "Enter your choice: " choice
    
    case $choice in
        1)
            check_prerequisites
            ;;
        2)
            build_services
            ;;
        3)
            start_infrastructure
            ;;
        4)
            start_all_services
            ;;
        5)
            stop_infrastructure
            ;;
        6)
            stop_all_services
            ;;
        7)
            check_health
            ;;
        8)
            view_logs
            ;;
        9)
            cleanup
            ;;
        0)
            echo -e "${GREEN}Goodbye!${NC}"
            exit 0
            ;;
        *)
            echo -e "${RED}Invalid option. Please try again.${NC}"
            ;;
    esac
done
