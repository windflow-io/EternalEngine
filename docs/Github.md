# Eternal Engine Github API
Please note the following important paths for the Eternal Engine Github API below.  
```properties
github_start_url: 'http://<your-domain>/api/auth/github',
github_token_exchange Url: 'http://<your-domain>/api/auth/github/exchange'
```
In README.md you would have read that you need to create a openid.development.properties as such:
```properties
eternalengine.auth.github_client_id=from_github
eternalengine.auth.github_client_secret=from_github
eternalengine.auth.github_auth_domain=<your-domain>
```
1. Visit: Github > User Home > Settings > OpenID Apps to create an app
2. Fill in the openid.development.properties as specified
3. When you want to login, create a link to the Eternal Engine github API extension as per 'github start url' as mentioned above via JavaScript or HTML.
4. The api will do all the redirects and at the end of the day the user will end up at the same page they started at.
5. The final redirect back to the page you started at will create a cookie with a life of 30 seconds called tokenExchange
6. Make a REST call to the service, appending the exchange token (that you read from the cookie) to the url, eg:

    `http://<your-domain>/api/auth/github/exchange/EXCHANGE_TOKEN_FROM_COOKE`

7. The REST request will return a json response with a token key-value pair, eg:

    ```json
    {
      "token": "1A2B3C4D.1A2B3C.1A2B3C4D"
    }
    ```

8. There are 2 periods in the token. Take the characters between the two keys and Base64 decode them to get user credentials. (Note, the token is signed, so EternalEngine will reject any changes to the token)

