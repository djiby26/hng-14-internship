# ─────────────────────────────────────────────
# Stage 1 — dependency installation
# ─────────────────────────────────────────────
FROM node:20-alpine AS deps

WORKDIR /app

# Copy manifests only — maximises layer cache hit rate
COPY package.json package-lock.json ./

# Install production deps only; no devDependencies end up in the final image
RUN npm ci --omit=dev


# ─────────────────────────────────────────────
# Stage 2 — lean runtime image
# ─────────────────────────────────────────────
FROM node:20-alpine AS runtime

WORKDIR /app

# Create a non-root user/group before copying anything in
RUN addgroup -S appgroup \
 && adduser  -S appuser -G appgroup

# Bring in only what the app needs at runtime
COPY --from=deps /app/node_modules ./node_modules
COPY app.js        ./
COPY views/        ./views/

# Hand ownership to the unprivileged user
RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 3000

# Probe the /health endpoint; fall back to wget (always present in alpine)
HEALTHCHECK --interval=30s --timeout=5s --start-period=15s --retries=3 \
    CMD wget -qO- http://localhost:3000/health || exit 1

CMD ["node", "app.js"]