package si.fri.rso.albify.imageservice.services.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Map;

@Provider
@Authenticate
public class AuthenticationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        System.out.println("Attempting to read header string");
        String authToken = ctx.getHeaderString("Authorization");
        System.out.println("Setting userId to null");
        String userId = null;

        System.out.println("checking token existence");
        if (authToken != null) {
            System.out.println("Trying decode");
            try {
                Algorithm algorithm = Algorithm.HMAC256("secret");
                JWTVerifier verifier = JWT
                        .require(algorithm)
                        .withSubject("AUTHENTICATION")
                        .build();
                DecodedJWT jwt = verifier.verify(authToken);

                Map<String, Claim> claims = jwt.getClaims();
                System.out.println("Parsing userId claim");
                userId = claims.get("userId").asString();

            } catch (JWTDecodeException exception) {
                System.out.println("Caught exception, setting userId to null");
                userId = null;
            }
        }

        System.out.println("checking user existence");
        if (userId == null) {

            System.out.println("User is null, aborting with unathorized");
            ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
        System.out.println("Setting user id");
        ctx.setProperty("userId", userId);
    }
}