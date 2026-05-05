# ===== STAGE 1: BUILD =====
FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm ci --silent

COPY . .
# VITE_API_URL se inyecta en build-time via ARG
ARG VITE_API_URL=http://localhost:8080
ENV VITE_API_URL=${VITE_API_URL}
RUN npm run build

# ===== STAGE 2: RUNTIME (Nginx) =====
FROM nginx:alpine

# Configuración de Nginx para SPA (React Router)
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
