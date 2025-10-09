FROM nginx:alpine
COPY ./public /usr/share/nginx/html
EXPOSE 8081
RUN printf "server { listen 8081; root /usr/share/nginx/html; index index.html; try_files \$uri /index.html; }" > /etc/nginx/conf.d/default.conf
CMD ["nginx", "-g", "daemon off;"]
