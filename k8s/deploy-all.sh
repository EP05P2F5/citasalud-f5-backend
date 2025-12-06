#!/bin/bash

# Script para desplegar toda la aplicación en Kubernetes

set -e  # Salir si hay errores

echo "🚀 Iniciando despliegue de PQRS Backend en Kubernetes..."

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Función para mensajes
log_info() {
    echo -e "${GREEN}✓${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}⚠${NC} $1"
}

log_error() {
    echo -e "${RED}✗${NC} $1"
}

# 1. Verificar que kubectl está disponible
if ! command -v kubectl &> /dev/null; then
    log_error "kubectl no está instalado"
    exit 1
fi
log_info "kubectl encontrado"

# 2. Crear namespace
log_warn "Creando namespace..."
kubectl apply -f namespace.yaml
log_info "Namespace creado"

# 3. Advertencia sobre secrets
log_warn "IMPORTANTE: Asegúrate de haber actualizado secret.yaml con credenciales reales"
read -p "¿Has actualizado los secrets? (y/N): " confirm
if [[ ! $confirm =~ ^[Yy]$ ]]; then
    log_error "Por favor actualiza secret.yaml antes de continuar"
    exit 1
fi

# 4. Aplicar secrets
log_warn "Aplicando secrets..."
kubectl apply -f secret.yaml
log_info "Secrets aplicados"

# 5. Aplicar ConfigMap
log_warn "Aplicando ConfigMap..."
kubectl apply -f configmap.yaml
log_info "ConfigMap aplicado"

# 6. Desplegar aplicación
log_warn "Desplegando aplicación..."
kubectl apply -f deployment.yaml
log_info "Deployment creado"

# 7. Crear services
log_warn "Creando services..."
kubectl apply -f service.yaml
log_info "Services creados"

# 8. Configurar HPA
log_warn "Configurando autoescalado..."
kubectl apply -f hpa.yaml
log_info "HPA configurado"

# 9. Ingress (opcional)
read -p "¿Deseas configurar Ingress? (y/N): " ingress_confirm
if [[ $ingress_confirm =~ ^[Yy]$ ]]; then
    kubectl apply -f ingress.yaml
    log_info "Ingress configurado"
fi

# 10. ServiceMonitor (opcional)
if kubectl get crd servicemonitors.monitoring.coreos.com &> /dev/null; then
    read -p "¿Deseas configurar ServiceMonitor para Prometheus? (y/N): " monitor_confirm
    if [[ $monitor_confirm =~ ^[Yy]$ ]]; then
        kubectl apply -f servicemonitor.yaml
        log_info "ServiceMonitor configurado"
    fi
else
    log_warn "Prometheus Operator no detectado, saltando ServiceMonitor"
fi

# 11. Esperar a que los pods estén listos
log_warn "Esperando a que los pods estén listos..."
kubectl wait --for=condition=ready pod -l app=pqrs-backend -n citasalud --timeout=300s
log_info "Pods listos"

# 12. Mostrar estado
echo ""
log_info "=== ESTADO DEL DESPLIEGUE ==="
kubectl get all -n citasalud

echo ""
log_info "=== LOGS DE LA APLICACIÓN ==="
kubectl logs -n citasalud -l app=pqrs-backend --tail=20

echo ""
log_info "✅ Despliegue completado exitosamente"
echo ""
echo "📝 Comandos útiles:"
echo "  - Ver logs: kubectl logs -n citasalud -l app=pqrs-backend -f"
echo "  - Ver pods: kubectl get pods -n citasalud"
echo "  - Port forward: kubectl port-forward -n citasalud svc/pqrs-backend-service 8080:80"
echo ""
