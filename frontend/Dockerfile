
# ----------------Build stage -------------
FROM node:20-alpine AS build
WORKDIR /app


#----Faster installs by caching deps----
COPY packgae*.json ./
RUN if [ -f package.json ]; then npm ci; else echo "No package.json yet"; fi

# ---Copy source and build (handles vite, CRA, Next static export)

Copy . .

# For vite /cra this creates /app/dist or /app/build Run build (ignore if build script missing)

RUN if [ -f package.json ]; then \

      npm run build || echo "No build script yet"; \
    else \
      echo "No package.json to build"; \
     fi

#---------runtime stage (nginx) --------------
FROM nginx:alpine
RUN printf "server { \
    listen 8081; \
    root /usr/share/nginx/html; \
    index index.html; \
    location / { try_files \$uri /index.html; } \
    }" > /etc/nginx/conf.d/default.conf

CMD ["nginx", "-g", "daemon off;"]

EXPOSE 8081
RUN printf "server { listen 8081; root /usr/share/nginx/html; index index.html; try_files \$uri /index.html; }" > /etc/nginx/conf.d/default.conf
CMD ["nginx", "-g", "daemon off;"]
