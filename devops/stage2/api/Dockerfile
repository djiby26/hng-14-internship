# ─────────────────────────────────────────────
# Stage 1 — build / wheel compilation
# ─────────────────────────────────────────────
FROM python:3.12-slim AS builder

WORKDIR /build

# Install build tools only in this stage — they never reach the final image
RUN apt-get update \
 && apt-get install -y --no-install-recommends gcc \
 && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .

# Install into an isolated prefix so we can COPY the tree cleanly
RUN pip install --no-cache-dir --prefix=/deps -r requirements.txt


# ─────────────────────────────────────────────
# Stage 2 — lean runtime image
# ─────────────────────────────────────────────
FROM python:3.12-slim AS runtime

WORKDIR /app

# Non-root user
RUN addgroup --system appgroup \
 && adduser  --system --ingroup appgroup appuser

# Pull compiled packages from the builder stage
COPY --from=builder /deps /usr/local

COPY main.py .

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8000

# Use Python's built-in urllib so no extra dependency is needed for the probe
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
    CMD python -c \
        "import urllib.request, sys; \
         urllib.request.urlopen('http://localhost:8000/health'); \
         sys.exit(0)" \
    || exit 1

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]