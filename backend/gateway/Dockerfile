FROM nginx:alpine
RUN apk add --no-cache curl
RUN printf 'server { \
  listen 8080; \
  location /actuator/health { return 200 "{\"status\":\"UP\"}\n"; add_header Content-Type application/json; } \
}' > /etc/nginx/conf.d/default.conf
EXPOSE 8080
CMD ["nginx","-g","daemon off;"]
