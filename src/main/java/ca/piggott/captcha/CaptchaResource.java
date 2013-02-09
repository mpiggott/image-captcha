package ca.piggott.captcha;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

@Path("captcha")
public class CaptchaResource {

	private LoadingCache<String, Captcha> cache;

	public CaptchaResource(String[] terms, String key, String cx)
	{
		cache = CacheBuilder.newBuilder().build(new CaptchaLoader(terms, key, cx));
	}

	@GET
	public String newInstance()
	{
		return UUID.randomUUID().toString();
	}

	@GET
	@Path("{captchaId}")
	public Response getImage(@PathParam("captchaId") String captchaId)
			throws ExecutionException
	{
		Captcha captcha = cache.get(captchaId);
		return Response.ok(captcha.getImage(), captcha.getType()).build();
	}

	@POST
	@Path("{captchaId}")
	public Response validate(@PathParam("captchaId") String captchaId, Guess guess)
			throws Exception
	{
		if (guess.getTerm() != null && guess.getTerm().equals(cache.get(captchaId).getTerm())) {
			return Response.ok().build();
		} else {
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}
	}
}
