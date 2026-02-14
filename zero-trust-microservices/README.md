# Zero-Trust Microservices (Spring Boot + Keycloak + Istio)

This project demonstrates:
- External HTTPS/TLS termination at an API Gateway
- OAuth2/OIDC with JWT validation in gateway and services
- Istio sidecar mesh with `STRICT` mTLS for internal traffic

## Architecture

```text
Client (curl/Postman)
   |
   | HTTPS :8443
   v
[Spring Cloud Gateway]
- TLS termination
- JWT validation (Resource Server)
- Forwards Authorization header downstream
   |
   | east-west via Istio sidecars (mTLS STRICT)
   v
[catalog-service] [order-service] [inventory-service]
- Resource Server JWT validation
- role-based endpoint security
   |
   v
[Keycloak in-cluster]
- realm/users/roles/clients
- OIDC metadata + JWKS
```

## Stack Versions
- Java: `21`
- Spring Boot: `3.5.8`
- Spring Cloud BOM: `2025.0.1`
- Istio: `1.28.3`
- Keycloak: `26.5.3`
- Kubernetes: Docker Desktop Kubernetes (`docker-desktop` context)

## Folder Layout

```text
zero-trust-microservices/
  gateway/
  catalog-service/
  order-service/
  inventory-service/
  infra/
    k8s/
      namespace.yaml
      istio/peerauthentication-strict.yaml
      keycloak/{configmap-realm.yaml,deployment.yaml,service.yaml}
      gateway/{secret-tls.yaml,deployment.yaml,service.yaml}
      services/{catalog.yaml,order.yaml,inventory.yaml}
    keycloak/zerotrust-realm.json
    certs/gateway-keystore.p12
  docker/keycloak/docker-compose.yaml
  README.md
```

## Running Checklist
- [x] Project scaffolding and source code created
- [x] Realm export and Kubernetes manifests created
- [x] TLS keystore generated and secret manifest populated
- [x] All four apps built with `mvnd`
- [x] Docker images built
- [ ] Docker Desktop Kubernetes enabled and context set
- [ ] Istio installed
- [ ] Manifests applied and pods healthy
- [ ] End-to-end verification completed

## 0) Prerequisites
- Docker Desktop running
- Java 21 installed
- `kubectl` installed
- `mvnd` installed

Install `istioctl` (PowerShell):

```powershell
$ver='1.28.3'
$zip="$env:TEMP\istio-$ver-win.zip"
Invoke-WebRequest -Uri "https://github.com/istio/istio/releases/download/$ver/istio-$ver-win.zip" -OutFile $zip
Expand-Archive -Path $zip -DestinationPath $env:USERPROFILE -Force
$env:Path="$env:USERPROFILE\istio-$ver\bin;$env:Path"
istioctl version
```

## 1) Enable Docker Desktop Kubernetes

In Docker Desktop UI:
1. Settings -> Kubernetes
2. Enable Kubernetes / Create cluster
3. Apply & Restart

Then run:

```powershell
kubectl config get-contexts
kubectl config use-context docker-desktop
kubectl cluster-info
kubectl get nodes -o wide
```

## 2) Build Apps and Docker Images

```powershell
cd d:\spring_boot_advanced_demos\zero-trust-microservices\catalog-service
mvnd clean package -DskipTests
docker build -t zerotrust/catalog-service:local .

cd d:\spring_boot_advanced_demos\zero-trust-microservices\inventory-service
mvnd clean package -DskipTests
docker build -t zerotrust/inventory-service:local .

cd d:\spring_boot_advanced_demos\zero-trust-microservices\order-service
mvnd clean package -DskipTests
docker build -t zerotrust/order-service:local .

cd d:\spring_boot_advanced_demos\zero-trust-microservices\gateway
mvnd clean package -DskipTests
docker build -t zerotrust/gateway:local .
```

## 3) Install Istio + Namespace Security

```powershell
istioctl install --set profile=default -y

cd d:\spring_boot_advanced_demos\zero-trust-microservices
kubectl apply -f infra\k8s\namespace.yaml
kubectl apply -f infra\k8s\istio\peerauthentication-strict.yaml
```

## 4) Deploy Keycloak

```powershell
cd d:\spring_boot_advanced_demos\zero-trust-microservices
kubectl apply -f infra\k8s\keycloak\configmap-realm.yaml
kubectl apply -f infra\k8s\keycloak\service.yaml
kubectl apply -f infra\k8s\keycloak\deployment.yaml
kubectl rollout status deploy/keycloak -n zerotrust --timeout=240s
```

Ensure local hostname alignment (for issuer consistency):
- Add this line to Windows hosts file (`C:\Windows\System32\drivers\etc\hosts`):

```text
127.0.0.1 keycloak.zerotrust.svc.cluster.local
```

