redis.call('DEL', KEYS[1])
redis.call('RPUSH', KEYS[1], unpack(ARGV))
return true