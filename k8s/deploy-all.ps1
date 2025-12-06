# Script PowerShell para desplegar toda la aplicación en Kubernetes

$ErrorActionPreference = "Stop"

Write-Host "🚀 Iniciando despliegue de PQRS Backend en Kubernetes..." -ForegroundColor Cyan

function Log-Info {
    param($Message)
    Write-Host "✓ $Message" -ForegroundColor Green
}

function Log-Warn {
    param($Message)
    Write-Host "⚠ $Message" -ForegroundColor Yellow
}

function Log-Error {
    param($Message)
    Write-Host "✗ $Message" -ForegroundColor Red
}

# 1. Verificar que kubectl está disponible
try {
    kubectl version --client --short | Out-Null
    Log-Info "kubectl encontrado"
} catch {
    Log-Error "kubectl no está instalado"
    exit 1
}

# 2. Crear namespace
Log-Warn "Creando namespace..."
kubectl apply -f namespace.yaml
Log-Info "Namespace creado"

# 3. Advertencia sobre secrets
Log-Warn "IMPORTANTE: Asegúrate de haber actualizado secret.yaml con credenciales reales"
$confirm = Read-Host "¿Has actualizado los secrets? (y/N)"
if ($confirm -notmatch '^[Yy]$') {
    Log-Error "Por favor actualiza secret.yaml antes de continuar"
    exit 1
}

# 4. Aplicar secrets
Log-Warn "Aplicando secrets..."
kubectl apply -f secret.yaml
Log-Info "Secrets aplicados"

# 5. Aplicar ConfigMap
Log-Warn "Aplicando ConfigMap..."
kubectl apply -f configmap.yaml
Log-Info "ConfigMap aplicado"

# 6. Desplegar aplicación
Log-Warn "Desplegando aplicación..."
kubectl apply -f deployment.yaml
Log-Info "Deployment creado"

# 7. Crear services
Log-Warn "Creando services..."
kubectl apply -f service.yaml
Log-Info "Services creados"

# 8. Configurar HPA
Log-Warn "Configurando autoescalado..."
kubectl apply -f hpa.yaml
Log-Info "HPA configurado"

# 9. Ingress (opcional)
$ingress_confirm = Read-Host "¿Deseas configurar Ingress? (y/N)"
if ($ingress_confirm -match '^[Yy]$') {
    kubectl apply -f ingress.yaml
    Log-Info "Ingress configurado"
}

# 10. ServiceMonitor (opcional)
try {
    kubectl get crd servicemonitors.monitoring.coreos.com | Out-Null
    $monitor_confirm = Read-Host "¿Deseas configurar ServiceMonitor para Prometheus? (y/N)"
    if ($monitor_confirm -match '^[Yy]$') {
        kubectl apply -f servicemonitor.yaml
        Log-Info "ServiceMonitor configurado"
    }
} catch {
    Log-Warn "Prometheus Operator no detectado, saltando ServiceMonitor"
}

# 11. Esperar a que los pods estén listos
Log-Warn "Esperando a que los pods estén listos..."
kubectl wait --for=condition=ready pod -l app=pqrs-backend -n citasalud --timeout=300s
Log-Info "Pods listos"

# 12. Mostrar estado
Write-Host ""
Log-Info "=== ESTADO DEL DESPLIEGUE ==="
kubectl get all -n citasalud

Write-Host ""
Log-Info "=== LOGS DE LA APLICACIÓN ==="
kubectl logs -n citasalud -l app=pqrs-backend --tail=20

Write-Host ""
Log-Info "✅ Despliegue completado exitosamente"
Write-Host ""
Write-Host "📝 Comandos útiles:" -ForegroundColor Cyan
Write-Host "  - Ver logs: kubectl logs -n citasalud -l app=pqrs-backend -f"
Write-Host "  - Ver pods: kubectl get pods -n citasalud"
Write-Host "  - Port forward: kubectl port-forward -n citasalud svc/pqrs-backend-service 8080:80"
Write-Host ""