Port-forward Keycloak:

```powershell
kubectl -n zerotrust port-forward svc/keycloak 8080:8080
```

## 5) Deploy Services and Gateway

```powershell
cd d:\spring_boot_advanced_demos\zero-trust-microservices
kubectl apply -f infra\k8s\services\catalog.yaml
kubectl apply -f infra\k8s\services\inventory.yaml
kubectl apply -f infra\k8s\services\order.yaml

kubectl apply -f infra\k8s\gateway\secret-tls.yaml
kubectl apply -f infra\k8s\gateway\service.yaml
kubectl apply -f infra\k8s\gateway\deployment.yaml

kubectl get pods -n zerotrust -w
```

Port-forward gateway HTTPS:

```powershell
kubectl -n zerotrust port-forward svc/gateway 8443:8443
```

## 6) Verification

### 6.1 Get USER token from Keycloak

```powershell
$tokenResp = Invoke-RestMethod -Method Post `
  -Uri "http://keycloak.zerotrust.svc.cluster.local:8080/realms/zerotrust/protocol/openid-connect/token" `
  -ContentType "application/x-www-form-urlencoded" `
  -Body "grant_type=password&client_id=postman-client&username=alice&password=alice123"
$USER_TOKEN = $tokenResp.access_token
$USER_TOKEN.Substring(0,30)
```

### 6.2 Get ADMIN token

```powershell
$adminResp = Invoke-RestMethod -Method Post `
  -Uri "http://keycloak.zerotrust.svc.cluster.local:8080/realms/zerotrust/protocol/openid-connect/token" `
  -ContentType "application/x-www-form-urlencoded" `
  -Body "grant_type=password&client_id=postman-client&username=admin&password=admin123"
$ADMIN_TOKEN = $adminResp.access_token
$ADMIN_TOKEN.Substring(0,30)
```

### 6.3 `200` with token

```powershell
curl.exe -k -i -H "Authorization: Bearer $USER_TOKEN" https://localhost:8443/api/catalog/items
```

### 6.4 `401` without token

```powershell
curl.exe -k -i https://localhost:8443/api/catalog/items
```

### 6.5 `403` USER on admin endpoint

```powershell
curl.exe -k -i -H "Authorization: Bearer $USER_TOKEN" https://localhost:8443/api/catalog/admin/report
```

### 6.6 `200` ADMIN on admin endpoint

```powershell
curl.exe -k -i -H "Authorization: Bearer $ADMIN_TOKEN" https://localhost:8443/api/catalog/admin/report
```

### 6.7 Order endpoint (`ROLE_USER`/`ROLE_ADMIN`) and internal S2S call to inventory

```powershell
curl.exe -k -i -X POST https://localhost:8443/api/orders/ `
  -H "Authorization: Bearer $USER_TOKEN" `
  -H "Content-Type: application/json" `
  -d '{"sku":"SKU-1","quantity":2}'
```

### 6.8 Prove STRICT mTLS blocks non-mesh pod

```powershell
kubectl create namespace plain
kubectl run curl-plain -n plain --image=curlimages/curl --restart=Never --command -- sleep 3600
kubectl wait --for=condition=Ready pod/curl-plain -n plain --timeout=120s
kubectl exec -n plain curl-plain -- curl -s -o /dev/null -w "%{http_code}`n" http://catalog-service.zerotrust.svc.cluster.local:8080/internal/catalog/items
```

Expected: connection failure or non-200 because namespace `plain` has no sidecar and target namespace enforces `STRICT` mTLS.

## APIs
- `GET /api/catalog/items` -> authenticated
- `GET /api/catalog/admin/report` -> `ROLE_ADMIN`
- `GET /api/inventory/stock/{sku}` -> authenticated
- `POST /api/orders/` -> `ROLE_USER` or `ROLE_ADMIN`

## Troubleshooting (paste exact logs)

If anything fails, run and share exact output:

```powershell
kubectl get pods -n zerotrust -o wide
kubectl describe pod <pod-name> -n zerotrust
kubectl logs <pod-name> -n zerotrust --tail=200
kubectl logs <pod-name> -n zerotrust -c istio-proxy --tail=200
```

Common checks:
- Issuer mismatch: confirm token `iss` equals `http://keycloak.zerotrust.svc.cluster.local:8080/realms/zerotrust`
- DNS/hosts: ensure hosts file entry exists for local token requests
- Sidecar: verify pods include `istio-proxy` container
- Gateway TLS: verify secret mounted and keystore path `/etc/certs/gateway-keystore.p12`

## Interview Statement

As a Spring Boot microservices dev, I mainly worked with TLS/HTTPS (and mTLS internally) plus OAuth2/OIDC with JWT for API security and authorization in Spring Security.
