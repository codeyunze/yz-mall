# 授权服务

## 客户端应用通过授权服务登录流程
1. 用户登录客户端应用时，客户端应用直接跳转到授权服务的授权码申请地址
   ```http request
   http://localhost:20002/oauth/authorize?client_id=3xj&response_type=code&scope=all&redirect_uri=http://localhost:20002/main.html
   ```
   
   参数说明：
 
    | 参数         |  类型  | 备注                                                                                                                   |
    | ---- |----------------------------------------------------------------------------------------------------------------------|----------|
    | client_id | String | 应用客户端编码                                                                                                              |
    | redirect_uri | String | 重定向地址，验证通过后需要跳转的地址（也就是客户端应用的地址）                                                                                      |
    | response_type | String | 响应方式（**code**：验证通过后跳转的地址会携带一个授权码，可以通过授权码请求`/oauth/token`接口获取account_token令牌；**token**：验证通过后跳转的地址会直接携带account_token令牌；） |
    

2. 访问上面地址会跳转到授权服务的登录页面
3. 登录成功后会跳到授权服务提供的一个授权确认页面
4. 确认成功后，会跳转到先前指定的客户端应用地址，且客户端应用页面的url后面会带上code（授权码），如下：
   ```http request
   http://localhost:20002/main.html?code=5Zz85f
   ```
5. 然后客户端应用使用授权码请求授权服务的`/oauth/token`接口，获取account_token令牌
    


## 授权码模式相关接口信息

### 通过授权码获取account_token令牌

Request URL：

```http request
http://localhost:20002/oauth/token
```

Request Method：POST

Content-Type: form-data

Parameters:

| 参数  |  类型  | 备注      |
| ---- | ---- |---------|
| grant_type | String | 授权类型    |
| client_id | String | 应用客户端编码 |
| client_secret | String | 应用客户端密钥 |
| redirect_uri | String | 服务重定向地址 |
| code | String | 授权码     |

Response：
```json
{
    "access_token": "7b146909-aa44-4e90-b1c3-4165c75d416e",
    "token_type": "bearer",
    "refresh_token": "eca479f9-1490-449d-a0c7-ec1cdf2e4e6c",
    "expires_in": 7199,
    "scope": "all"
}
```

### 校验access_token令牌

Request URL:
```http request
http://localhost:20002/oauth/check_token
```

Request Method：POST

Content-Type: form-data

Parameters:

| 参数  |  类型  | 备注      |
| ---- | ---- |---------|
| token | String | access_token令牌    |

Response：
```json
{
    "aud": [
        "auth1",
        "auth2"
    ],
    "user_name": "admin",
    "scope": [
        "all"
    ],
    "active": true,
    "exp": 1662308542,
    "authorities": [
        "auth1",
        "auth2"
    ],
    "client_id": "3xj"
}
```
