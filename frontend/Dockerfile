
# ----------------Build stage -------------
FROM node:20-alpine AS build
WORKDIR /app


#Install deps (dev deps needed for vite)
COPY package*.json ./
RUN npm ci --include=dev

# ---Copy source and build
COPY . .
RUN npm run build

#---------runtime stage (nginx) --------------
FROM nginx:alpine

#Copy built assets from vite (dist) to nginx html root
COPY --from=build /app/dist/ /usr/share/nginx/html

# Single nginx config listening on 8081 (SPA fallback)
RUN cat > /etc/nginx/conf.d/default.conf <<'NGINX'
server {
  listen 8081;
  root /usr/share/nginx/html;
  index index.html;

  location / {
    try_files $uri /index.html;
  }
}
NGINX



EXPOSE 8081
CMD ["nginx", "-g", "daemon off;"]
