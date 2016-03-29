package com.adi.ho.jackie.bubblestocks.oauth;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.github.scribejava.core.oauth.OAuthService;

/**
 * Created by JHADI on 3/25/16.
 */
public class TradeKingClient
{
    private static final String CONSUMER_KEY = "JYnluVt22VtggCnIBF7l2IdPHCYksVjSesxqmEZ3QQw0";
    private static final String CONSUMER_SECRET = "17AAEKoz1xNG0Zpb0a2aGGDF73eugAu8gcCXyiaIfVc6";
    private static final String OAUTH_TOKEN = "cys3k9gY0eoMMMHZc2wyjAQHrWdysb05qvPUbnTff947";
    private static final String OAUTH_TOKEN_SECRET = "bi3ZL9zBtjdJDbld1E8yHB07e9EZSKZyStRT4jacgzY7";

    private static final String PROTECTED_RESOURCE_URL = "https://api.tradeking.com/v1/market/news/search.xml?symbols=aapl";

    public void run(String marketSymbol){
        OAuthService service = new ServiceBuilder()
                .apiKey(CONSUMER_KEY)
                .apiSecret(CONSUMER_SECRET)
                .build(new TradeKingApi());
        OAuth1AccessToken accessToken = new OAuth1AccessToken(OAUTH_TOKEN, OAUTH_TOKEN_SECRET);

        // Now let's go and ask for a protected resource!
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL,service);
        Response response = request.send();
        System.out.println(response.getBody());
    }
}