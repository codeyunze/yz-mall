redis.call('DEL', KEYS[1])
redis.call('RPUSH', KEYS[1], unpack(ARGV))
redis.call('EXPIRE', KEYS[1], 86400)
return true