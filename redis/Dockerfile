# Purpose: Define a custom image for Redis using a custom configuration file.

# 1. Base Image: Start from the official Redis image (using the smaller 'alpine' version).
FROM redis:7-alpine

# 2. Set working directory for configuration files
WORKDIR /usr/local/etc/redis/

# 3. Copy Custom Configuration:
# Copy the local redis.conf file into the container at the expected path.
COPY redis.conf .

# 4. Define the Command:
# The default CMD for Redis is often just 'redis-server'.
# We explicitly override it to ensure Redis starts using our custom configuration file.
# The command runs Redis in the foreground (daemonize no) as required by Docker.
CMD [ "redis-server", "/usr/local/etc/redis/redis.conf" ]