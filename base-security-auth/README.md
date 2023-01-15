# 授权服务

## 授权码模式相关接口信息

### 通过授权码获取account_token令牌

```http request
http://localhost:20002/oauth/token
```

入参：

| 字段  |  类型  | 备注       |
| ---- | ---- |----------|
| grant_type | String | 授权类型     |
| client_id | String | 应用客户端比编码 |
| client_secret | String | 应用客户端密钥  |
| redirect_uri | String | 服务重定向地址  |
| code | String | 授权码      |

