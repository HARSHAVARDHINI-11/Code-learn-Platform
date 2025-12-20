# AWS Infrastructure Setup for Code-Learn Platform

## Overview
This document describes the AWS infrastructure setup for the Code-Learn educational platform, designed to achieve sub-second response times, zero-downtime deployments, and automated quality control.

## AWS Services Used

### 1. Compute (EC2 + EKS)
- **Amazon EKS (Elastic Kubernetes Service)**: Managed Kubernetes cluster for microservices orchestration
- **EC2 Instances**: Worker nodes for EKS cluster
  - Instance Type: t3.medium or m5.large for production
  - Auto Scaling enabled with min 2, max 10 nodes

### 2. Load Balancing
- **Application Load Balancer (ALB)**: 
  - Routes traffic to API Gateway service
  - Health checks for high availability
  - SSL/TLS termination
  - Path-based routing

### 3. Auto Scaling
- **Auto Scaling Groups (ASG)**:
  - Configured for EC2 worker nodes
  - Scaling policies based on CPU and memory utilization
  - Target tracking scaling policy: 70% CPU utilization
  - Kubernetes HPA for pod-level autoscaling

### 4. Database
- **Amazon RDS (PostgreSQL)**:
  - Multi-AZ deployment for high availability
  - Automated backups with 7-day retention
  - Read replicas for read-heavy workloads
  - Instance: db.t3.medium (can scale to db.r5.large)
  - Storage: 100GB GP3 with auto-scaling enabled

### 5. Caching
- **Amazon ElastiCache (Redis)**:
  - Cluster mode enabled for horizontal scaling
  - Multi-AZ with automatic failover
  - Node type: cache.t3.medium
  - Sub-millisecond latency for cached data

### 6. Storage
- **Amazon S3**:
  - Static assets storage
  - User uploads and attachments
  - Backup storage for databases
  - Lifecycle policies for cost optimization
  - Versioning enabled for critical buckets

### 7. Messaging
- **Amazon MQ (RabbitMQ)** or **Amazon MSK (Kafka)**:
  - Message broker for event-driven architecture
  - Asynchronous communication between services

### 8. Monitoring & Logging
- **Amazon CloudWatch**:
  - Metrics collection and monitoring
  - Log aggregation from all services
  - Alarms for critical metrics
- **AWS X-Ray**: Distributed tracing for performance analysis

### 9. Security
- **AWS IAM**: Role-based access control
- **AWS Secrets Manager**: Secure credential storage
- **Security Groups**: Network-level security
- **AWS WAF**: Web application firewall for ALB

## Architecture Diagram

```
                    ┌─────────────────┐
                    │   Route 53      │
                    │   (DNS)         │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │   CloudFront    │
                    │   (CDN)         │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │      ALB        │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │   API Gateway   │
                    │   (EKS Pod)     │
                    └────────┬────────┘
                             │
            ┌────────────────┼────────────────┐
            │                │                │
    ┌───────▼──────┐  ┌─────▼──────┐  ┌─────▼──────┐
    │User Service  │  │Post Service│  │Group Service│
    │  (EKS Pod)   │  │ (EKS Pod)  │  │  (EKS Pod)  │
    └───────┬──────┘  └─────┬──────┘  └─────┬──────┘
            │                │                │
            └────────────────┼────────────────┘
                             │
            ┌────────────────┼────────────────┐
            │                │                │
    ┌───────▼──────┐  ┌─────▼──────┐  ┌─────▼──────┐
    │   RDS        │  │ElastiCache │  │  Amazon MQ │
    │ (PostgreSQL) │  │  (Redis)   │  │ (RabbitMQ) │
    └──────────────┘  └────────────┘  └────────────┘
```

## Deployment Steps

### Step 1: Create VPC and Networking
```bash
aws ec2 create-vpc --cidr-block 10.0.0.0/16
aws ec2 create-subnet --vpc-id <vpc-id> --cidr-block 10.0.1.0/24 --availability-zone us-east-1a
aws ec2 create-subnet --vpc-id <vpc-id> --cidr-block 10.0.2.0/24 --availability-zone us-east-1b
```

### Step 2: Create EKS Cluster
```bash
eksctl create cluster \
  --name codelearn-cluster \
  --region us-east-1 \
  --nodegroup-name standard-workers \
  --node-type t3.medium \
  --nodes 3 \
  --nodes-min 2 \
  --nodes-max 10 \
  --managed
```

### Step 3: Create RDS Instance
```bash
aws rds create-db-instance \
  --db-instance-identifier codelearn-postgres \
  --db-instance-class db.t3.medium \
  --engine postgres \
  --engine-version 15 \
  --master-username admin \
  --master-user-password <password> \
  --allocated-storage 100 \
  --storage-type gp3 \
  --multi-az \
  --backup-retention-period 7
```

### Step 4: Create ElastiCache Cluster
```bash
aws elasticache create-cache-cluster \
  --cache-cluster-id codelearn-redis \
  --cache-node-type cache.t3.medium \
  --engine redis \
  --num-cache-nodes 1 \
  --az-mode cross-az
```

### Step 5: Create S3 Buckets
```bash
aws s3 mb s3://codelearn-static-assets
aws s3 mb s3://codelearn-backups
aws s3 mb s3://codelearn-user-uploads
```

### Step 6: Deploy Application to EKS
```bash
kubectl apply -f k8s/
```

### Step 7: Create Application Load Balancer
```bash
# ALB is automatically created by Kubernetes Ingress Controller
kubectl apply -f k8s/ingress.yml
```

## Performance Optimizations

1. **Sub-Second Response Times**:
   - Redis caching for frequently accessed data
   - Database query optimization with proper indexing
   - Connection pooling for database connections
   - CDN for static content delivery

2. **Zero-Downtime Deployments**:
   - Rolling updates in Kubernetes
   - Health checks and readiness probes
   - Blue-green deployment strategy
   - Canary releases for gradual rollouts

3. **Auto-Scaling**:
   - Horizontal Pod Autoscaler (HPA) for pods
   - Cluster Autoscaler for nodes
   - Target tracking policies based on metrics
   - Scheduled scaling for predictable traffic patterns

## Cost Optimization

1. Use Spot Instances for non-critical workloads
2. Enable S3 lifecycle policies for old data
3. Use Reserved Instances for predictable workloads
4. Implement auto-scaling to reduce over-provisioning
5. Use CloudWatch metrics to identify unused resources

## Security Best Practices

1. Enable encryption at rest for RDS and S3
2. Enable encryption in transit (TLS/SSL)
3. Use AWS Secrets Manager for sensitive credentials
4. Implement least privilege IAM policies
5. Enable VPC Flow Logs for network monitoring
6. Use AWS WAF to protect against common attacks
7. Regular security audits and patch management

## Monitoring and Alerting

1. CloudWatch dashboards for real-time monitoring
2. Alarms for critical metrics (CPU, memory, errors)
3. Log aggregation in CloudWatch Logs
4. Distributed tracing with AWS X-Ray
5. Regular performance reviews and optimization

## Disaster Recovery

1. Automated backups for RDS (7-day retention)
2. Cross-region replication for S3
3. Multi-AZ deployment for high availability
4. Regular disaster recovery drills
5. Documented recovery procedures
