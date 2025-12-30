# Deployment Guide

This guide provides step-by-step instructions for deploying the Code-Learn platform in different environments.

## Table of Contents
- [Local Development](#local-development)
- [Docker Deployment](#docker-deployment)
- [Kubernetes Deployment](#kubernetes-deployment)
- [AWS Production Deployment](#aws-production-deployment)
- [CI/CD Setup](#cicd-setup)

## Local Development

### Prerequisites
- Java 17 or higher
- Maven 3.8+
- PostgreSQL 15
- Redis 7
- RabbitMQ 3

### Setup Steps

1. **Clone the repository**
```bash
git clone https://github.com/HARSHAVARDHINI-11/Code-Learn.git
cd Code-Learn
```

2. **Start infrastructure services**
```bash
# Start PostgreSQL
docker run -d --name postgres -p 5432:5432 \
  -e POSTGRES_PASSWORD=postgres postgres:15-alpine

# Start Redis
docker run -d --name redis -p 6379:6379 redis:7-alpine

# Start RabbitMQ
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 \
  rabbitmq:3-management-alpine
```

3. **Create databases**
```bash
docker exec -it postgres psql -U postgres -c "CREATE DATABASE codelearn_users;"
docker exec -it postgres psql -U postgres -c "CREATE DATABASE codelearn_posts;"
docker exec -it postgres psql -U postgres -c "CREATE DATABASE codelearn_groups;"
docker exec -it postgres psql -U postgres -c "CREATE DATABASE codelearn_contests;"
docker exec -it postgres psql -U postgres -c "CREATE DATABASE codelearn_notifications;"
```

4. **Build all services**
```bash
mvn clean package -DskipTests
```

5. **Run services**

Open separate terminals for each service:

```bash
# Terminal 1: API Gateway
cd api-gateway && mvn spring-boot:run

# Terminal 2: User Service
cd user-service && mvn spring-boot:run

# Terminal 3: Post Service
cd post-service && mvn spring-boot:run

# Terminal 4: Group Service
cd group-service && mvn spring-boot:run

# Terminal 5: Contest Service
cd contest-service && mvn spring-boot:run

# Terminal 6: Notification Service
cd notification-service && mvn spring-boot:run
```

6. **Verify deployment**
```bash
# Check API Gateway
curl http://localhost:8080/actuator/health

# Check User Service
curl http://localhost:8081/actuator/health

# Check all services
for port in 8080 8081 8082 8083 8084 8085; do
  echo "Checking port $port:"
  curl -s http://localhost:$port/actuator/health | jq .
done
```

## Docker Deployment

### Prerequisites
- Docker 20.10+
- Docker Compose 2.0+

### Setup Steps

1. **Build all services**
```bash
mvn clean package -DskipTests
```

2. **Build Docker images**
```bash
# Build all images
docker-compose build

# Or build individually
docker build -t codelearn/api-gateway:latest ./api-gateway
docker build -t codelearn/user-service:latest ./user-service
docker build -t codelearn/post-service:latest ./post-service
docker build -t codelearn/group-service:latest ./group-service
docker build -t codelearn/contest-service:latest ./contest-service
docker build -t codelearn/notification-service:latest ./notification-service
```

3. **Start all services**
```bash
docker-compose up -d
```

4. **View logs**
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f user-service
```

5. **Verify deployment**
```bash
docker-compose ps
```

6. **Stop all services**
```bash
docker-compose down
```

7. **Clean up volumes**
```bash
docker-compose down -v
```

## Kubernetes Deployment

### Prerequisites
- Kubernetes cluster (minikube, EKS, GKE, AKS)
- kubectl CLI configured
- Docker registry access

### Local Kubernetes (Minikube)

1. **Start Minikube**
```bash
minikube start --cpus=4 --memory=8192
```

2. **Enable ingress**
```bash
minikube addons enable ingress
minikube addons enable metrics-server
```

3. **Build and load images**
```bash
# Build images
mvn clean package -DskipTests
docker-compose build

# Load into minikube
minikube image load codelearn/api-gateway:latest
minikube image load codelearn/user-service:latest
minikube image load codelearn/post-service:latest
minikube image load codelearn/group-service:latest
minikube image load codelearn/contest-service:latest
minikube image load codelearn/notification-service:latest
```

4. **Deploy to Kubernetes**
```bash
# Create namespace
kubectl create namespace codelearn

# Deploy infrastructure
kubectl apply -f k8s/postgres-deployment.yml -n codelearn
kubectl apply -f k8s/redis-deployment.yml -n codelearn
kubectl apply -f k8s/rabbitmq-deployment.yml -n codelearn

# Wait for infrastructure to be ready
kubectl wait --for=condition=ready pod -l app=postgres -n codelearn --timeout=120s
kubectl wait --for=condition=ready pod -l app=redis -n codelearn --timeout=120s
kubectl wait --for=condition=ready pod -l app=rabbitmq -n codelearn --timeout=120s

# Deploy services
kubectl apply -f k8s/api-gateway-deployment.yml -n codelearn
kubectl apply -f k8s/user-service-deployment.yml -n codelearn
kubectl apply -f k8s/post-service-deployment.yml -n codelearn
kubectl apply -f k8s/group-service-deployment.yml -n codelearn
kubectl apply -f k8s/contest-service-deployment.yml -n codelearn
kubectl apply -f k8s/notification-service-deployment.yml -n codelearn

# Deploy HPA
kubectl apply -f k8s/hpa.yml -n codelearn
```

5. **Verify deployment**
```bash
# Check pods
kubectl get pods -n codelearn

# Check services
kubectl get services -n codelearn

# Check HPA
kubectl get hpa -n codelearn

# View logs
kubectl logs -f deployment/user-service -n codelearn
```

6. **Access services**
```bash
# Port forward API Gateway
kubectl port-forward service/api-gateway-service 8080:80 -n codelearn

# Or get minikube service URL
minikube service api-gateway-service -n codelearn --url
```

### Production Kubernetes

For production deployment on managed Kubernetes (EKS, GKE, AKS):

1. **Push images to registry**
```bash
# Tag images
docker tag codelearn/api-gateway:latest your-registry/api-gateway:v1.0.0
docker tag codelearn/user-service:latest your-registry/user-service:v1.0.0
# ... tag all other services

# Push to registry
docker push your-registry/api-gateway:v1.0.0
docker push your-registry/user-service:v1.0.0
# ... push all other services
```

2. **Update Kubernetes manifests**
Update image references in k8s/*.yml files to use your registry.

3. **Create secrets**
```bash
# Create database secret
kubectl create secret generic postgres-secret \
  --from-literal=username=your-username \
  --from-literal=password=your-password \
  -n codelearn

# Create other secrets as needed
```

4. **Deploy to production**
```bash
kubectl apply -f k8s/ -n codelearn
```

## AWS Production Deployment

### Prerequisites
- AWS CLI configured
- eksctl installed
- kubectl configured
- Terraform (optional)

### Setup Steps

1. **Create VPC and networking**
```bash
aws ec2 create-vpc --cidr-block 10.0.0.0/16 --tag-specifications 'ResourceType=vpc,Tags=[{Key=Name,Value=codelearn-vpc}]'

# Create subnets in multiple AZs
aws ec2 create-subnet --vpc-id <vpc-id> --cidr-block 10.0.1.0/24 --availability-zone us-east-1a
aws ec2 create-subnet --vpc-id <vpc-id> --cidr-block 10.0.2.0/24 --availability-zone us-east-1b
aws ec2 create-subnet --vpc-id <vpc-id> --cidr-block 10.0.3.0/24 --availability-zone us-east-1c
```

2. **Create EKS cluster**
```bash
eksctl create cluster \
  --name codelearn-cluster \
  --region us-east-1 \
  --nodegroup-name standard-workers \
  --node-type t3.medium \
  --nodes 3 \
  --nodes-min 2 \
  --nodes-max 10 \
  --managed \
  --vpc-cidr 10.0.0.0/16
```

3. **Create RDS instance**
```bash
aws rds create-db-instance \
  --db-instance-identifier codelearn-postgres \
  --db-instance-class db.t3.medium \
  --engine postgres \
  --engine-version 15 \
  --master-username admin \
  --master-user-password <secure-password> \
  --allocated-storage 100 \
  --storage-type gp3 \
  --multi-az \
  --backup-retention-period 7 \
  --db-subnet-group-name codelearn-db-subnet
```

4. **Create ElastiCache cluster**
```bash
aws elasticache create-cache-cluster \
  --cache-cluster-id codelearn-redis \
  --cache-node-type cache.t3.medium \
  --engine redis \
  --num-cache-nodes 1 \
  --az-mode cross-az
```

5. **Create S3 buckets**
```bash
aws s3 mb s3://codelearn-static-assets --region us-east-1
aws s3 mb s3://codelearn-backups --region us-east-1
aws s3 mb s3://codelearn-user-uploads --region us-east-1
```

6. **Setup AWS Load Balancer Controller**
```bash
# Install AWS Load Balancer Controller
kubectl apply -k "github.com/aws/eks-charts/stable/aws-load-balancer-controller//crds?ref=master"
helm install aws-load-balancer-controller eks/aws-load-balancer-controller \
  -n kube-system \
  --set clusterName=codelearn-cluster
```

7. **Deploy application**
```bash
kubectl apply -f k8s/
```

8. **Configure DNS**
```bash
# Get ALB DNS name
kubectl get ingress -n codelearn

# Create Route53 record pointing to ALB
aws route53 change-resource-record-sets \
  --hosted-zone-id <zone-id> \
  --change-batch file://dns-change.json
```

## CI/CD Setup

### Jenkins Setup

1. **Install Jenkins plugins**
- Docker Pipeline
- Kubernetes CLI
- AWS Steps
- SonarQube Scanner
- Email Extension

2. **Configure credentials**
- Docker registry credentials
- AWS credentials
- Kubernetes config
- SonarQube token

3. **Create Jenkins pipeline**
```bash
# Create new pipeline job in Jenkins
# Point to Jenkinsfile in repository
# Configure webhooks for automatic triggers
```

4. **Configure environment variables**
```bash
DOCKER_REGISTRY=your-registry
AWS_REGION=us-east-1
EKS_CLUSTER_NAME=codelearn-cluster
SONAR_HOST_URL=http://sonarqube:9000
```

### GitHub Actions Alternative

Create `.github/workflows/deploy.yml`:

```yaml
name: Deploy to Production

on:
  push:
    branches: [main]

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
          
      - name: Build with Maven
        run: mvn clean package -DskipTests
        
      - name: Build Docker images
        run: docker-compose build
        
      - name: Push to registry
        run: |
          docker login -u ${{ secrets.DOCKER_USERNAME }} -p ${{ secrets.DOCKER_PASSWORD }}
          docker-compose push
          
      - name: Deploy to Kubernetes
        run: |
          aws eks update-kubeconfig --name codelearn-cluster
          kubectl apply -f k8s/
```

## Troubleshooting

### Common Issues

1. **Service won't start**
```bash
# Check logs
kubectl logs -f deployment/service-name

# Check events
kubectl describe pod pod-name
```

2. **Database connection issues**
```bash
# Verify database is accessible
kubectl exec -it pod-name -- psql -h postgres-service -U postgres

# Check connection string in config
kubectl get configmap
```

3. **Out of memory**
```bash
# Increase memory limits in deployment
kubectl edit deployment service-name

# Check current usage
kubectl top pods
```

4. **Circuit breaker always open**
```bash
# Check service health
curl http://service:port/actuator/health

# Review circuit breaker metrics
curl http://service:port/actuator/metrics/resilience4j.circuitbreaker
```

## Monitoring

### Setup Prometheus and Grafana

```bash
# Install Prometheus
helm install prometheus prometheus-community/kube-prometheus-stack

# Access Grafana
kubectl port-forward service/prometheus-grafana 3000:80

# Default credentials: admin/prom-operator
```

### Setup CloudWatch Monitoring

```bash
# Install CloudWatch agent
kubectl apply -f https://raw.githubusercontent.com/aws-samples/amazon-cloudwatch-container-insights/latest/k8s-deployment-manifest-templates/deployment-mode/daemonset/container-insights-monitoring/quickstart/cwagent-fluentd-quickstart.yaml
```

## Backup and Recovery

### Database Backup

```bash
# Manual backup
kubectl exec -it postgres-pod -- pg_dump -U postgres codelearn_users > backup.sql

# Automated backups (AWS RDS)
aws rds create-db-snapshot \
  --db-instance-identifier codelearn-postgres \
  --db-snapshot-identifier codelearn-backup-$(date +%Y%m%d)
```

### Disaster Recovery

```bash
# Restore from backup
kubectl exec -it postgres-pod -- psql -U postgres codelearn_users < backup.sql

# Or restore RDS snapshot
aws rds restore-db-instance-from-db-snapshot \
  --db-instance-identifier codelearn-postgres-restored \
  --db-snapshot-identifier codelearn-backup-20231220
```

## Security Best Practices

1. **Use secrets for sensitive data**
2. **Enable network policies**
3. **Use RBAC for access control**
4. **Enable pod security policies**
5. **Regular security scanning**
6. **Keep dependencies updated**
7. **Use TLS/SSL everywhere**
8. **Enable audit logging**

## Performance Tuning

1. **JVM tuning**: Adjust heap size based on workload
2. **Connection pools**: Tune pool sizes for optimal performance
3. **Cache tuning**: Adjust TTL and eviction policies
4. **Database indexing**: Create indexes for frequent queries
5. **Auto-scaling**: Configure appropriate thresholds
