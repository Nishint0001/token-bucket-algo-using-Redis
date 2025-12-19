-- Redis key for a specific user / API / client
local key = KEYS[1]

-- Maximum tokens allowed in the bucket
local capacity = tonumber(ARGV[1])

-- Token refill rate (tokens added per second)
local refill_rate = tonumber(ARGV[2])

-- Current timestamp (usually epoch seconds or millis)
local now = tonumber(ARGV[3])

-- Fetch existing token count and last updated timestamp from Redis
local data = redis.call("HMGET", key, "tokens", "timestamp")

-- If tokens don't exist, initialize with full capacity
local tokens = tonumber(data[1]) or capacity

-- If timestamp doesn't exist, use current time
local last_time = tonumber(data[2]) or now

-- Time difference since last request
local delta = math.max(0, now - last_time)

-- Calculate how many tokens should be refilled
local refill = delta * refill_rate

-- Add refilled tokens but do not exceed capacity
tokens = math.min(capacity, tokens + refill)

-- Flag to tell whether request is allowed
local allowed = 0

-- If at least 1 token is available, allow request
if tokens >= 1 then
    tokens = tokens - 1
    allowed = 1
end

-- Save updated token count and timestamp back to Redis
redis.call("HMSET", key,
  "tokens", tokens,
  "timestamp", now
)

-- Set expiry so unused keys auto-clean from Redis
-- TTL = time required to fully refill the bucket
redis.call("EXPIRE", key, math.ceil(capacity / refill_rate))

-- Return 1 if request allowed, else 0
return allowed
