# Use the official Nginx image as a base
FROM nginx:latest

# Remove any existing config files
RUN rm /etc/nginx/conf.d/default.conf

# Copy the custom Nginx configuration file into the image
COPY nginx.conf /etc/nginx/nginx.conf

# Expose port 80 for HTTP traffic
EXPOSE 80
