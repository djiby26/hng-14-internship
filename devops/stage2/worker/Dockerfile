# ─────────────────────────────────────────────
# Stage 1 — build / wheel compilation
# ─────────────────────────────────────────────
FROM python:3.12-slim AS builder

WORKDIR /build

RUN apt-get update \
 && apt-get install -y --no-install-recommends gcc \
 && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .

RUN pip install --no-cache-dir --prefix=/deps -r requirements.txt


# ─────────────────────────────────────────────
# Stage 2 — lean runtime image
# ─────────────────────────────────────────────
FROM python:3.12-slim AS runtime

WORKDIR /app

# Non-root user
RUN addgroup --system appgroup \
 && adduser  --system --ingroup appgroup appuser

COPY --from=builder /deps /usr/local

COPY worker.py .

RUN chown -R appuser:appgroup /app

USER appuser

# Workers have no HTTP server, so the healthcheck verifies that:
#   1. the redis package is importable (deps are present), AND
#   2. we can actually reach / PING Redis — confirming the worker's
#      connectivity to its sole external dependency.
# REDIS_HOST and REDIS_PORT are injected by docker-compose at runtime.
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
    CMD python -c \
        "import redis, os, sys; \
         r = redis.Redis( \
             host=os.environ['REDIS_HOST'], \
             port=int(os.environ.get('REDIS_PORT', 6379)), \
             socket_connect_timeout=3); \
         r.ping(); \
         sys.exit(0)" \
    || exit 1

CMD ["python", "worker.py"]