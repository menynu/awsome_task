FROM python:3.9-slim

WORKDIR /app

# Copy the Python script from GitHub into the Docker image
COPY main.py .

# Run the Python script
CMD ["python", "main.py"]